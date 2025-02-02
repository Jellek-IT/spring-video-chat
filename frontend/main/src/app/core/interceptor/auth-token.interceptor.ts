import {
  HttpErrorResponse,
  HttpEvent,
  HttpInterceptorFn,
  HttpStatusCode,
} from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { inject } from '@angular/core';
import Keycloak from 'keycloak-js';
import { catchError, from, mergeMap, of, throwError } from 'rxjs';
import { addAuthorizationHeader } from 'keycloak-angular';

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(environment.serverUrl)) {
    return next(req);
  }
  const keycloak = inject(Keycloak);

  const handle401Error = (error: HttpEvent<any>) => {
    if (
      error instanceof HttpErrorResponse &&
      error.status === HttpStatusCode.Unauthorized
    ) {
      keycloak.logout();
    }
    return throwError(() => error);
  };

  return from(keycloak.updateToken())
    .pipe(
      catchError(() => {
        return of(null);
      })
    )
    .pipe(
      mergeMap(() =>
        keycloak.authenticated
          ? addAuthorizationHeader(req, next, keycloak, {
              bearerPrefix: 'Bearer',
            })
          : next(req)
      )
    )
    .pipe(catchError(handle401Error));
};
