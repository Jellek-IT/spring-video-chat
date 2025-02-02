import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StompResponse } from '../../../core/stomp/model/stomp-response.model';
import {
  PublishArgs,
  StompService,
  StompSubscriptionAccepted,
} from '../../../core/stomp/service/stomp.service';
import { ChannelMessageBasicsDto } from '../../model/message/channel-message-basics-dto.model';
import { CreateChannelMessagePayload } from '../../model/message/create-channel-message-payload.model';

@Injectable({
  providedIn: 'root',
})
export class StompChannelService {
  private readonly stompService = inject(StompService);

  public subscribeToMessage(
    id: string
  ): Observable<
    StompResponse<ChannelMessageBasicsDto> | StompSubscriptionAccepted
  > {
    return this.stompService.subscribe(`/topic/channels.${id}.message`);
  }

  public createMessage(
    id: string,
    payload: CreateChannelMessagePayload,
    args: PublishArgs = {}
  ) {
    this.stompService.publishJson(
      `channels.${id}.create-message`,
      payload,
      args
    );
  }
}
