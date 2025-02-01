import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { ChannelMemberRights } from '../../enum/channel-member-rights.enum';
import validation from '../../../shared/utils/validation';
import formUtils from '../../../shared/utils/form-utils';
import { ToastService } from '../../../shared/service/toast.service';
import { MemberChannelService } from '../../service/api/member-channel.service';
import { AddChannelMemberRequest } from '../../model/member/add-channel-member-request.model';
import { ChannelDetailsDto } from '../../model/channel-detais-dto.model';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { EndpointErrorService } from '../../../error/service/endpoint-error.service';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ValidationMessageComponent } from '../../../shared/component/validation-message/validation-message.component';
import { CheckboxModule } from 'primeng/checkbox';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';
import { ChannelMemberRightPipe } from '../../pipe/channel-member-right.pipe';
import { CommonModule } from '@angular/common';

interface AddChannelMemberForm {
  memberId: FormControl<string | null>;
  rights: FormControl<ChannelMemberRights[] | null>;
}

export interface AddChannelMemberDialogConfig {
  channel: ChannelDetailsDto;
  availableRights: ChannelMemberRights[];
  onCreateEnd?: () => void;
}

@Component({
  selector: 'app-add-channel-member-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    ButtonModule,
    InputTextModule,
    ValidationMessageComponent,
    CheckboxModule,
    ChannelMemberRightPipe,
  ],
  templateUrl: './add-channel-member-dialog.component.html',
  styleUrl: './add-channel-member-dialog.component.scss',
})
export class AddChannelMemberDialogComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly config: DynamicDialogConfig<AddChannelMemberDialogConfig> =
    inject(DynamicDialogConfig);
  private readonly ref = inject(DynamicDialogRef);
  private readonly endpointErrorService = inject(EndpointErrorService);

  protected readonly availableRights = this.config.data!.availableRights;
  protected loading: boolean = false;
  protected readonly form: FormGroup<AddChannelMemberForm> =
    this.formBuilder.group({
      memberId: ['', validation.uuidV4],
      rights: [[] as ChannelMemberRights[]],
    });

  protected onCreateSubmit() {
    formUtils.validate(this.form);
    if (!this.form.valid) {
      this.toastService.displayErrorMessage('error.formInvalid');
      return;
    }
    const value = this.form.value!;
    const addChannelMemberRequest: AddChannelMemberRequest = {
      member: { id: value.memberId! },
      rights: value.rights!,
    };
    this.memberChannelService
      .addMember(this.config.data?.channel.id ?? '', addChannelMemberRequest)
      .subscribe({
        next: (_) => {
          this.toastService.displaySuccessMessage('channel.addMemberSuccess');
          this.loading = false;
          this.config.data?.onCreateEnd?.();
          this.ref.close();
        },
        error: (e) => {
          this.endpointErrorService.handle(e);
          this.loading = false;
        },
      });
  }
}
