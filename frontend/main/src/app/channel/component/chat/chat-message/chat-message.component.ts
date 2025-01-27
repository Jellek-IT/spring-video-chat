import { Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { MemberProfileDto } from '../../../../user/model/member-profile-dto.model';
import { CurrentUserService } from '../../../../user/service/current-user.service';
import { CommonModule } from '@angular/common';
import { ChannelDetailsDto } from '../../../model/channel-detais-dto.model';
import { AvatarComponent } from '../../../../shared/component/avatar/avatar.component';
import {
  MessageDomTagProcessorService,
  MessageNode,
} from '../../../service/message-dom-tag-processor.service';
import { InternalChannelMessageBasicsDto } from '../../../model/internal/message/internal-channel-message-basics-dto.model';
import { MessageTextNodeComponent } from '../message-text-node/message-text-node.component';
import { TypographyComponent } from '../../../../shared/component/typography/typography.component';
import { ZonedDatePipe } from '../../../../shared/pipe/zoned-date.pipe';
import { TranslateModule } from '@ngx-translate/core';
import { InternalMessageStatus } from '../../../enum/internal/internal-message-status.enum';
import { UnprocessedChannelMessageBasicsDto } from '../../../model/internal/message/unprocessed-channel-message-basics-dto';

@Component({
  selector: 'app-chat-message',
  imports: [
    CommonModule,
    AvatarComponent,
    MessageTextNodeComponent,
    TypographyComponent,
    ZonedDatePipe,
    TranslateModule,
  ],
  templateUrl: './chat-message.component.html',
  styleUrl: './chat-message.component.scss',
})
export class ChatMessageComponent implements OnInit, OnDestroy {
  @Input({ required: true }) channel!: ChannelDetailsDto;
  @Input({ required: true }) message!:
    | InternalChannelMessageBasicsDto
    | UnprocessedChannelMessageBasicsDto;

  private readonly currentUserService = inject(CurrentUserService);
  private readonly messageDomTagProcessorService = inject(
    MessageDomTagProcessorService
  );

  private userProfileSubscription!: Subscription;
  protected userProfile!: MemberProfileDto;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => (this.userProfile = userProfile));
  }

  public ngOnDestroy(): void {
    this.userProfileSubscription.unsubscribe();
  }

  protected getMessageContent(): MessageNode[] {
    return this.messageDomTagProcessorService.toTextNodes(this.message.text);
  }

  protected isSending() {
    return (
      (this.message as UnprocessedChannelMessageBasicsDto).internalStatus ===
      InternalMessageStatus.SENDING
    );
  }

  protected isError() {
    return (
      (this.message as UnprocessedChannelMessageBasicsDto).internalStatus ===
      InternalMessageStatus.ERROR
    );
  }
}
