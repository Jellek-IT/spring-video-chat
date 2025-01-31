import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { CacheService } from '../../shared/service/cache.service';
import { catchError, filter, finalize, of, tap, throwError } from 'rxjs';

export const cacheInterceptor: HttpInterceptorFn = (req, next) => {
  const cacheService = inject(CacheService);

  const cacheRequestSettings = cacheService.getRequestSettings(req);

  if (cacheRequestSettings === undefined) {
    return next(req);
  }

  const previousRequest = cacheService.getPreviousRequest(req);
  if (previousRequest !== undefined) {
    if (previousRequest.response !== null) {
      return of(previousRequest.response);
    }
    return previousRequest.response$;
  }

  const cacheRequestDetails = cacheService.addRequest(
    req,
    cacheRequestSettings
  );

  return next(req)
    .pipe(
      filter((res) => res instanceof HttpResponse),
      tap((res) => {
        if (!cacheRequestDetails.response$.closed) {
          cacheRequestDetails.response$.next(res);
        }
        cacheRequestDetails.response = res;
      })
    )
    .pipe(
      catchError((e) => {
        if (!cacheRequestDetails.response$.closed) {
          cacheRequestDetails.response$.error(e);
        }
        cacheService.deleteRequest(req);
        return throwError(() => e);
      })
    )
    .pipe(
      finalize(() => {
        cacheRequestDetails.response$.complete();
      })
    );
};
