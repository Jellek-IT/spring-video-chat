import {
  Component,
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
import { ChannelMemberRights } from '../../enum/channel-member-rights.enum';
import { MemberBasicsDto } from '../../../user/model/member-basics-dto.model';

@Component({
  selector: 'app-channel-member-profile-picture',
  imports: [AvatarComponent],
  templateUrl: './channel-member-profile-picture.component.html',
  styleUrl: './channel-member-profile-picture.component.scss',
})
export class ChannelMemberProfilePictureComponent
  implements OnChanges, OnDestroy, OnInit
{
  @Input() public size: 'normal' | 'large' | 'xlarge' = 'large';
  @Input()
  public member?: MemberBasicsDto;
  @Input() public contextMenu: boolean = false;
  @Input({ required: true }) public channel!: ChannelDetailsDto | null;

  private readonly memberChannelService = inject(MemberService);
  private readonly currentUserService = inject(CurrentUserService);

  protected profilePictureUrl: string | null = null;
  protected getProfilePictureSubscription?: Subscription;
  protected userProfile!: MemberProfileDto;
  private userProfileSubscription!: Subscription;
  protected channelMemberRights = ChannelMemberRights;
  protected currentUserChannelMember?: ChannelMemberDto;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => {
        this.userProfile = userProfile;
        this.updateCurrentChannelMember();
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
    if (this.member === undefined || !this.member.hasProfilePicture) {
      this.profilePictureUrl = null;
      return;
    }
    this.getProfilePictureSubscription = this.memberChannelService
      .getProfilePicture(this.member.id)
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

  protected currentUserHasRight(right: ChannelMemberRights) {
    return (
      this.currentUserChannelMember !== undefined &&
      this.currentUserChannelMember.rights.includes(right)
    );
  }
}
