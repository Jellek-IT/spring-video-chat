import { TestBed } from '@angular/core/testing';

import { StompVideoRoomService } from './stomp-video-room.service';

describe('StompVideoRoomService', () => {
  let service: StompVideoRoomService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StompVideoRoomService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
