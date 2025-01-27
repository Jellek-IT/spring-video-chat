import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterMemberRequest } from '../../model/register-member-request.model';
import { MemberBasicsDto } from '../../model/member-basics-dto.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PublicMemberService {
  private readonly endpointUrl = `${environment.serverUrl}/public/members`;
  private readonly httpClient = inject(HttpClient);

  public register(
    registerMemberRequest: RegisterMemberRequest
  ): Observable<MemberBasicsDto> {
    return this.httpClient.post<MemberBasicsDto>(
      `${this.endpointUrl}`,
      registerMemberRequest,
      {
        headers: { 'Content-Type': 'application/json' },
      }
    );
  }
}
