import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { ChannelMemberRights } from '../../enum/channel-member-rights.enum';
import { ChannelMemberDto } from '../../model/member/channel-member-dto.model';
import { ChannelDetailsDto } from '../../model/channel-detais-dto.model';
import { ToastService } from '../../../shared/service/toast.service';
import { MemberChannelService } from '../../service/api/member-channel.service';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { EndpointErrorService } from '../../../error/service/endpoint-error.service';
import formUtils from '../../../shared/utils/form-utils';
import { UpdateChannelMemberRequest } from '../../model/update-channel-member-request.model';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { ChannelMemberRightPipe } from '../../pipe/channel-member-right.pipe';

interface ModifyChannelMemberRightsForm {
  rights: FormControl<ChannelMemberRights[] | null>;
}

export interface ModifyChannelMemberRightsDialogConfig {
  channel: ChannelDetailsDto;
  channelMember: ChannelMemberDto;
}

@Component({
  selector: 'app-modify-channel-member-rights-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    ButtonModule,
    CheckboxModule,
    ChannelMemberRightPipe,
  ],
  templateUrl: './modify-channel-member-rights-dialog.component.html',
  styleUrl: './modify-channel-member-rights-dialog.component.scss',
})
export class ModifyChannelMemberRightsDialogComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly config: DynamicDialogConfig<ModifyChannelMemberRightsDialogConfig> =
    inject(DynamicDialogConfig);
  private readonly ref = inject(DynamicDialogRef);
  private readonly endpointErrorService = inject(EndpointErrorService);
  private readonly channelMember = this.config.data!.channelMember;
  private readonly channel = this.config.data!.channel;

  protected readonly channelMemberRights = Object.values(ChannelMemberRights);
  protected loading: boolean = false;
  protected readonly form: FormGroup<ModifyChannelMemberRightsForm> =
    this.formBuilder.group({
      rights: [this.channelMember.rights as ChannelMemberRights[]],
    });

  protected onCreateSubmit() {
    formUtils.validate(this.form);
    if (!this.form.valid) {
      this.toastService.displayErrorMessage('error.formInvalid');
      return;
    }
    const value = this.form.value!;
    const updateChannelMemberRequest: UpdateChannelMemberRequest = {
      rights: value.rights!,
      memberId: this.channelMember.member.id,
    };
    this.memberChannelService
      .updateMember(this.channel.id, updateChannelMemberRequest)
      .subscribe({
        next: (_) => {
          this.toastService.displaySuccessMessage(
            'channel.member.updateRightsSuccess',
            { nickname: this.channelMember.member.nickname }
          );
          this.loading = false;
          this.ref.close();
        },
        error: (e) => {
          this.endpointErrorService.handle(e);
          this.loading = false;
        },
      });
  }
}
