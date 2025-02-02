import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifyChannelMemberRightsDialogComponent } from './modify-channel-member-rights-dialog.component';

describe('ModifyChannelMemberRightsDialogComponent', () => {
  let component: ModifyChannelMemberRightsDialogComponent;
  let fixture: ComponentFixture<ModifyChannelMemberRightsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModifyChannelMemberRightsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifyChannelMemberRightsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
