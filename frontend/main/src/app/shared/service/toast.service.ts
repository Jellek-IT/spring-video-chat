import { inject, Injectable } from '@angular/core';
import { InterpolationParameters, TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';

@Injectable({
	providedIn: 'root'
})
export class ToastService {
	private readonly messageService = inject(MessageService);
	private readonly translateService = inject(TranslateService);

	public displaySuccessMessage(descriptionTranslationKey: string, interpolateParams?: InterpolationParameters): void {
		this.displayMessage('success', descriptionTranslationKey, interpolateParams);
	}

	public displayInfoMessage(descriptionTranslationKey: string, interpolateParams?: InterpolationParameters): void {
		this.displayMessage('info', descriptionTranslationKey, interpolateParams);
	}

	public displayWarnMessage(descriptionTranslationKey: string, interpolateParams?: InterpolationParameters): void {
		this.displayMessage('warn', descriptionTranslationKey, interpolateParams);
	}

	public displayErrorMessage(descriptionTranslationKey: string, interpolateParams?: InterpolationParameters): void {
		this.displayMessage('error', descriptionTranslationKey, interpolateParams);
	}

	private displayMessage(
		severity: string,
		descriptionTranslationKey: string,
		interpolateParams?: InterpolationParameters,
		life = 5000
	): void {
		this.messageService.add({
			severity,
			life,
			detail: this.translateService.instant(descriptionTranslationKey, interpolateParams)
		});
	}
}
