import { HttpEvent, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { interval, Observable, Subject } from 'rxjs';

export interface CacheRequestDetails {
  response: HttpEvent<unknown> | null;
  response$: Subject<HttpResponse<unknown>>;
  expireAt: Date;
}

export interface CacheRequestSettings {
  url: RegExp;
  ttl: number;
}

@Injectable({
  providedIn: 'root',
})
export class CacheService {
  private requests: Map<string, CacheRequestDetails> = new Map();
  private requestsSettings: CacheRequestSettings[] = [];

  constructor() {
    interval(1000).subscribe(() => this.clearExpiredRequests());
  }

  public addRequestSettings(settings: CacheRequestSettings) {
    this.requestsSettings.push(settings);
  }

  public getRequestSettings(
    req: HttpRequest<unknown>
  ): CacheRequestSettings | undefined {
    if (req.method !== 'GET') {
      return undefined;
    }
    return this.requestsSettings.find((requestSettings) =>
      requestSettings.url.test(req.url)
    );
  }

  public getPreviousRequest(
    req: HttpRequest<unknown>
  ): CacheRequestDetails | undefined {
    const key = this.getUniqueKey(req);
    const previousRequest = this.requests.get(key);
    if (previousRequest === undefined) {
      return undefined;
    }
    if (previousRequest.expireAt < new Date()) {
      this.requests.delete(key);
      return undefined;
    }
    return previousRequest;
  }

  public addRequest(
    req: HttpRequest<unknown>,
    settings: CacheRequestSettings
  ): CacheRequestDetails {
    const key = this.getUniqueKey(req);
    const expireAt = new Date();
    expireAt.setSeconds(expireAt.getSeconds() + settings.ttl);
    const previousRequest: CacheRequestDetails = {
      response: null,
      response$: new Subject(),
      expireAt,
    };
    this.requests.set(key, previousRequest);
    return previousRequest;
  }

  public deleteRequest(req: HttpRequest<unknown>) {
    const key = this.getUniqueKey(req);
    this.requests.delete(key);
  }

  private clearExpiredRequests() {
    const now = new Date();
    const requestsEntries = this.requests.entries();
    for (const [key, request] of requestsEntries) {
      if (request.expireAt < now) {
        this.requests.delete(key);
      }
    }
  }

  public getUniqueKey(req: HttpRequest<unknown>): string {
    if (req.method !== 'GET') {
      throw Error('Other methods than GET are not supported');
    }
    return `${req.url};${req.params.toString()}`;
  }
}
