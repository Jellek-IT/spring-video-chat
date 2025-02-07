import { fakeAsync, TestBed, tick } from '@angular/core/testing';

import { StompService, StompSubscriptionAccepted } from './stomp.service';
import { StompClientFactory } from './stomp-client.factory';
import {
  Client,
  IFrame,
  IMessage,
  StompConfig,
  StompHeaders,
} from '@stomp/stompjs';
import { StompConnectionState } from '../enum/internal/stomp-state.enum';
import Keycloak from 'keycloak-js';
import { StompResponse } from '../model/stomp-response.model';
import { StompResponseType } from '../enum/stomp-response-type.enum';

describe('StompService', () => {
  const testConnectedFrame: IFrame = {
    command: 'CONNECTED',
    headers: new StompHeaders(),
    isBinaryBody: false,
    body: '',
    binaryBody: new Uint8Array(),
  };
  const testReceiptFrame: IFrame = {
    command: 'RECEIPT',
    headers: new StompHeaders(),
    isBinaryBody: false,
    body: '',
    binaryBody: new Uint8Array(),
  };
  const message = (response: StompResponse<any>): IMessage => ({
    ack: (_) => {},
    nack: (_) => {},
    command: '',
    headers: new StompHeaders(),
    isBinaryBody: false,
    body: JSON.stringify(response),
    binaryBody: new Uint8Array(),
  });
  const errorResponse: StompResponse<any> = {
    responseType: StompResponseType.ERROR,
    data: 'error',
  };
  const sampleResponse: StompResponse<any> = {
    responseType: StompResponseType.MESSAGE,
    data: 'message',
  };
  const subscriptionDestination = 'test';
  let service: StompService;
  let stompClientFactoryMock: jasmine.SpyObj<StompClientFactory>;
  let stompClientMock: jasmine.SpyObj<Client>;
  let keycloakMock: jasmine.SpyObj<Keycloak>;

  beforeEach(() => {
    stompClientFactoryMock = jasmine.createSpyObj<StompClientFactory>(
      'StompClientFactoryService',
      ['create']
    );
    stompClientMock = jasmine.createSpyObj<Client>('StompClient', [
      'activate',
      'subscribe',
      'watchForReceipt',
      'deactivate',
      'debug',
    ]);
    keycloakMock = jasmine.createSpyObj<Keycloak>('Keycloak', [
      'authenticated',
      'token',
      'updateToken',
    ]);
    stompClientMock.activate.and.identity;
    stompClientMock.debug.and.identity;
    keycloakMock.authenticated = true;
    keycloakMock.token = 'token';
    TestBed.configureTestingModule({
      providers: [
        StompService,
        { provide: StompClientFactory, useValue: stompClientFactoryMock },
        { provide: Keycloak, useValue: keycloakMock },
      ],
    });
    service = TestBed.inject(StompService);
  });

  it('should connect to client', fakeAsync(() => {
    stompClientMock.watchForReceipt.and.callFake((_, callback) => {
      setTimeout(() => callback(testReceiptFrame));
    });
    stompClientMock.subscribe.and.callFake((_1, _2) => {
      return {
        id: '',
        unsubscribe: () => {},
      };
    });
    stompClientFactoryMock.create.and.callFake((config) => {
      setTimeout(() => config!.onConnect!(testConnectedFrame));
      return stompClientMock;
    });
    expect(service.isActivated()).toBeFalse();
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.DEACTIVATED
    );
    service.connect();
    expect(service.isActivated()).toBeTrue();
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.ACTIVATING
    );
    tick(0);
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.ACTIVATED
    );
  }));

  it('should reconnect activated client on failure', fakeAsync(() => {
    stompClientMock.deactivate.and.identity;
    stompClientFactoryMock.create.and.callFake((config) => {
      setTimeout(() => config?.onWebSocketClose?.(null));
      return stompClientMock;
    });
    expect(service.isActivated()).toBeFalse();
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.DEACTIVATED
    );
    service.connect();
    expect(service.isActivated()).toBeTrue();
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.ACTIVATING
    );
    tick(0);
    expect(service.isActivated()).toBeTrue();
    expect(stompClientFactoryMock.create.calls.count()).toBe(1);
    tick(service['reconnectDelay'] + 1000);
    expect(service.isActivated()).toBeTrue();
    expect(stompClientFactoryMock.create.calls.count()).toBe(2);
  }));

  it('should not reconnect deactivated client', fakeAsync(() => {
    let stompConfig: StompConfig | undefined;
    stompClientMock.watchForReceipt.and.callFake((_, callback) => {
      setTimeout(() => callback(testReceiptFrame));
    });
    stompClientMock.subscribe.and.callFake((_1, _2) => {
      return {
        id: '',
        unsubscribe: () => {},
      };
    });
    stompClientMock.deactivate.and.callFake(() => {
      return new Promise((resolve) => {
        setTimeout(() => {
          setTimeout(() => stompConfig!.onWebSocketClose!(null));
          resolve();
        });
      });
    });
    stompClientFactoryMock.create.and.callFake((config) => {
      stompConfig = config;
      setTimeout(() => config!.onConnect!(testConnectedFrame));
      return stompClientMock;
    });
    service.connect();
    tick(0);
    expect(service.isActivated()).toBeTrue();
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.ACTIVATED
    );
    service.disconnect();
    expect(service.isActivated()).toBeFalse();
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.DEACTIVATING
    );
    tick(0);
    expect(service.isActivated()).toBeFalse();
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.DEACTIVATED
    );
    expect(stompClientFactoryMock.create.calls.count()).toBe(1);
    tick(service['reconnectDelay'] + 1000);
    expect(service.isActivated()).toBeFalse();
    expect(stompClientFactoryMock.create.calls.count()).toBe(1);
  }));

  it('should watch for error messages', fakeAsync(() => {
    const errorObservableSpy = jasmine.createSpy('observer');
    service.getErrorAsObservable().subscribe(errorObservableSpy);
    stompClientMock.watchForReceipt.and.callFake((_, callback) => {
      setTimeout(() => callback(testReceiptFrame));
    });
    stompClientMock.subscribe.and.callFake((_1, callback) => {
      setTimeout(() => callback(message(errorResponse)), 1000);
      return {
        id: '',
        unsubscribe: () => {},
      };
    });
    stompClientFactoryMock.create.and.callFake((config) => {
      setTimeout(() => config!.onConnect!(testConnectedFrame));
      return stompClientMock;
    });
    service.connect();
    tick(0);
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.ACTIVATED
    );
    expect(errorObservableSpy).toHaveBeenCalledTimes(0);
    tick(2000);
    expect(errorObservableSpy).toHaveBeenCalledTimes(1);
    expect(errorObservableSpy).toHaveBeenCalledWith(errorResponse);
  }));

  it('should create subscription', fakeAsync(() => {
    const subscriptionObservableSpy = jasmine.createSpy('observer');
    stompClientMock.watchForReceipt.and.callFake((_, callback) => {
      setTimeout(() => callback(testReceiptFrame));
    });
    stompClientMock.subscribe.and.callFake((destination, callback) => {
      if (destination === subscriptionDestination) {
        setTimeout(() => callback(message(sampleResponse)), 1000);
      }
      return {
        id: '',
        unsubscribe: () => {},
      };
    });
    stompClientFactoryMock.create.and.callFake((config) => {
      setTimeout(() => config!.onConnect!(testConnectedFrame));
      return stompClientMock;
    });
    service.connect();
    tick(0);
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.ACTIVATED
    );
    service
      .subscribe(subscriptionDestination)
      .subscribe(subscriptionObservableSpy);
    tick(0);
    expect(subscriptionObservableSpy).toHaveBeenCalledTimes(1);
    expect(subscriptionObservableSpy).toHaveBeenCalledWith(
      new StompSubscriptionAccepted()
    );
    tick(2000);
    expect(subscriptionObservableSpy).toHaveBeenCalledTimes(2);
    expect(subscriptionObservableSpy).toHaveBeenCalledWith(sampleResponse);
  }));

  it('should throw error when timeout occurred waiting for subscription', fakeAsync((
    done: DoneFn
  ) => {
    let subscriptionReceipt: string;
    stompClientMock.watchForReceipt.and.callFake((receipt, callback) => {
      setTimeout(() => {
        if (receipt !== subscriptionReceipt) {
          callback(testReceiptFrame);
        }
      });
    });
    stompClientMock.subscribe.and.callFake((destination, _, headers) => {
      console.log(headers);
      if (destination === subscriptionDestination) {
        subscriptionReceipt = headers!['receipt'];
      }
      return {
        id: '',
        unsubscribe: () => {},
      };
    });
    stompClientFactoryMock.create.and.callFake((config) => {
      setTimeout(() => config!.onConnect!(testConnectedFrame));
      return stompClientMock;
    });
    service.connect();
    tick(0);
    expect(service['connectionState$'].value).toEqual(
      StompConnectionState.ACTIVATED
    );
    service.subscribe(subscriptionDestination).subscribe({
      next: () => done.fail('excepted error'),
      error: (error) =>
        expect(error.message).toContain(
          `Server did not respond for subscription ${subscriptionDestination}`
        ),
    });
    tick(service['subscribeTimeoutTime'] + 1000);
  }));

  it('should not allow to subscribe when client is not connected', fakeAsync((
    done: DoneFn
  ) => {
    service.subscribe(subscriptionDestination).subscribe({
      next: () => done.fail('excepted error'),
      error: (error) =>
        expect(error.message).toContain('Stomp client is not activated'),
    });
  }));
});
