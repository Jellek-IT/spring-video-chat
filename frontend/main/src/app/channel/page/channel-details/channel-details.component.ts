import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ContainerComponent } from '../../../shared/component/container/container.component';
import { CardModule } from 'primeng/card';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ChannelDetailsDto } from '../../model/channel-detais-dto.model';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ChannelChatComponent } from '../../component/chat/channel-chat/channel-chat.component';
import { MemberChannelService } from '../../service/api/member-channel.service';
import { ButtonModule } from 'primeng/button';
import { VideoRoomOverlayComponent } from '../../component/video-room/video-room-overlay/video-room-overlay.component';
import { TooltipModule } from 'primeng/tooltip';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MemberProfileDto } from '../../../user/model/member-profile-dto.model';
import { CurrentUserService } from '../../../user/service/current-user.service';
import { ChannelMemberDto } from '../../model/member/channel-member-dto.model';
import { ChannelMemberRights } from '../../enum/channel-member-rights.enum';
import {
  AddChannelMemberDialogComponent,
  AddChannelMemberDialogConfig,
} from '../../component/add-channel-member-dialog/add-channel-member-dialog.component';
import { DialogService } from 'primeng/dynamicdialog';
import { AvatarComponent } from '../../../shared/component/avatar/avatar.component';
import {
  ChannelSettingsConfig,
  ChannelSettingsDialogComponent,
} from '../../component/channel-settings-dialog/channel-settings-dialog.component';
import { ChannelMemberProfilePictureComponent } from '../../component/channel-member-profile-picture/channel-member-profile-picture.component';
import { ConfirmationService } from 'primeng/api';
import { ToastService } from '../../../shared/service/toast.service';
import { HttpErrorResponse } from '@angular/common/http';
import { EndpointErrorService } from '../../../error/service/endpoint-error.service';

@Component({
  selector: 'app-channel-details',
  imports: [
    CommonModule,
    ContainerComponent,
    CardModule,
    TypographyComponent,
    ChannelChatComponent,
    ButtonModule,
    VideoRoomOverlayComponent,
    TooltipModule,
    TranslateModule,
    ChannelMemberProfilePictureComponent,
  ],
  templateUrl: './channel-details.component.html',
  styleUrl: './channel-details.component.scss',
})
export class ChannelDetailsComponent implements OnInit, OnDestroy {
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly currentUserService = inject(CurrentUserService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly dialogService = inject(DialogService);
  private readonly translateService = inject(TranslateService);
  private readonly confirmationService = inject(ConfirmationService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);
  private readonly endpointErrorHandlerService = inject(EndpointErrorService);

  private loadChannelSubscription: Subscription | null = null;
  private userProfileSubscription!: Subscription;
  protected channel: ChannelDetailsDto | null = null;
  protected userProfile!: MemberProfileDto;
  protected currentUserChannelMember?: ChannelMemberDto;
  protected showVideoRoom = false;
  protected channelMemberRights = ChannelMemberRights;
  protected leaveLoading = false;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => (this.userProfile = userProfile));
    this.activatedRoute.params.subscribe((params: Params) => {
      this.channel = null;
      this.loadChannel(params['id']);
    });
  }

  public ngOnDestroy(): void {
    this.userProfileSubscription.unsubscribe();
  }

  private loadChannel(id: string): void {
    this.loadChannelSubscription && this.loadChannelSubscription.unsubscribe();
    this.loadChannelSubscription = this.memberChannelService
      .getById(id)
      .subscribe((res) => {
        this.channel = res;
        this.currentUserChannelMember = res.members.find(
          (member) => member.member.id === this.userProfile.id
        );
      });
  }

  protected toggleShowVideoRoom(): void {
    this.showVideoRoom = !this.showVideoRoom;
  }

  protected closeVideoRoom(): void {
    this.showVideoRoom = false;
  }

  protected currentUserHasRight(right: ChannelMemberRights) {
    return (
      this.currentUserChannelMember !== undefined &&
      this.currentUserChannelMember.rights.includes(right)
    );
  }

  protected openAddChannelMemberDialog(): void {
    if (this.channel === null || this.currentUserChannelMember === undefined) {
      return;
    }
    const data: AddChannelMemberDialogConfig = {
      manageRight: this.currentUserHasRight(ChannelMemberRights.MANAGE),
      channel: this.channel,
    };
    this.dialogService.open(AddChannelMemberDialogComponent, {
      closable: true,
      modal: true,
      header: this.translateService.instant('channel.member.add'),
      dismissableMask: true,
      width: '30rem',
      data,
    });
  }

  protected leave() {
    this.confirmationService.confirm({
      header: this.translateService.instant('channel.leaveDialog.header'),
      message: this.translateService.instant('channel.leaveDialog.message', {
        channel: this.channel?.name,
      }),
      accept: () => {
        if (this.channel === null) {
          return;
        }
        this.leaveLoading = true;
        this.memberChannelService.leave(this.channel.id).subscribe({
          next: () => {
            this.router.navigate(['/']);
            this.toastService.displaySuccessMessage(
              'channel.leaveDialog..success'
            );
          },
          error: (error: HttpErrorResponse) => {
            this.endpointErrorHandlerService.handle(error),
              (this.leaveLoading = false);
          },
        });
      },
    });
  }

  protected getNotDeletedChannelMembers(): ChannelMemberDto[] {
    if (this.channel === null) {
      return [];
    }
    return this.channel.members.filter((member) => !member.deleted);
  }

  protected openSettingsDialog(): void {
    if (this.channel === null) {
      return;
    }
    const data: ChannelSettingsConfig = {
      channel: this.channel,
      onUpdateEnd: (value) => {
        this.channel = {
          ...this.channel,
          name: value.name,
        } as ChannelDetailsDto;
      },
      onUpdateThumbnailEnd: (image) => {
        this.channel = {
          ...this.channel,
          hasThumbnail: image !== null,
        } as ChannelDetailsDto;
      },
    };
    this.dialogService.open(ChannelSettingsDialogComponent, {
      closable: true,
      modal: true,
      header: this.translateService.instant('channel.settings'),
      dismissableMask: true,
      width: '30rem',
      data,
    });
  }
}
