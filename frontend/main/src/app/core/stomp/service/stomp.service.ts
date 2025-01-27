import { inject, Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../../../environments/environment';
import {
  Client,
  IMessage,
  IStompSocket,
  StompSubscription,
} from '@stomp/stompjs';
import {
  BehaviorSubject,
  filter,
  map,
  Observable,
  share,
  Subject,
  Subscription,
} from 'rxjs';
import SockJS from 'sockjs-client';
import { StompConnectionState } from '../enum/internal/stomp-state.enum';
import { v4 as uuidv4 } from 'uuid';
import { StompResponse } from '../model/stomp-response.model';
import { ExceptionResponse } from '../../../error/model/exception-response.model';
import { StompResponseType } from '../enum/stomp-response-type.enum';

export interface PublishArgs {
  transactionId?: string;
}

interface StompResponseWithReceipt<T> {
  receiptId?: string;
  response: StompResponse<T>;
}

export class StompSubscriptionAccepted {}

@Injectable({
  providedIn: 'root',
})
export class StompService {
  private readonly debug = false;
  private readonly bearerTokenHeaderName = 'Authorization';
  private readonly bearerTokenPrefix = 'bearer';
  private readonly destinationPrefix = '/app/';
  private readonly appTransactionHeader = 'X-Transaction-Id';
  private readonly reconnectDelay = 3000;
  private readonly subscribeTimeoutTime = 5000;
  private readonly connectionState$ = new BehaviorSubject<StompConnectionState>(
    StompConnectionState.DEACTIVATED
  );
  private readonly errors$ =
    new Subject<StompResponseWithReceipt<ExceptionResponse> | null>();

  private keycloak = inject(Keycloak);
  private client: Client | null = null;
  private errorQueueSubscription?: Subscription;

  public connect(): void {
    this._connect();
  }

  private async _connect(): Promise<void> {
    this.connectionState$.next(StompConnectionState.ACTIVATING);
    await this.keycloak.updateToken();
    if (!this.keycloak.authenticated) {
      this.keycloak.login({ redirectUri: environment.url });
    }
    if (this.client !== null) {
      await this.client.deactivate({ force: true });
    }
    if (!this.isActivated()) {
      return;
    }
    this.client = new Client({
      connectHeaders: {
        [this.bearerTokenHeaderName]: `${this.bearerTokenPrefix} ${this.keycloak
          .token!}`,
      },
      // for new versions of chrome task schedulers are running every one minute
      // what can cause connection close for lower values
      heartbeatIncoming: 60000,
      heartbeatOutgoing: 60000,
      // manual reconnect so kc token can be refreshed
      reconnectDelay: 0,
      discardWebsocketOnCommFailure: true,
      onConnect: () => {
        this.errorQueueSubscription = this.subscribeInternal<ExceptionResponse>(
          '/user/queue/errors'
        ).subscribe({
          next: (res) => {
            if (res instanceof StompSubscriptionAccepted) {
              this.connectionState$.next(StompConnectionState.ACTIVATED);
            } else {
              this.errors$.next(res);
            }
          },
          error: () => {
            this.client?.deactivate({ force: true });
          },
        });
      },
      debug: (data) => {
        if (this.debug) {
          console.log(data);
        }
      },
      // will be upgraded automatically to ws when possible
      webSocketFactory: () => {
        return new SockJS(environment.sockJsUrl) as IStompSocket;
      },
      onStompError: (frame) => {
        const content = frame.body != null ? JSON.parse(frame.body) : null;
        this.errors$.next(content);
      },
      onUnhandledMessage: (frame) => {
        const content: StompResponse<any> =
          frame.body != null ? JSON.parse(frame.body) : null;
        if (content.responseType === StompResponseType.ERROR) {
          this.errors$.next({
            response: content,
            receiptId: frame.headers['receipt-id'],
          });
        }
      },
      onWebSocketClose: () => {
        this?.errorQueueSubscription?.unsubscribe();
        if (!this.isActivated()) {
          return;
        }
        this.reconnect();
      },
    });
    this.client.activate();
  }

  private reconnect() {
    this.connectionState$.next(StompConnectionState.ACTIVATING);
    if (this.client !== null) {
      this.client.debug(
        `STOMP: scheduling reconnection in ${this.reconnectDelay}ms`
      );
    }
    setTimeout(() => {
      if (!this.isActivated()) {
        return;
      }
      if (this.client !== null) {
        this.client.debug(
          `STOMP: scheduling reconnection in ${this.reconnectDelay}ms`
        );
      }
      this._connect();
    }, this.reconnectDelay);
  }

  public isActivated(): boolean {
    return [
      StompConnectionState.ACTIVATING,
      StompConnectionState.ACTIVATED,
    ].includes(this.connectionState$.getValue());
  }

  public getConnectionStateAsObservable() {
    return this.connectionState$.asObservable();
  }

  public disconnect(): void {
    if (this.client === null) {
      this.connectionState$.next(StompConnectionState.DEACTIVATED);
      return;
    }
    if (this.isActivated()) {
      this.connectionState$.next(StompConnectionState.DEACTIVATING);
      this.client
        .deactivate()
        .then(() =>
          this.connectionState$.next(StompConnectionState.DEACTIVATED)
        );
    }
    this.client = null;
  }

  private getClient(): Client {
    if (this.client === null || !this.isActivated()) {
      throw Error('Stomp client is not activated');
    }
    return this.client;
  }

  private subscribeInternal<T>(
    destination: string
  ): Observable<StompResponseWithReceipt<T> | StompSubscriptionAccepted> {
    let subscribeStompSubscription: StompSubscription | undefined;
    let errorSubscription: Subscription;
    let subscribeTimeout: ReturnType<typeof setTimeout>;
    let hasReceipt = false;

    return new Observable<
      StompResponseWithReceipt<T> | StompSubscriptionAccepted
    >((observer) => {
      const receipt = uuidv4();
      const headers = {
        receipt,
      };
      errorSubscription = this.errors$.subscribe((error) => {
        if (error?.receiptId === receipt) {
          observer.error(error);
        }
      });
      this.getClient().watchForReceipt(receipt, () => {
        hasReceipt = true;
        clearTimeout(subscribeTimeout);
        observer.next(new StompSubscriptionAccepted());
      });
      subscribeTimeout = setTimeout(
        () =>
          observer.error(
            new Error(`Server did not respond for subscription ${destination}`)
          ),
        this.subscribeTimeoutTime
      );
      subscribeStompSubscription = this.getClient().subscribe(
        destination,
        (message: IMessage) => {
          const result = {
            response: { ...JSON.parse(message.body) },
            receipt: message.headers['receipt'],
          };
          observer.next(result);
        },
        headers
      );

      return () => {
        if (this.isActivated() && hasReceipt) {
          subscribeStompSubscription?.unsubscribe();
        }
        errorSubscription.unsubscribe();
        clearTimeout(subscribeTimeout);
      };
    }).pipe(share());
  }

  public subscribe<T>(
    destination: string
  ): Observable<StompResponse<T> | StompSubscriptionAccepted> {
    return this.subscribeInternal<T>(destination).pipe(
      map((response) =>
        response instanceof StompSubscriptionAccepted
          ? response
          : response.response
      )
    );
  }

  public publishJson<T>(destination: string, data: T, args: PublishArgs = {}) {
    const headers: Record<string, any> = {
      'Content-Type': 'application/json',
    };
    /** custom receipt for messages associated with this SENT command
     * so that corresponding message or error can be find
     */
    if (args.transactionId !== undefined) {
      headers[this.appTransactionHeader] = args.transactionId;
    }
    this.getClient().publish({
      destination: this.destinationPrefix + destination,
      headers,
      body: JSON.stringify(data),
    });
  }

  public getErrorAsObservable(): Observable<StompResponse<ExceptionResponse>> {
    return this.errors$.asObservable().pipe(
      filter((res) => res !== null),
      map((res) => res.response)
    );
  }
}
