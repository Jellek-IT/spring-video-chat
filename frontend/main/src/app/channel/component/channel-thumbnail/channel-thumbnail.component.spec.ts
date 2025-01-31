import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChannelThumbnailComponent } from './channel-thumbnail.component';

describe('ChannelThumbnailComponent', () => {
  let component: ChannelThumbnailComponent;
  let fixture: ComponentFixture<ChannelThumbnailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChannelThumbnailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChannelThumbnailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
