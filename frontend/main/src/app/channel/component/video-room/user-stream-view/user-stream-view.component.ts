import { Component, Input } from '@angular/core';
import { VideoRoomStream } from '../../../../core/janus/model/internal/video-room-stream.model';
import { ChannelMemberDto } from '../../../model/member/channel-member-dto.model';
import { ChannelDetailsDto } from '../../../model/channel-detais-dto.model';
import { CommonModule } from '@angular/common';
import { ChannelMemberProfilePictureComponent } from '../../channel-member-profile-picture/channel-member-profile-picture.component';
import { VideoViewComponent } from '../video-view/video-view.component';

@Component({
  selector: 'app-user-stream-view',
  imports: [
    CommonModule,
    ChannelMemberProfilePictureComponent,
    VideoViewComponent,
  ],
  templateUrl: './user-stream-view.component.html',
  styleUrl: './user-stream-view.component.scss',
})
export class UserStreamViewComponent {
  @Input({ required: true }) public streams!: VideoRoomStream[];
  @Input({ required: true }) public member!: ChannelMemberDto;
  @Input({ required: true }) public channel!: ChannelDetailsDto;
  @Input({ required: true }) public showDetails!: boolean;

  protected getViewClasses(): string[] {
    const classes: string[] = [];
    if (this.streams.length === 0) {
      classes.push('view--no-streams');
    }
    if (this.showDetails === true) {
      classes.push('view--details');
    }
    return classes;
  }
}
