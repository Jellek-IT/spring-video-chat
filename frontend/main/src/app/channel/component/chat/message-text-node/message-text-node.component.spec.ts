import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageTextNodeComponent } from './message-text-node.component';

describe('MessageTextNodeComponent', () => {
  let component: MessageTextNodeComponent;
  let fixture: ComponentFixture<MessageTextNodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageTextNodeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessageTextNodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
