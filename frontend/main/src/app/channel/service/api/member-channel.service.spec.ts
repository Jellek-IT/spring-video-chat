import { TestBed } from '@angular/core/testing';

import { MemberChannelService } from './member-channel.service';

describe('MemberChannelService', () => {
  let service: MemberChannelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MemberChannelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
