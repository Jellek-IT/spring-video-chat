import { Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AvatarUploadComponent } from '../../../shared/component/avatar-upload/avatar-upload.component';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';
import { TranslateModule } from '@ngx-translate/core';
import { CurrentUserService } from '../../service/current-user.service';
import { ToastService } from '../../../shared/service/toast.service';
import { EndpointErrorService } from '../../../error/service/endpoint-error.service';
import { DynamicDialogConfig } from 'primeng/dynamicdialog';
import { MemberProfileDto } from '../../model/member-profile-dto.model';
import { Subscription } from 'rxjs';
import { ButtonModule } from 'primeng/button';

export interface UserSettingsDialogConfig {
  onUpdateProfilePictureEnd?: (image: Blob | null) => void;
}

@Component({
  selector: 'app-user-settings-dialog',
  imports: [
    AvatarUploadComponent,
    TypographyComponent,
    TranslateModule,
    ButtonModule,
  ],
  templateUrl: './user-settings-dialog.component.html',
  styleUrl: './user-settings-dialog.component.scss',
})
export class UserSettingsDialogComponent implements OnInit, OnDestroy {
  private readonly toastService = inject(ToastService);
  private readonly currentUserService = inject(CurrentUserService);
  private readonly endpointErrorService = inject(EndpointErrorService);
  private readonly config: DynamicDialogConfig<UserSettingsDialogConfig> =
    inject(DynamicDialogConfig<UserSettingsDialogConfig>);

  @ViewChild('profilePictureUpload')
  protected thumbnailUploadComponent?: AvatarUploadComponent;
  private userProfileSubscription!: Subscription;
  protected userProfile!: MemberProfileDto;
  protected removeProfilePictureLoading: boolean = false;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => (this.userProfile = userProfile));
  }
  public ngOnDestroy(): void {
    this.userProfileSubscription.unsubscribe();
  }

  protected handleProfilePictureUpload(image: Blob) {
    this.currentUserService.updateProfilePicture(image).subscribe({
      next: (_) => {
        this.toastService.displaySuccessMessage(
          'user.updateProfilePictureSuccess'
        );
        this.thumbnailUploadComponent?.uploadEnded();
        this.config.data?.onUpdateProfilePictureEnd?.(image);
      },
      error: (e) => {
        this.endpointErrorService.handle(e);
        this.thumbnailUploadComponent?.uploadError();
      },
    });
  }

  protected removeProfilePicture() {
    this.removeProfilePictureLoading = true;
    this.currentUserService.updateProfilePicture(null).subscribe({
      next: (_) => {
        this.toastService.displaySuccessMessage(
          'user.updateProfilePictureSuccess'
        );
        this.thumbnailUploadComponent?.uploadEnded();
        this.config.data?.onUpdateProfilePictureEnd?.(null);
        this.removeProfilePictureLoading = false;
      },
      error: (e) => {
        this.endpointErrorService.handle(e);
        this.removeProfilePictureLoading = false;
      },
    });
  }
}
