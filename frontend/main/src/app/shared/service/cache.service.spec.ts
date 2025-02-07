import { fakeAsync, TestBed, tick } from '@angular/core/testing';

import { CacheRequestSettings, CacheService } from './cache.service';
import { HttpRequest } from '@angular/common/http';

describe('CacheService', () => {
  let service: CacheService;
  let settings: CacheRequestSettings;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CacheService);
    settings = { url: /api\/[0-9]+\/test/, ttl: 1 };
    service.addRequestSettings(settings);
  });

  it('should add request settings', () => {
    expect(
      service.getRequestSettings(new HttpRequest('GET', 'api/99/test'))
    ).toEqual(settings);
  });

  it('should return undefined for non-GET requests', () => {
    const req = new HttpRequest('POST', 'api/99/test', {});
    expect(service.getRequestSettings(req)).toBeUndefined();
  });

  it('should retrieve a previous request if it exists and is valid', () => {
    const req = new HttpRequest('GET', 'api/99/test', {});
    const requestDetails = service.addRequest(req, settings);
    expect(service.getPreviousRequest(req)).toEqual(requestDetails);
  });

  it('should clear expired requests periodically', fakeAsync(() => {
    const req = new HttpRequest('GET', 'api/99/test', {});
    service.addRequest(req, settings);
    expect(service.getPreviousRequest(req)).toBeTruthy();
    tick(2000);
    expect(service.getPreviousRequest(req)).toBeUndefined();
  }));
});
