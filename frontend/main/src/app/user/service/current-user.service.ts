import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import {
  BehaviorSubject,
  distinctUntilChanged,
  map,
  mergeMap,
  Observable,
  of,
  skip,
  tap,
} from 'rxjs';
import { UserProfileDto } from '../../user/model/user-profile-dto.model';
import { HttpClient } from '@angular/common/http';
import { MemberProfileDto } from '../model/member-profile-dto.model';
import { UserType } from '../enum/user-type.enum';

@Injectable({
  providedIn: 'root',
})
export class CurrentUserService {
  private readonly endpointUrl = `${environment.serverUrl}/users/profile`;
  private readonly httpClient = inject(HttpClient);
  private readonly userProfileSubject$: BehaviorSubject<UserProfileDto> =
    new BehaviorSubject<UserProfileDto>(undefined as any);

  private getUserProfileRequest(): Observable<UserProfileDto> {
    return this.httpClient.get<UserProfileDto>(this.endpointUrl, {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  public getUserProfile(): Observable<UserProfileDto> {
    const loadUserProfile = (
      userProfile: UserProfileDto
    ): Observable<UserProfileDto> => {
      if (userProfile !== undefined && userProfile !== null) {
        return of(userProfile);
      } else {
        return this.getUserProfileRequest().pipe(
          tap((value) => this.updateUserProfile(value))
        );
      }
    };
    return of(this.userProfileSubject$.value).pipe(mergeMap(loadUserProfile));
  }

  private updateUserProfile(userProfile: UserProfileDto): void {
    this.userProfileSubject$.next(userProfile);
  }

  public getUserProfileAsObservable(): Observable<UserProfileDto> {
    if (this.userProfileSubject$.value === undefined) {
      return this.userProfileSubject$
        .asObservable()
        .pipe(skip(1), distinctUntilChanged());
    }
    return this.userProfileSubject$.asObservable().pipe(distinctUntilChanged());
  }

  public getMemberProfileAsObservable(): Observable<MemberProfileDto> {
    return this.getUserProfileAsObservable().pipe(
      map((userProfile) => {
        if (userProfile.type == UserType.MEMBER) {
          return userProfile as MemberProfileDto;
        }
        throw Error('User type is not Member');
      })
    );
  }

  public updateProfilePicture(file: Blob | null): Observable<void> {
    const formData = new FormData();
    if (file !== null) {
      formData.append('file', file);
    }

    return this.httpClient
      .post<void>(`${this.endpointUrl}/profile-picture`, formData)
      .pipe(
        tap(() => {
          this.updateUserProfile({
            ...this.userProfileSubject$.value,
            hasProfilePicture: file !== null,
          });
        })
      );
  }

  public getProfilePicture(): Observable<Blob> {
    return this.httpClient.get(`${this.endpointUrl}/profile-picture`, {
      responseType: 'blob',
    });
  }
}
