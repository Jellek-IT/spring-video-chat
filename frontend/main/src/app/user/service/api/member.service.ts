import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CacheService } from '../../../shared/service/cache.service';
import regexUtils from '../../../shared/utils/regex-utils';

@Injectable({
  providedIn: 'root',
})
export class MemberService {
  private readonly endpointUrl = `${environment.serverUrl}/members`;
  private readonly httpClient = inject(HttpClient);
  private readonly cacheService = inject(CacheService);

  constructor() {
    const firstPart = regexUtils.escape(`${this.endpointUrl}/`);
    const secondPart = regexUtils.escape(`/profile-picture`);
    this.cacheService.addRequestSettings({
      url: new RegExp(`^${firstPart}[a-zA-Z0-9\-]+${secondPart}$`),
      ttl: 10 * 60, // 10 minutes
    });
  }

  public getProfilePicture(id: string): Observable<Blob> {
    return this.httpClient.get(`${this.endpointUrl}/${id}/profile-picture`, {
      responseType: 'blob',
    });
  }
}
