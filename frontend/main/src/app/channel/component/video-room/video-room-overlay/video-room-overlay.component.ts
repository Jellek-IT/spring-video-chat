import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { ChannelDetailsDto } from '../../../model/channel-detais-dto.model';
import { CardModule } from 'primeng/card';
import mathUtils from '../../../../shared/utils/math-utils';
import { CommonModule, DOCUMENT } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { TranslateModule } from '@ngx-translate/core';
import { TooltipModule } from 'primeng/tooltip';
import { interval, Subscription } from 'rxjs';
import { LoaderComponent } from '../../../../shared/component/loader/loader.component';
import { ToastService } from '../../../../shared/service/toast.service';
import {
  StompService,
  StompSubscriptionAccepted,
} from '../../../../core/stomp/service/stomp.service';
import { JanusVideoRoomService } from '../../../../core/janus/service/janus-video-room.service';
import { StompVideoRoomService } from '../../../service/api/stomp-video-room.service';
import { JanusStatus } from '../../../../core/janus/enum/jansu-status.enum';
import { JanusVideoRoomStreamType } from '../../../../core/janus/enum/janus-video-room-stream-type.enum';
import { VideoRoomUser } from '../../../../core/janus/model/video-room-user.model';
import { VideoViewComponent } from '../video-view/video-view.component';
import { CarouselModule, CarouselResponsiveOptions } from 'primeng/carousel';
import { VideoRoomStream } from '../../../../core/janus/model/internal/video-room-stream.model';
import { CurrentUserService } from '../../../../user/service/current-user.service';
import { MemberProfileDto } from '../../../../user/model/member-profile-dto.model';
import { ChannelMemberDto } from '../../../model/member/channel-member-dto.model';
import { MemberBasicsDto } from '../../../../user/model/member-basics-dto.model';
import { ErrorResponseType } from '../../../../error/enum/error-response-type.enum';
import { LetDirective } from '../../../../shared/directive/let.directive';
import { AvatarComponent } from '../../../../shared/component/avatar/avatar.component';

interface DragBoxDetails {
  deltaX: number;
  deltaY: number;
}

@Component({
  selector: 'app-video-room-overlay',
  imports: [
    CardModule,
    CommonModule,
    ButtonModule,
    TranslateModule,
    TooltipModule,
    LoaderComponent,
    VideoViewComponent,
    CarouselModule,
    LetDirective,
    AvatarComponent,
  ],
  templateUrl: './video-room-overlay.component.html',
  styleUrl: './video-room-overlay.component.scss',
})
export class VideoRoomOverlayComponent implements OnInit, OnDestroy, OnChanges {
  private readonly tokenRefreshTime = 5 * 60 * 1000; // 5 minutes (token has life of 10 minutes)
  private readonly dragDisabledSelector = '.drag-disabled';
  private readonly stompVideoRoomService = inject(StompVideoRoomService);
  private readonly toastService = inject(ToastService);
  private readonly janusVideoRoomService = inject(JanusVideoRoomService);
  private readonly stompService = inject(StompService);
  private readonly currentUserService = inject(CurrentUserService);
  private readonly document = inject(DOCUMENT);

  @Input({ required: true }) public channel!: ChannelDetailsDto;
  @Output() public videoRoomClosed = new EventEmitter<undefined>();
  @ViewChild('overlay')
  protected overlayRef?: ElementRef<HTMLElement>;
  @ViewChild('box')
  protected boxRef?: ElementRef<HTMLElement>;

  private stompUserVideoRoomSubscription?: Subscription;
  private janusStatusSubscription?: Subscription;
  private refreshTimerSubscription?: Subscription;
  private janusPublishersSubscription?: Subscription;
  private userProfileSubscription!: Subscription;
  private stompErrorSubscription!: Subscription;
  private dragBoxDetails: DragBoxDetails | null = null;
  private initialized = false;
  protected readonly streamTypes = JanusVideoRoomStreamType;
  protected boxStyle?: { [key: string]: any };
  protected connecting = true;
  protected videRoomUsers: VideoRoomUser[] = [];
  protected microphoneEnabled = false;
  protected cameraEnabled = false;
  protected userProfile!: MemberProfileDto;
  protected idToMember: Map<string, MemberBasicsDto> = new Map();
  protected fullscreen = false;
  protected usersCarouselResponsiveOptions:
    | CarouselResponsiveOptions[]
    | undefined;

