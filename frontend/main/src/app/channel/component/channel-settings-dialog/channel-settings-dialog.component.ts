import { Component, inject, ViewChild } from '@angular/core';
import { ChannelDetailsDto } from '../../model/channel-detais-dto.model';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { ToastService } from '../../../shared/service/toast.service';
import { MemberChannelService } from '../../service/api/member-channel.service';
import { EndpointErrorService } from '../../../error/service/endpoint-error.service';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { AvatarUploadComponent } from '../../../shared/component/avatar-upload/avatar-upload.component';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';
import { TranslateModule } from '@ngx-translate/core';
import validation from '../../../shared/utils/validation';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ValidationMessageComponent } from '../../../shared/component/validation-message/validation-message.component';
import formUtils from '../../../shared/utils/form-utils';
import { UpdateChannelRequest } from '../../model/update-channel-request.model';
import { CommonModule } from '@angular/common';

interface UpdateChannelForm {
  name: FormControl<string | null>;
}

export interface ChannelSettingsConfig {
  onUpdateEnd?: (value: UpdateChannelRequest) => void;
  onUpdateThumbnailEnd?: (image: Blob | null) => void;
  channel: ChannelDetailsDto;
}

@Component({
  selector: 'app-channel-settings-dialog',
  imports: [
    CommonModule,
    AvatarUploadComponent,
    TypographyComponent,
    TranslateModule,
    ButtonModule,
    InputTextModule,
    ValidationMessageComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './channel-settings-dialog.component.html',
  styleUrl: './channel-settings-dialog.component.scss',
})
export class ChannelSettingsDialogComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly endpointErrorService = inject(EndpointErrorService);
  private readonly config: DynamicDialogConfig<ChannelSettingsConfig> = inject(
    DynamicDialogConfig<ChannelSettingsConfig>
  );
  protected readonly form: FormGroup<UpdateChannelForm> =
    this.formBuilder.group({
      name: [this.config.data!.channel.name, validation.channelName],
    });

  @ViewChild('thumbnailUpload')
  protected thumbnailUploadComponent?: AvatarUploadComponent;
  protected channel: ChannelDetailsDto = this.config.data!.channel;
  protected formLoading: boolean = false;
  protected removeThumbnailLoading: boolean = false;

  protected handleThumbnailUpload(image: Blob) {
    this.memberChannelService
      .updateThumbnail(this.config.data!.channel.id, image)
      .subscribe({
        next: (_) => {
          this.toastService.displaySuccessMessage(
            'channel.updateThumbnailSuccess'
          );
          this.thumbnailUploadComponent?.uploadEnded();
          this.config.data?.onUpdateThumbnailEnd?.(image);
          this.channel = { ...this.channel, hasThumbnail: true };
        },
        error: (e) => {
          this.endpointErrorService.handle(e);
          this.thumbnailUploadComponent?.uploadError();
        },
      });
  }

  protected removeThumbnail() {
    this.removeThumbnailLoading = true;
    this.memberChannelService
      .updateThumbnail(this.config.data!.channel.id, null)
      .subscribe({
        next: (_) => {
          this.toastService.displaySuccessMessage(
            'user.updateProfilePictureSuccess'
          );
          this.thumbnailUploadComponent?.uploadEnded();
          this.removeThumbnailLoading = false;
          this.config.data?.onUpdateThumbnailEnd?.(null);
          this.channel = { ...this.channel, hasThumbnail: false };
        },
        error: (e) => {
          this.endpointErrorService.handle(e);
          this.removeThumbnailLoading = false;
        },
      });
  }

  protected onUpdateSubmit(): void {
    formUtils.validate(this.form);
    if (!this.form.valid) {
      this.toastService.displayErrorMessage('error.formInvalid');
      return;
    }
    const value = this.form.value!;
    const updateChannelRequest: UpdateChannelRequest = {
      name: value.name!,
    };
    this.formLoading = true;
    this.memberChannelService
      .update(this.config.data!.channel.id, updateChannelRequest)
      .subscribe({
        next: (_) => {
          this.toastService.displaySuccessMessage('channel.updateSuccess');
          this.formLoading = false;
          this.config.data?.onUpdateEnd?.(updateChannelRequest);
        },
        error: (e) => {
          this.endpointErrorService.handle(e);
          this.formLoading = false;
        },
      });
  }
}
