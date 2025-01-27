import { inject, Injectable } from '@angular/core';
import { ToastService } from '../../shared/service/toast.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, from, map, of } from 'rxjs';
import ExceptionResponseProp, { ExceptionResponse } from '../model/exception-response.model';
import ErrorResponseTypeProp, { ErrorResponseType } from '../enum/error-response-type.enum';
import { TranslateService } from '@ngx-translate/core';
@Injectable({
	providedIn: 'root'
})
export class EndpointErrorService {
	private readonly toastService = inject(ToastService);
	private readonly translateService = inject(TranslateService);

	public handle(error: HttpErrorResponse) {
		this.getContentAsExceptionResponse(error).subscribe(content => {
			const customErrors = ExceptionResponseProp.getExistingTypes(content);
			if (customErrors.length > 0) {
				customErrors.forEach(errorType =>
					this.toastService.displayErrorMessage(ErrorResponseTypeProp.getTranslationKey(errorType))
				);
			} else {
				this.toastService.displayErrorMessage('error.processingError');
			}
		});
	}

	public getErrorText(error: HttpErrorResponse): Observable<string> {
		return this.getContentAsExceptionResponse(error).pipe(
			map(content => {
				const types = ExceptionResponseProp.getExistingTypes(content);
				return types.length == 0
					? this.translateService.instant('error.processingError')
					: types
							.map(errorType => this.translateService.instant(ErrorResponseTypeProp.getTranslationKey(errorType)))
							.join('<br/>');
			})
		);
	}

	public getContentAsExceptionResponse(error: HttpErrorResponse): Observable<ExceptionResponse | null> {
		return this.getContent(error).pipe(
			map(content => (content !== null && 'types' in content ? (content as ExceptionResponse) : null))
		);
	}

	public getContent(error: HttpErrorResponse): Observable<Object | null> {
		if (typeof error.error !== 'object') {
			return of(null);
		}
		if (error.error instanceof Blob) {
			return from(error.error.text()).pipe(map(value => JSON.parse(value)));
		}
		return of(error.error);
	}
}
