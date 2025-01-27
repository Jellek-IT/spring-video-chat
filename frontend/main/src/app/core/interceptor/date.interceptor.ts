import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { tap } from 'rxjs';

export const dateInterceptor: HttpInterceptorFn = (req, next) => {
	const iso8601DateOnly: RegExp = /^\d{4}-\d{2}-\d{2}$/;
	const iso8601FullDate: RegExp = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d+)?Z$/;

	const convertToDate = (body: any): any => {
		if (body === null || body === undefined) {
			return body;
		}

		if (typeof body !== 'object') {
			return body;
		}

		Object.keys(body).forEach((key: string) => {
			const value = body[key];
			if (isIso8601DateOnly(value) || isIso86012FullDate(value)) {
				body[key] = new Date(value);
			} else if (typeof value === 'object') {
				convertToDate(value);
			}
		});
	};

	const isIso8601DateOnly = (value?: string | null): boolean => {
		if (value === null || value === undefined) {
			return false;
		}
		return iso8601DateOnly.test(value);
	};

	const isIso86012FullDate = (value?: string | null): boolean => {
		if (value === null || value === undefined) {
			return false;
		}
		return iso8601FullDate.test(value);
	};

	return next(req).pipe(
		tap(event => {
			if (event instanceof HttpResponse) {
				const body: any = event.body;
				convertToDate(body);
			}
		})
	);
};
