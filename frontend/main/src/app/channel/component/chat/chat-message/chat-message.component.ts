import { Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { MemberProfileDto } from '../../../../user/model/member-profile-dto.model';
import { CurrentUserService } from '../../../../user/service/current-user.service';
import { CommonModule } from '@angular/common';
import { ChannelDetailsDto } from '../../../model/channel-detais-dto.model';
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
import { ChannelMemberProfilePictureComponent } from '../../channel-member-profile-picture/channel-member-profile-picture.component';
import { ChannelFileType } from '../../../enum/channel-file-type.enum';
import { ChannelFileDto } from '../../../model/file/channel-file-dto.model';
import { MemberChannelService } from '../../../service/api/member-channel.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import {
  ImagePreviewDialogComponent,
  ImagePreviewDialogConfig,
} from '../../../../shared/component/image-preview-dialog/image-preview-dialog.component';
import { DialogService } from 'primeng/dynamicdialog';
import { LoaderComponent } from '../../../../shared/component/loader/loader.component';
import { ChannelMemberDto } from '../../../model/member/channel-member-dto.model';

interface ImageDetails {
  src: SafeUrl | null;
  file: Blob | null;
}

@Component({
  selector: 'app-chat-message',
  imports: [
    CommonModule,
    ChannelMemberProfilePictureComponent,
    MessageTextNodeComponent,
    TypographyComponent,
    ZonedDatePipe,
    TranslateModule,
    LoaderComponent,
    LoaderComponent,
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
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly dialogService = inject(DialogService);

  private userProfileSubscription!: Subscription;
  protected userProfile!: MemberProfileDto;
  protected imagesDetails: ImageDetails[] = [];
  private readonly domSanitizer = inject(DomSanitizer);

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => (this.userProfile = userProfile));
    this.message.files
      .filter((file) => file.type === ChannelFileType.IMAGE)
      .sort((a, b) => a.createdAt.getTime() - b.createdAt.getTime())
      .forEach((image) => this.loadImage(image));
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

  protected getChannelMember(): ChannelMemberDto | undefined {
    return this.channel.members.find(
      (channelMember) => channelMember.member.id === this.message.member.id
    );
  }

  protected isError() {
    return (
      (this.message as UnprocessedChannelMessageBasicsDto).internalStatus ===
      InternalMessageStatus.ERROR
    );
  }

  private loadImage(image: ChannelFileDto): void {
    const imageDetails: ImageDetails = {
      src: null,
      file: null,
    };
    this.imagesDetails.push(imageDetails);
    this.memberChannelService
      .downloadFie(this.channel.id, image.id)
      .subscribe((res) => {
        imageDetails.file = res;
        const reader = new FileReader();
        reader.onload = (): void => {
          const value = this.domSanitizer.bypassSecurityTrustUrl(
            reader.result as string
          );
          imageDetails.src = value;
        };
        reader.readAsDataURL(res);
      });
  }
  protected openImagePreview(index: number) {
    if (
      index >= this.imagesDetails.length ||
      this.imagesDetails[index].file === null
    ) {
      return;
    }
    const data: ImagePreviewDialogConfig = {
      image: this.imagesDetails[index].file,
    };
    this.dialogService.open(ImagePreviewDialogComponent, {
      closable: true,
      modal: true,
      width: 'calc(100vw - 15rem)' as string,
      closeOnEscape: true,
      dismissableMask: true,
      focusOnShow: false,
      data,
    });
  }
}
