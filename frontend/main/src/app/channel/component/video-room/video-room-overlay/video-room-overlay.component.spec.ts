import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoRoomOverlayComponent } from './video-room-overlay.component';

describe('VideoRoomOverlayComponent', () => {
  let component: VideoRoomOverlayComponent;
  let fixture: ComponentFixture<VideoRoomOverlayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VideoRoomOverlayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VideoRoomOverlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
