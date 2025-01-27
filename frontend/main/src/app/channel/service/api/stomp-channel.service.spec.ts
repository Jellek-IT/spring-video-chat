import { TestBed } from '@angular/core/testing';

import { StompChannelService } from './stomp-channel.service';

describe('StompChannelService', () => {
  let service: StompChannelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StompChannelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
