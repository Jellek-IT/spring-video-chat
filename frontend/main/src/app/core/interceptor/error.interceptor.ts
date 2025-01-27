import {
  HttpErrorResponse,
  HttpEvent,
  HttpInterceptorFn,
  HttpStatusCode,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, EMPTY, Observable, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  const handleErrorWithRedirect = (
    redirectPath: string
  ): Observable<HttpEvent<any>> => {
    router.navigate([redirectPath]);
    return EMPTY;
  };

  const handleError = (error: HttpEvent<any>) => {
    if (error instanceof HttpErrorResponse) {
      switch ((error as HttpErrorResponse).status) {
        case HttpStatusCode.Unauthorized:
          return throwError(() => error);
        case HttpStatusCode.InternalServerError:
          return throwError(() => error);
        case HttpStatusCode.BadGateway:
          return handleErrorWithRedirect('/error/bad-gateway');
        case 0:
        case HttpStatusCode.ServiceUnavailable:
          return handleErrorWithRedirect('/error/service-unavailable');
        case HttpStatusCode.GatewayTimeout:
          return handleErrorWithRedirect('/error/timeout');
        default:
          return throwError(() => error);
      }
    } else {
      return throwError(() => error);
    }
  };
  return next(req).pipe(catchError(handleError));
};
