import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChannelMemberProfilePictureComponent } from './channel-member-profile-picture.component';

describe('ChannelMemberProfilePictureComponent', () => {
  let component: ChannelMemberProfilePictureComponent;
  let fixture: ComponentFixture<ChannelMemberProfilePictureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChannelMemberProfilePictureComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChannelMemberProfilePictureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
