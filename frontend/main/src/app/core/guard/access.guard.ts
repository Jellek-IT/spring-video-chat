import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';
import Keycloak from 'keycloak-js';
import { map, of } from 'rxjs';
import { UserProfileDto } from '../../user/model/user-profile-dto.model';
import { UserType } from '../../user/enum/user-type.enum';
import { CurrentUserService } from '../../user/service/current-user.service';
import { environment } from '../../../environments/environment';

export const accessGuard: CanMatchFn = (route, segments) => {
  const keycloak = inject(Keycloak);
  const currentUserService = inject(CurrentUserService);

  const userHasAccess = (userProfileDto: UserProfileDto): boolean => {
    const allowedUserTypes =
      (route.data?.['allowedUserTypes'] as UserType[] | undefined) ?? [];
    return allowedUserTypes.includes(userProfileDto.type);
  };

  const authenticated = keycloak?.authenticated ?? false;
  if (!authenticated) {
    keycloak.login({ redirectUri: environment.url });
    return of(false);
  }
  return currentUserService
    .getUserProfile()
    .pipe(map((userProfileDto) => userHasAccess(userProfileDto)));
};
