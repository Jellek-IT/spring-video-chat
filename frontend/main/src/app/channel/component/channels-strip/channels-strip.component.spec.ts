import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChannelsStripComponent } from './channels-strip.component';

describe('ChannelsStripComponent', () => {
  let component: ChannelsStripComponent;
  let fixture: ComponentFixture<ChannelsStripComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChannelsStripComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChannelsStripComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
