import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ContainerComponent } from '../../../shared/component/container/container.component';
import { CardModule } from 'primeng/card';
import { TypographyComponent } from '../../../shared/component/typography/typography.component';
import { ActivatedRoute, Params } from '@angular/router';
import { ChannelDetailsDto } from '../../model/channel-detais-dto.model';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ChannelChatComponent } from '../../component/chat/channel-chat/channel-chat.component';
import { MemberChannelService } from '../../service/api/member-channel.service';
import { ButtonModule } from 'primeng/button';
import { VideoRoomOverlayComponent } from '../../component/video-room/video-room-overlay/video-room-overlay.component';
import { TooltipModule } from 'primeng/tooltip';
import { TranslateModule } from '@ngx-translate/core';

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
  ],
  templateUrl: './channel-details.component.html',
  styleUrl: './channel-details.component.scss',
})
export class ChannelDetailsComponent implements OnInit {
  private readonly memberChannelService = inject(MemberChannelService);
  private readonly activatedRoute = inject(ActivatedRoute);

  private loadChannelSubscription: Subscription | null = null;
  protected channel: ChannelDetailsDto | null = null;
  protected showVideoRoom = false;

  public ngOnInit(): void {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.loadChannel(params['id']);
    });
  }

  private loadChannel(id: string): void {
    this.channel = null;
    //todo: change to false
    this.showVideoRoom = true;
    this.loadChannelSubscription && this.loadChannelSubscription.unsubscribe();
    this.loadChannelSubscription = this.memberChannelService
      .getById(id)
      .subscribe((res) => {
        this.channel = res;
      });
  }

  protected toggleShowVideoRoom() {
    this.showVideoRoom = !this.showVideoRoom;
  }

  protected closeVideoRoom() {
    this.showVideoRoom = false;
  }
}
