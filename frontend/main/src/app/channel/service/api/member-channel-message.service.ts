import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { MemberChannelMessageQueryParamsPageable } from '../../model/message/member-channel-message-query-params-pageable.model';
import { Observable } from 'rxjs';
import { Page } from '../../../shared/model/page.model';
import { ChannelMessageBasicsDto } from '../../model/message/channel-message-basics-dto.model';

@Injectable({
  providedIn: 'root',
})
export class MemberChannelMessageService {
  private readonly endpointUrl = (channelId: string) =>
    `${environment.serverUrl}/member/channels/${channelId}/messages`;
  private readonly httpClient = inject(HttpClient);

  public getAll(
    channelId: string,
    filter: MemberChannelMessageQueryParamsPageable
  ): Observable<Page<ChannelMessageBasicsDto>> {
    const params = new HttpParams({
      fromObject: filter as { [key: string]: string | boolean | string[] },
    });
    return this.httpClient.get<Page<ChannelMessageBasicsDto>>(
      `${this.endpointUrl(channelId)}`,
      {
        params,
        headers: { 'Content-Type': 'application/json' },
      }
    );
  }
}