  public ngOnInit(): void {
    this.userProfileSubscription = this.currentUserService
      .getMemberProfileAsObservable()
      .subscribe((userProfile) => (this.userProfile = userProfile));
    this.janusPublishersSubscription = this.janusVideoRoomService
      .getUsersAsObservable()
      .subscribe((videoRoomUsers) => {
        this.videRoomUsers = this.sortUsers(videoRoomUsers);
      });
    this.janusStatusSubscription = this.janusVideoRoomService
      .getStatusAsObservable()
      .subscribe((status) => {
        if (status === JanusStatus.CONNECTING) {
          this.janusVideoRoomService.changeCameraState(this.cameraEnabled);
          this.janusVideoRoomService.changeMicrophoneState(
            this.microphoneEnabled
          );
          this.initialized = true;
        }
        if (this.connecting && status === JanusStatus.CONNECTED) {
          this.refreshTimerSubscription = interval(
            this.tokenRefreshTime
          ).subscribe(() => {
            this.stompVideoRoomService.refresh();
          });
          this.connecting = false;
        }
        if (this.initialized && status === JanusStatus.DISCONNECTED) {
          this.videoRoomClosed.emit();
        }
      });
    this.stompUserVideoRoomSubscription = this.stompVideoRoomService
      .subscribeToVideoRoomTokenUserQueue(this.channel.id)
      .subscribe((response) => {
        if (response instanceof StompSubscriptionAccepted) {
          //do noting
        } else {
          this.janusVideoRoomService.updateSession(response.data);
        }
      });
    this.stompErrorSubscription = this.stompService
      .getErrorAsObservable()
      .subscribe((res) => {
        const alreadyJoined = res.data.types.some(
          (type) => type.type === ErrorResponseType.VIDEO_ROOM_ALREADY_JOINED
        );
        if (alreadyJoined) {
          this.toastService.displayErrorMessage(
            'channel.videoRoom.alreadyJoined'
          );
          this.videoRoomClosed.emit();
        }
      });
  }
  public ngOnChanges(changes: SimpleChanges): void {
    if (changes['channel'] !== undefined) {
      this.idToMember.clear();
      (changes['channel'].currentValue as ChannelDetailsDto).members.forEach(
        (member) => {
          this.idToMember.set(member.member.id, member.member);
        }
      );
    }
  }
  public ngOnDestroy(): void {
    this.stompUserVideoRoomSubscription?.unsubscribe();
    this.janusStatusSubscription?.unsubscribe();
    this.refreshTimerSubscription?.unsubscribe();
    this.janusPublishersSubscription?.unsubscribe();
    this.userProfileSubscription.unsubscribe();
    this.stompErrorSubscription.unsubscribe();
    this.janusVideoRoomService.detach();
  }

  public toggleCamera() {
    this.cameraEnabled = !this.cameraEnabled;
    this.janusVideoRoomService.changeCameraState(this.cameraEnabled);
  }

  public toggleMicrophone() {
    this.microphoneEnabled = !this.microphoneEnabled;
    this.janusVideoRoomService.changeMicrophoneState(this.microphoneEnabled);
  }

  public toggleFullScreen() {
    if (this.boxRef === undefined) {
      return;
    }
    if (this.fullscreen) {
      this.document.exitFullscreen();
    } else {
      this.boxRef.nativeElement.requestFullscreen();
    }
  }

  protected getBoxClass() {
    const classes: string[] = [];
    if (this.boxStyle !== undefined) {
      classes.push('box--positioned');
    }
    if (this.fullscreen) {
      classes.push('box--fullscreen');
    }
    return classes.join(' ');
  }

