import {
  Component,
  ElementRef,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { AvatarComponent } from '../../../shared/component/avatar/avatar.component';
import { ChannelMemberDto } from '../../model/member/channel-member-dto.model';
import { MemberService } from '../../../user/service/api/member.service';
import { ChannelDetailsDto } from '../../model/channel-detais-dto.model';
import { Subscription } from 'rxjs';
import { MemberProfileDto } from '../../../user/model/member-profile-dto.model';
import { CurrentUserService } from '../../../user/service/current-user.service';
import { ChannelMemberRight } from '../../enum/channel-member-right.enum';
import { MenuModule } from 'primeng/menu';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';
import { MenuItem } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NAVIGATOR } from '../../../core/token/navigator.token';
import { ToastService } from '../../../shared/service/toast.service';
import { MemberChannelService } from '../../service/api/member-channel.service';
import { KickCHannelMemberRequest } from '../../model/member/kick-channel-memeber-request.model';
import { EndpointErrorService } from '../../../error/service/endpoint-error.service';
import { DialogService } from 'primeng/dynamicdialog';
import {
  ModifyChannelMemberRightsDialogComponent,
  ModifyChannelMemberRightsDialogConfig,
} from '../modify-channel-member-rights-dialog/modify-channel-member-rights-dialog.component';

@Component({
  selector: 'app-channel-member-profile-picture',
  imports: [
    AvatarComponent,
    MenuModule,
    TypographyComponent,
    CommonModule,
    TranslateModule,
  ],
  templateUrl: './channel-member-profile-picture.component.html',
  styleUrl: './channel-member-profile-picture.component.scss',
})
export class ChannelMemberProfilePictureComponent
  implements OnChanges, OnDestroy, OnInit
{
  @Input() public size: 'normal' | 'large' | 'xlarge' = 'large';
  @Input() public member?: ChannelMemberDto;
  @Input() public contextMenu: boolean = false;
  @Input({ required: true }) public channel!: ChannelDetailsDto | null;
  @Input() public appendToBody: boolean = true;
  @Input() public tooltipPosition: 'right' | 'left' | 'top' | 'bottom' =
    'bottom';

  private readonly memberService = inject(MemberService);
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly currentUserService = inject(CurrentUserService);
  private readonly translateService = inject(TranslateService);
  private readonly navigator = inject(NAVIGATOR);
  private readonly endpointErrorService = inject(EndpointErrorService);
  private readonly toastService = inject(ToastService);
  private readonly dialogService = inject(DialogService);

  protected profilePictureUrl: string | null = null;
  protected getProfilePictureSubscription?: Subscription;
  protected userProfile!: MemberProfileDto;
  private userProfileSubscription!: Subscription;
  protected channelMemberRights = ChannelMemberRight;
  protected currentUserChannelMember?: ChannelMemberDto;
  protected menuItems: MenuItem[] | undefined;
  protected kickLoading = false;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => {
        this.userProfile = userProfile;
        this.updateCurrentChannelMember();
        this.buildMenuItems();
      });
  }

  public ngOnChanges(changes: SimpleChanges): void {
    if (changes['member'] !== undefined) {
      this.loadProfilePicture();
    }
  }
  public ngOnDestroy(): void {
    this.getProfilePictureSubscription?.unsubscribe();
    this.userProfileSubscription.unsubscribe();
  }

  private loadProfilePicture() {
    this.getProfilePictureSubscription?.unsubscribe();
    if (this.member?.member?.hasProfilePicture !== true) {
      this.profilePictureUrl = null;
      return;
    }
    this.getProfilePictureSubscription = this.memberService
      .getProfilePicture(this.member.member.id)
      .subscribe((res) => {
        const reader = new FileReader();
        reader.onload = (): void => {
          this.profilePictureUrl = reader.result as string;
        };
        reader.readAsDataURL(res);
      });
  }

  private updateCurrentChannelMember(): void {
    this.currentUserChannelMember = this.channel?.members.find(
      (member) => member.member.id === this.userProfile.id
    );
  }

  protected currentUserHasRight(right: ChannelMemberRight) {
    return (
      this.currentUserChannelMember !== undefined &&
      this.currentUserChannelMember.rights.includes(right)
    );
  }

  private buildMenuItems(): void {
    this.menuItems = [
      {
        separator: true,
      },
      {
        label: this.translateService.instant('channel.member.menu.copyId'),
        command: () => this.copyIdToClipboard(),
      },
    ];
    if (
      this.member === undefined ||
      this.userProfile.id === this.member.member?.id ||
      this.member.deleted === true
    ) {
      return;
    }
    if (this.currentUserHasRight(ChannelMemberRight.MANAGE)) {
      this.menuItems.push({
        label: this.translateService.instant(
          'channel.member.menu.modifyRights'
        ),
        command: () => this.openUpdateRightsDialog(),
      });
    }
    if (this.currentUserHasRight(ChannelMemberRight.KICK)) {
      this.menuItems.push({
        label: this.translateService.instant('channel.member.menu.kick'),
        icon: this.kickLoading ? 'pi pi-spinner pi-spin' : undefined,
        command: () => this.kickMember(),
      });
    }
  }

  private kickMember(): void {
    if (
      this.kickLoading ||
      this.member === undefined ||
      this.channel === null
    ) {
      return;
    }
    this.kickLoading = true;
    this.buildMenuItems();
    const request: KickCHannelMemberRequest = {
      memberId: this.member.member.id,
    };
    this.memberChannelService.kickMember(this.channel.id, request).subscribe({
      next: () => {
        this.toastService.displaySuccessMessage(
          'channel.member.menu.kickSuccess'
        );
      },
      error: (e) => {
        this.endpointErrorService.handle(e);
      },
    });
  }

  protected openUpdateRightsDialog() {
    if (this.channel === null || this.member === undefined) {
      return;
    }
    const data: ModifyChannelMemberRightsDialogConfig = {
      channelMember: this.member,
      channel: this.channel,
    };
    this.dialogService.open(ModifyChannelMemberRightsDialogComponent, {
      closable: true,
      modal: true,
      header: this.translateService.instant('channel.member.editRights', {
        nickname: this.member.member.nickname,
      }),
      dismissableMask: true,
      width: '30rem',
      data,
    });
  }

  private async copyIdToClipboard(): Promise<void> {
    if (this.navigator === undefined) {
      this.toastService.displayErrorMessage('menu.copyIdError');
      return;
    }
    try {
      await this.navigator.clipboard.writeText(this.userProfile.id);
      this.toastService.displaySuccessMessage('menu.copyIdSuccess');
    } catch (_) {
      this.toastService.displayErrorMessage('menu.copyIdError');
    }
  }

  protected getNickname() {
    let suffix: string = '';
    if (this.userProfile.id === this.member?.member?.id) {
      suffix += ` (${this.translateService.instant('channel.member.you')})`;
    }
    if (this.member?.deleted === true) {
      suffix += ` (${this.translateService.instant('channel.member.deleted')})`;
    }
    return (this.member?.member?.nickname ?? '') + suffix;
  }
}
