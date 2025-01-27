import { TestBed } from '@angular/core/testing';
import { MemberChannelMessageService } from './member-channel-message.service';

describe('MemberChannelMessageService', () => {
  let service: MemberChannelMessageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MemberChannelMessageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
