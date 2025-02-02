import { inject, Injectable } from '@angular/core';
import {
  PublishArgs,
  StompService,
  StompSubscriptionAccepted,
} from '../../../core/stomp/service/stomp.service';
import { Observable } from 'rxjs';
import { StompResponse } from '../../../core/stomp/model/stomp-response.model';
import { VideoRoomSessionDetailsDto } from '../../../core/janus/model/video-room-session-details-dto.model';

@Injectable({
  providedIn: 'root',
})
export class StompVideoRoomService {
  private readonly stompService = inject(StompService);

  public subscribeToVideoRoomTokenUserQueue(
    id: string
  ): Observable<
    StompResponse<VideoRoomSessionDetailsDto> | StompSubscriptionAccepted
  > {
    return this.stompService.subscribe(
      `/user/exchange/amq.direct/channels.${id}.video-room.token`
    );
  }

  public refresh(args: PublishArgs = {}) {
    this.stompService.publishJson(`video-rooms.active.refresh`, null, args);
  }
}
