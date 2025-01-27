import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  inject,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { ChannelDetailsDto } from '../../../model/channel-detais-dto.model';
import { CardModule } from 'primeng/card';
import mathUtils from '../../../../shared/utils/math-utils';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { TranslateModule } from '@ngx-translate/core';
import { TooltipModule } from 'primeng/tooltip';
import { interval, Subscription } from 'rxjs';
import { LoaderComponent } from '../../../../shared/component/loader/loader.component';
import { ToastService } from '../../../../shared/service/toast.service';
import { StompSubscriptionAccepted } from '../../../../core/stomp/service/stomp.service';
import { JanusVideoRoomService } from '../../../../core/janus/service/janus-video-room.service';
import { StompVideoRoomService } from '../../../service/api/stomp-video-room.service';
import { JanusStatus } from '../../../../core/janus/enum/jansu-status.enum';
import { JanusVideoRoomStreamType } from '../../../../core/janus/enum/janus-video-room-stream-type.enum';
import { VideoRoomUser } from '../../../../core/janus/model/video-room-user.model';
import { VideoViewComponent } from '../video-view/video-view.component';
import { CarouselModule } from 'primeng/carousel';
import { VideoRoomStream } from '../../../../core/janus/model/internal/video-room-stream.model';

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
  ],
  templateUrl: './video-room-overlay.component.html',
  styleUrl: './video-room-overlay.component.scss',
})
export class VideoRoomOverlayComponent implements OnInit, OnDestroy {
  private readonly tokenRefreshTime = 5 * 60 * 1000; // 5 minutes (token has life of 10 minutes)
  private readonly dragDisabledSelector = '.drag-disabled';
  private readonly stompVideoRoomService = inject(StompVideoRoomService);
  private readonly toastService = inject(ToastService);
  private readonly janusVideoRoomService = inject(JanusVideoRoomService);

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
  private dragBoxDetails: DragBoxDetails | null = null;
  private initialized = false;
  protected readonly streamTypes = JanusVideoRoomStreamType;
  protected boxStyle?: { [key: string]: any };
  protected connecting = true;
  protected users: VideoRoomUser[] = [];
  protected microphoneEnabled = true;
  protected cameraEnabled = true;

  public ngOnInit(): void {
    this.janusVideoRoomService.changeCameraState(this.cameraEnabled);
    this.janusVideoRoomService.changeMicrophoneState(this.microphoneEnabled);
    this.janusPublishersSubscription = this.janusVideoRoomService
      .getUsersAsObservable()
      .subscribe((users) => {
        this.users = this.sortUsers(users);
      });
    this.janusStatusSubscription = this.janusVideoRoomService
      .getStatusAsObservable()
      .subscribe((status) => {
        if (status === JanusStatus.CONNECTING) {
          this.initialized = true;
        }
        if (this.connecting && status === JanusStatus.CONNECTED) {
          this.refreshTimerSubscription = interval(
            this.tokenRefreshTime
          ).subscribe(() => this.stompVideoRoomService.refresh());
          this.connecting = false;
        }
        if (this.initialized && status === JanusStatus.DISCONNECTED) {
          this.videoRoomClosed.emit();
        }
      });
    this.stompUserVideoRoomSubscription = this.stompVideoRoomService
      .subscribeToVideoRoomTokenUserQueue(this.channel.id)
      .subscribe({
        next: (response) => {
          if (response instanceof StompSubscriptionAccepted) {
            //do noting
          } else {
            this.janusVideoRoomService.updateSession(response.data);
          }
        },
        error: () => {
          this.toastService.displayErrorMessage('channel.videoRoom.joinError');
          this.videoRoomClosed.emit();
        },
      });
  }
  public toggleCamera() {
    this.cameraEnabled = !this.cameraEnabled;
    this.janusVideoRoomService.changeCameraState(this.cameraEnabled);
  }

  public toggleMicrophone() {
    this.microphoneEnabled = !this.microphoneEnabled;
    this.janusVideoRoomService.changeMicrophoneState(this.microphoneEnabled);
  }

  public ngOnDestroy(): void {
    this.stompUserVideoRoomSubscription?.unsubscribe();
    this.janusStatusSubscription?.unsubscribe();
    this.refreshTimerSubscription?.unsubscribe();
    this.janusPublishersSubscription?.unsubscribe();
    this.janusVideoRoomService.detach();
  }

  private sortUsers(users: VideoRoomUser[]): VideoRoomUser[] {}

  protected getAudios(): VideoRoomStream[] {
    return this.users
      .flatMap((user) => user.streams)
      .filter((stream) => stream.type === JanusVideoRoomStreamType.AUDIO)
      .filter((stream) => stream.media !== null);
  }

  protected onBoxGrab(event: PointerEvent): void {
    const target = event.target;
    if (
      this.boxRef === undefined ||
      (target instanceof HTMLElement &&
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
