import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

@Component({
	selector: 'app-validation-message',
	imports: [CommonModule, TranslateModule],
	templateUrl: './validation-message.component.html',
	styleUrl: './validation-message.component.scss'
})
export class ValidationMessageComponent {
	@Input({ required: true }) control!: AbstractControl;
}
