import { TestBed } from '@angular/core/testing';

import { JanusVideoRoomService } from './janus-video-room.service';

describe('JanusVideoRoomService', () => {
  let service: JanusVideoRoomService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JanusVideoRoomService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
