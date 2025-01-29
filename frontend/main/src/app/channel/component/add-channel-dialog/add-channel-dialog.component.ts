import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import validation from '../../../shared/utils/validation';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import formUtils from '../../../shared/utils/form-utils';
import { ToastService } from '../../../shared/service/toast.service';
import { CreateChannelRequest } from '../../model/create-channel-request.model';
import { EndpointErrorService } from '../../../error/service/endpoint-error.service';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ValidationMessageComponent } from '../../../shared/component/validation-message/validation-message.component';
import { MemberChannelService } from '../../service/api/member-channel.service';

interface AddChannelForm {
  name: FormControl<string | null>;
}

export interface AddChannelDialogConfig {
  onCreateEnd?: () => void;
}

@Component({
  selector: 'app-add-channel-dialog',
  imports: [
    ReactiveFormsModule,
    TranslateModule,
    ButtonModule,
    InputTextModule,
    ValidationMessageComponent,
  ],
  templateUrl: './add-channel-dialog.component.html',
  styleUrl: './add-channel-dialog.component.scss',
})
export class AddChannelDialogComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly endpointErrorService = inject(EndpointErrorService);
  private readonly config: DynamicDialogConfig<AddChannelDialogConfig> = inject(
    DynamicDialogConfig<AddChannelDialogConfig>
  );
  private readonly ref = inject(DynamicDialogRef);

  protected loading: boolean = false;
  protected readonly form: FormGroup<AddChannelForm> = this.formBuilder.group({
    name: ['', validation.channelName],
  });

  protected onCreateSubmit() {
    formUtils.validate(this.form);
    if (!this.form.valid) {
      this.toastService.displayErrorMessage('error.formInvalid');
      return;
    }
    const value = this.form.value!;
    const createChannelRequest: CreateChannelRequest = {
      name: value.name!,
    };
    this.loading = true;
    this.memberChannelService.create(createChannelRequest).subscribe({
      next: (res) => {
        this.toastService.displaySuccessMessage('channel.createSuccess', {
          name: res.name,
        });
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
