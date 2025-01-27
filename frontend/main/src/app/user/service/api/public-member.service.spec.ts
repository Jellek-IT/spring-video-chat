import { TestBed } from '@angular/core/testing';

import { PublicMemberService } from './public-member.service';

describe('PublicMemberService', () => {
  let service: PublicMemberService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PublicMemberService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
