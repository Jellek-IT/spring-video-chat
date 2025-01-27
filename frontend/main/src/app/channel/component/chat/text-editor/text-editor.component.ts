import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  EventEmitter,
  inject,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import validation, {
  channelMessageTextMaxCharacters,
} from '../../../../shared/utils/validation';
import formUtils from '../../../../shared/utils/form-utils';
import { ToastService } from '../../../../shared/service/toast.service';
import { CreateChannelMessagePayload } from '../../../model/message/create-channel-message-payload.model';
import { v4 as uuidv4 } from 'uuid';
import { ChannelDetailsDto } from '../../../model/channel-detais-dto.model';
import { InternalMessageStatus } from '../../../enum/internal/internal-message-status.enum';
import { MemberProfileDto } from '../../../../user/model/member-profile-dto.model';
import { CurrentUserService } from '../../../../user/service/current-user.service';
import { Subscription } from 'rxjs';
import { MessageDomTagProcessorService } from '../../../service/message-dom-tag-processor.service';
import { StompChannelService } from '../../../service/api/stomp-channel.service';
import { UnprocessedChannelMessageBasicsDto } from '../../../model/internal/message/unprocessed-channel-message-basics-dto';

interface MessageForm {
  text: FormControl<string | null>;
}

@Component({
  selector: 'app-text-editor',
  imports: [
    CommonModule,
    ButtonModule,
    TranslateModule,
    RippleModule,
    ReactiveFormsModule,
  ],
  templateUrl: './text-editor.component.html',
  styleUrl: './text-editor.component.scss',
})
export class TextEditorComponent implements OnInit, OnDestroy {
  @Input({ required: true }) public channel!: ChannelDetailsDto;
  @Output() public messageCreated =
    new EventEmitter<UnprocessedChannelMessageBasicsDto>();
  @ViewChild('textarea')
  protected set textarea(element: ElementRef<HTMLElement>) {
    this._textarea = element;
    this.previousHtmlValue = element.nativeElement.innerHTML;
  }

  private readonly formBuilder = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly stompChannelService = inject(StompChannelService);
  private readonly currentUserService = inject(CurrentUserService);
  private readonly messageDomTagProcessorService = inject(
    MessageDomTagProcessorService
  );

  private _textarea?: ElementRef<HTMLElement>;
  private userProfileSubscription!: Subscription;
  protected form: FormGroup<MessageForm> = this.formBuilder.group({
    text: ['', validation.channelMessageText],
  });
  protected focused = false;
  protected sendEnabled = false;
  protected previousHtmlValue = '<br>';
  protected textContent: string | null = '';
  protected userProfile!: MemberProfileDto;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => (this.userProfile = userProfile));
  }
  public ngOnDestroy(): void {
    this.userProfileSubscription.unsubscribe();
  }

  protected handleFocus(): void {
    this.focused = true;
  }

  protected handleFocusOut(): void {
    this.focused = false;
  }

  protected handleChange(): void {
    const textareaElement = this._textarea?.nativeElement;
    if (textareaElement === undefined) {
      return;
    }
    const result = this.messageDomTagProcessorService.toTags(textareaElement);
    if (result.length >= channelMessageTextMaxCharacters) {
      textareaElement.innerHTML = this.previousHtmlValue;
    }
    this.previousHtmlValue = textareaElement.innerHTML;
    this.setTextValue(result);
  }

  protected handleKeydown(event: KeyboardEvent): void {
    if (event.key !== 'Enter' || event.shiftKey) {
      return;
    }
    event.preventDefault();
    if (!this.isBlank()) {
      this.onSubmit();
    }
  }

  protected onSubmit(): void {
    formUtils.validate(this.form);
    if (!this.form.valid) {
      if (this.form.controls.text.hasError('channelMessageText')) {
        this.toastService.displayErrorMessage(
          'error.validation.channelMessageText'
        );
      } else {
        this.toastService.displayErrorMessage('error.formInvalid');
      }
      return;
    }
    const textareaElement = this._textarea?.nativeElement;
    const value = this.form.value!;
    const payload: CreateChannelMessagePayload = {
      text: value.text!,
    };
    const transactionId = uuidv4();
    const message: UnprocessedChannelMessageBasicsDto = {
      transactionId: transactionId,
      internalStatus: InternalMessageStatus.SENDING,
      createdAt: new Date(),
      text: value.text!,
      textNodes: this.messageDomTagProcessorService.toTextNodes(value.text!),
      member: {
        id: this.userProfile.id,
        nickname: this.userProfile.nickname,
      },
    };
    this.messageCreated.emit(message);
    this.previousHtmlValue = '';
    if (textareaElement !== undefined) {
      textareaElement.innerHTML = '';
    }
    this.setTextValue('');
    this.stompChannelService.createMessage(this.channel.id, payload, {
      transactionId: transactionId,
    });
  }

  protected isEmpty(): boolean {
    return this.textContent === null || this.textContent === '';
  }

  private isBlank(): boolean {
    return (this.form.value.text ?? '') === '';
  }

  private setTextValue(text: string): void {
    const textareaElement = this._textarea?.nativeElement;
    if (textareaElement !== undefined) {
      this.textContent = textareaElement.textContent;
    }
    this.sendEnabled = text !== '';
    this.form.controls.text.setValue(text);
  }
}