  private sortUsers(videoRoomUsers: VideoRoomUser[]): VideoRoomUser[] {
    return videoRoomUsers.sort((a, b) => {
      if (a.id === this.userProfile.id) {
        return -1;
      }
      if (b.id === this.userProfile.id) {
        return 1;
      }
      const aVideoStreamsLength = a.streams.filter(
        (stream) => stream.type === JanusVideoRoomStreamType.VIDEO
      ).length;
      const bVideoStreamsLength = b.streams.filter(
        (stream) => stream.type === JanusVideoRoomStreamType.VIDEO
      ).length;
      if (aVideoStreamsLength > 0 && bVideoStreamsLength === 0) {
        return -1;
      }
      if (bVideoStreamsLength > 0 && aVideoStreamsLength === 0) {
        return 1;
      }
      const aUser = this.idToMember.get(a.id);
      const bUser = this.idToMember.get(b.id);
      if (aUser === undefined && bUser !== undefined) {
        return 1;
      }
      if (bUser === undefined && aUser !== undefined) {
        return -1;
      }
      if (aUser === undefined && bUser === undefined) {
        return 0;
      }
      return aUser!.nickname.localeCompare(bUser!.nickname);
    });
  }

  protected getAudios(): VideoRoomStream[] {
    return this.videRoomUsers
      .flatMap((videoRoomUser) => videoRoomUser.streams)
      .filter((stream) => stream.type === JanusVideoRoomStreamType.AUDIO)
      .filter((stream) => stream.media !== null);
  }

  protected getVideoRoomUserVideoStreams(
    videoRoomUser: VideoRoomUser
  ): VideoRoomStream[] {
    return videoRoomUser.streams
      .filter((stream) => stream.type === JanusVideoRoomStreamType.VIDEO)
      .filter((stream) => stream.media !== null);
  }

  protected getVideoRoomUserMember(
    videoRoomUser: VideoRoomUser
  ): MemberBasicsDto {
    const member = this.idToMember.get(videoRoomUser.id);
    if (member === undefined) {
      this.videoRoomClosed.emit();
      throw Error('Could not find member for video room user id');
    }
    return member;
  }

  protected onBoxGrab(event: PointerEvent): void {
    const target = event.target;
    if (
      this.boxRef === undefined ||
      (target instanceof Element &&
        target.closest(this.dragDisabledSelector) !== null)
    ) {
      return;
    }
    const boxRect = this.boxRef.nativeElement.getBoundingClientRect();
    this.dragBoxDetails = {
      deltaX: event.clientX - boxRect.x,
      deltaY: event.clientY - boxRect.y,
    };
  }

  @HostListener('document:fullscreenchange')
  protected onFullScreenChange() {
    if (this.document.fullscreenElement === null) {
      this.fullscreen = false;
    } else if (this.document.fullscreenElement === this.boxRef?.nativeElement) {
      this.fullscreen = true;
    }
  }

  @HostListener('document:pointerup')
  @HostListener('document:pointercancel')
  protected onBoxRelease(): void {
    this.dragBoxDetails = null;
  }

  @HostListener('document:pointermove', ['$event'])
  protected onContentDrag(event: PointerEvent): void {
    if (
      this.dragBoxDetails === null ||
      this.overlayRef === undefined ||
      this.boxRef === undefined
    ) {
      return;
    }
    const boxRect = this.boxRef.nativeElement.getBoundingClientRect();
    const overlayRect = this.overlayRef.nativeElement.getBoundingClientRect();
    const x = mathUtils.clamp(
      event.clientX - this.dragBoxDetails.deltaX,
      0,
      overlayRect.width - boxRect.width
    );
    const y = mathUtils.clamp(
      event.clientY - this.dragBoxDetails.deltaY,
      0,
      overlayRect.height - boxRect.height
    );
    this.boxStyle = {
      left: `${x}px`,
      top: `${y}px`,
    };
  }
}
