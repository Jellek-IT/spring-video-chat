import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChannelSettingsDialogComponent } from './channel-settings-dialog.component';

describe('ChannelSettingsDialogComponent', () => {
  let component: ChannelSettingsDialogComponent;
  let fixture: ComponentFixture<ChannelSettingsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChannelSettingsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChannelSettingsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
