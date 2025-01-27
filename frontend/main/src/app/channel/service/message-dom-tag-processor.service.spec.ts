import { TestBed } from '@angular/core/testing';

import { MessageDomTagProcessorService } from './message-dom-tag-processor.service';

describe('MessageDomTagProcessorService', () => {
  let service: MessageDomTagProcessorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessageDomTagProcessorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
