import { TestBed } from '@angular/core/testing';

import { EndpointErrorService } from './endpoint-error.service';

describe('EndpointErrorService', () => {
  let service: EndpointErrorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EndpointErrorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
