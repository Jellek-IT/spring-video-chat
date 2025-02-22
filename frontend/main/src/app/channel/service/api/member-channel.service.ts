import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChannelBasicsDto } from '../../model/channel-basics-dto.model';
import { Page } from '../../../shared/model/page.model';
import { ChannelDetailsDto } from '../../model/channel-detais-dto.model';
import { CreateChannelRequest } from '../../model/create-channel-request.model';
import { UpdateChannelRequest } from '../../model/update-channel-request.model';
import { AddChannelMemberRequest } from '../../model/member/add-channel-member-request.model';
import { UpdateChannelMemberRequest } from '../../model/update-channel-member-request.model';
import { KickCHannelMemberRequest } from '../../model/member/kick-channel-memeber-request.model';
import { MemberChannelQueryParamsPageable } from '../../model/member-channel-query-params-pageable.model';
import { environment } from '../../../../environments/environment';
import { MemberChannelMessageQueryParamsPageable } from '../../model/message/member-channel-message-query-params-pageable.model';
import { ChannelMessageBasicsDto } from '../../model/message/channel-message-basics-dto.model';
import { ChannelFileDto } from '../../model/file/channel-file-dto.model';

@Injectable({
  providedIn: 'root',
})
export class MemberChannelService {
  private readonly endpointUrl = `${environment.serverUrl}/member/channels`;
  private readonly httpClient = inject(HttpClient);

  public getAll(
    filter: MemberChannelQueryParamsPageable
  ): Observable<Page<ChannelBasicsDto>> {
    const params = new HttpParams({
      fromObject: filter as { [key: string]: string | boolean | string[] },
    });
    return this.httpClient.get<Page<ChannelBasicsDto>>(`${this.endpointUrl}`, {
      params,
      headers: { 'Content-Type': 'application/json' },
    });
  }

  public getById(id: string): Observable<ChannelDetailsDto> {
    return this.httpClient.get<ChannelDetailsDto>(`${this.endpointUrl}/${id}`, {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  public getAllMessagesById(
    id: string,
    filter: MemberChannelMessageQueryParamsPageable
  ): Observable<Page<ChannelMessageBasicsDto>> {
    const params = new HttpParams({
      fromObject: filter as { [key: string]: string | boolean | string[] },
    });
    return this.httpClient.get<Page<ChannelMessageBasicsDto>>(
      `${this.endpointUrl}/${id}/messages`,
      {
        params,
        headers: { 'Content-Type': 'application/json' },
      }
    );
  }

  public create(request: CreateChannelRequest): Observable<ChannelBasicsDto> {
    return this.httpClient.post<ChannelBasicsDto>(
      `${this.endpointUrl}`,
      request,
      {
        headers: { 'Content-Type': 'application/json' },
      }
    );
  }

  public update(id: string, request: UpdateChannelRequest): Observable<void> {
    return this.httpClient.put<void>(`${this.endpointUrl}/${id}`, request, {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  public addMember(
    id: string,
    request: AddChannelMemberRequest
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${this.endpointUrl}/${id}/add-member`,
      request,
      {
        headers: { 'Content-Type': 'application/json' },
      }
    );
  }

  public updateThumbnail(id: string, file: Blob | null): Observable<void> {
    const formData = new FormData();
    if (file !== null) {
      formData.append('file', file);
    }

    return this.httpClient.post<void>(
      `${this.endpointUrl}/${id}/thumbnail`,
      formData
    );
  }

  public getThumbnail(id: string): Observable<Blob> {
    return this.httpClient.get(`${this.endpointUrl}/${id}/thumbnail`, {
      responseType: 'blob',
    });
  }

  public uploadFile(id: string, file: Blob): Observable<ChannelFileDto> {
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post<ChannelFileDto>(
      `${this.endpointUrl}/${id}/file`,
      formData
    );
  }

  public downloadFie(id: string, fileId: string): Observable<Blob> {
    return this.httpClient.get(`${this.endpointUrl}/${id}/file/${fileId}`, {
      responseType: 'blob',
    });
  }

  public updateMember(
    id: string,
    request: UpdateChannelMemberRequest
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${this.endpointUrl}/${id}/update-member`,
      request,
      {
        headers: { 'Content-Type': 'application/json' },
      }
    );
  }

  public leave(id: string): Observable<void> {
    return this.httpClient.post<void>(`${this.endpointUrl}/${id}/leave`, null, {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  public kickMember(
    id: string,
    request: KickCHannelMemberRequest
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${this.endpointUrl}/${id}/kick-member`,
      request,
      {
        headers: { 'Content-Type': 'application/json' },
      }
    );
  }

  public delete(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.endpointUrl}/${id}`, {
      headers: { 'Content-Type': 'application/json' },
    });
  }
}
