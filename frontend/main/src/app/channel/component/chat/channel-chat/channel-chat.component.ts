import {
  AfterViewChecked,
  Component,
  ElementRef,
  inject,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { ChannelMessageBasicsDto } from '../../../model/message/channel-message-basics-dto.model';
import { TextEditorComponent } from '../text-editor/text-editor.component';
import { ChannelDetailsDto } from '../../../model/channel-detais-dto.model';
import { InternalChannelMessageBasicsDto } from '../../../model/internal/message/internal-channel-message-basics-dto.model';
import { Subscription } from 'rxjs';
import { ToastService } from '../../../../shared/service/toast.service';
import { Router } from '@angular/router';
import {
  StompService,
  StompSubscriptionAccepted,
} from '../../../../core/stomp/service/stomp.service';
import { StompResponse } from '../../../../core/stomp/model/stomp-response.model';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../../../shared/component/loader/loader.component';
import { InternalMessageStatus } from '../../../enum/internal/internal-message-status.enum';
import { ExceptionResponse } from '../../../../error/model/exception-response.model';
import { ChatMessageComponent } from '../chat-message/chat-message.component';
import { StompChannelService } from '../../../service/api/stomp-channel.service';
import { MemberChannelService } from '../../../service/api/member-channel.service';
import { MemberChannelMessageQueryParamsPageable } from '../../../model/message/member-channel-message-query-params-pageable.model';
import { UnprocessedChannelMessageBasicsDto } from '../../../model/internal/message/unprocessed-channel-message-basics-dto';
import { MessageDomTagProcessorService } from '../../../service/message-dom-tag-processor.service';

@Component({
  selector: 'app-channel-chat',
  imports: [
    CommonModule,
    TextEditorComponent,
    LoaderComponent,
    ChatMessageComponent,
  ],
  templateUrl: './channel-chat.component.html',
  styleUrl: './channel-chat.component.scss',
})
export class ChannelChatComponent
  implements OnInit, OnDestroy, AfterViewChecked
{
  @Input({ required: true }) public channel!: ChannelDetailsDto;
  @Input({ required: true }) public displayChat!: boolean;
  @Input({ required: true }) public displayEditor!: boolean;

  private readonly previousLoadBatchSize = 20;
  private readonly newMessageScrollBottomOffsetDelta = 20;
  private readonly stompChannelService = inject(StompChannelService);
  private readonly stompService = inject(StompService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly messageDomTagProcessorService = inject(
    MessageDomTagProcessorService
  );

  private previousMessagesViewLoading = false;
  private previousMessagesSubscription?: Subscription;
  private stompMessageSubscription?: Subscription;
  private stompErrorSubscription!: Subscription;
  private transactionToUnprocessedMessages: Map<
    string,
    UnprocessedChannelMessageBasicsDto
  > = new Map();
  private idToProcessedMessage: Map<string, InternalChannelMessageBasicsDto> =
    new Map();
  @ViewChild('loaderContainer')
  protected loaderContainerRef?: ElementRef<HTMLElement>;
  @ViewChild('scrollableContainer')
  protected scrollableContainerRef?: ElementRef<HTMLElement>;
  protected messages: InternalChannelMessageBasicsDto[] = [];
  protected messageSubscriptionAccepted = false;
  protected previousMessagesLoading = false;
  protected hasPreviousMessages = true;
  protected restoreBottomOffset: number | null = 0;

  public ngOnInit(): void {
    this.stompErrorSubscription = this.stompService
      .getErrorAsObservable()
      .subscribe((res) => this.handleMessageError(res));
    if (this.displayChat) {
      this.loadPreviousMessages();
      this.connectToChannelMessages();
    }
  }

  public ngOnDestroy(): void {
    this.previousMessagesSubscription?.unsubscribe();
    this.stompMessageSubscription?.unsubscribe();
    this.stompErrorSubscription.unsubscribe();
  }

  public ngAfterViewChecked(): void {
    if (this.restoreBottomOffset !== null) {
      this.restoreScrollOffset();
      this.restoreBottomOffset = null;
    }
    if (this.previousMessagesViewLoading && !this.previousMessagesLoading) {
      this.previousMessagesViewLoading = false;
    }
  }

  protected loadPreviousMessages() {
    this.previousMessagesLoading = true;
    this.previousMessagesViewLoading = true;

    const queryParams: MemberChannelMessageQueryParamsPageable = {
      sort: 'sequence,desc',
      size: this.previousLoadBatchSize,
    };
    const lastSequence = this.messages[0]?.sequence;
    if (lastSequence !== undefined) {
      queryParams.beforeSequence = lastSequence;
    }

    this.previousMessagesSubscription?.unsubscribe();
    this.memberChannelService
      .getAllMessagesById(this.channel.id, queryParams)
      .subscribe((res) => {
        this.hasPreviousMessages = !res.last;
        const messages: InternalChannelMessageBasicsDto[] = res.content
          .filter((message) => !this.idToProcessedMessage.has(message.id!))
          .map((message) => ({
            ...message,
            textNodes: this.messageDomTagProcessorService.toTextNodes(
              message.text
            ),
          }))
          .reverse();
        messages.forEach((message) =>
          this.idToProcessedMessage.set(message.id!, message)
        );
        this.messages = messages.concat(this.messages);
        this.saveBottomOffset();
        this.previousMessagesLoading = false;
      });
  }

  protected handleMessageCreated(message: UnprocessedChannelMessageBasicsDto) {
    this.messages.push(message);
    this.transactionToUnprocessedMessages.set(message.transactionId, message);
    this.saveMaxScrollOffset();
  }

  private connectToChannelMessages(): void {
    this.stompMessageSubscription = this.stompChannelService
      .subscribeToMessage(this.channel!.id)
      .subscribe({
        next: (message) => {
          if (message instanceof StompSubscriptionAccepted) {
            this.messageSubscriptionAccepted = true;
          } else {
            this.handleMessageResponse(message);
          }
        },
        error: () => {
          this.toastService.displayErrorMessage(
            'channel.chat.stompMessageSubscribeError',
            { name: this.channel.name }
          );
          this.router.navigate(['/channels']);
        },
      });
  }

  private handleMessageResponse(
    messageResponse: StompResponse<ChannelMessageBasicsDto>
  ) {
    const data = messageResponse.data;
    const unprocessedMessage = this.getUnprocessedMessage(messageResponse);
    if (unprocessedMessage !== null) {
      this.finishProcessing(unprocessedMessage, data);
    } else {
      const processedMessage: InternalChannelMessageBasicsDto = {
        textNodes: this.messageDomTagProcessorService.toTextNodes(data.text),
        ...data,
      };
      this.messages.push(processedMessage);
      this.idToProcessedMessage.set(processedMessage.id!, processedMessage);
      this.saveMaxScrollOffset(true);
    }
  }

  private finishProcessing(
    message: UnprocessedChannelMessageBasicsDto,
    response: ChannelMessageBasicsDto
  ) {
    this.transactionToUnprocessedMessages.delete(message.transactionId);
    message.id = response.id!;
    message.createdAt = response.createdAt;
    message.internalStatus = InternalMessageStatus.SENT;
    (message.textNodes = this.messageDomTagProcessorService.toTextNodes(
      response.text
    )),
      this.idToProcessedMessage.set(response.id!, message);
  }

  private handleMessageError(errorResponse: StompResponse<ExceptionResponse>) {
    if (errorResponse === null || errorResponse === undefined) {
      return;
    }
    const unprocessedMessage = this.getUnprocessedMessage(errorResponse);
    if (unprocessedMessage !== null) {
      unprocessedMessage.internalStatus = InternalMessageStatus.ERROR;
    }
  }

  private getUnprocessedMessage(
    stompResponse: StompResponse<Object>
  ): UnprocessedChannelMessageBasicsDto | null {
    const transactionId = stompResponse.transactionId;
    if (
      transactionId !== undefined &&
      this.transactionToUnprocessedMessages.has(transactionId)
    ) {
      return this.transactionToUnprocessedMessages.get(transactionId)!;
    }
    return null;
  }

  private restoreScrollOffset() {
    const scrollableContainer = this.scrollableContainerRef?.nativeElement;
    if (scrollableContainer === undefined) {
      return;
    }
    const scrollOffset = Math.min(
      this.getScrollableDistance(),
      this.getScrollableDistance() - this.restoreBottomOffset!
    );
    scrollableContainer.scrollTo(0, scrollOffset);
  }

  private saveBottomOffset() {
    const scrollableContainer = this.scrollableContainerRef?.nativeElement;
    if (scrollableContainer === undefined) {
      return;
    }
    this.restoreBottomOffset =
      this.getScrollableDistance() - scrollableContainer.scrollTop;
  }

  private saveMaxScrollOffset(forMaxScrollOnly = false) {
    const scrollableContainer = this.scrollableContainerRef?.nativeElement;
    if (scrollableContainer === undefined) {
      return;
    }
    if (
      forMaxScrollOnly &&
      scrollableContainer.scrollTop + this.newMessageScrollBottomOffsetDelta <
        this.getScrollableDistance()
    ) {
      return;
    }
    this.restoreBottomOffset = -Infinity;
  }

  private getScrollableDistance(): number {
    const scrollableContainer = this.scrollableContainerRef?.nativeElement;
    if (scrollableContainer === undefined) {
      return 0;
    }
    return scrollableContainer.scrollHeight - scrollableContainer.clientHeight;
  }

  protected handleScrollableContainerScroll() {
    const scrollableContainer = this.scrollableContainerRef?.nativeElement;
    const loaderContainer = this.loaderContainerRef?.nativeElement;
    if (
      scrollableContainer === undefined ||
      loaderContainer === undefined ||
      this.previousMessagesViewLoading
    ) {
      return;
    }
    const scrollableContainerRect = scrollableContainer.getBoundingClientRect();
    const loaderContainerRect = loaderContainer.getBoundingClientRect();
    if (
      scrollableContainerRect.y <
      loaderContainerRect.y + loaderContainerRect.height
    ) {
      this.loadPreviousMessages();
    }
  }
}
