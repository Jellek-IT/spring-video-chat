import { AbstractControl, FormControl, FormGroup } from '@angular/forms';

export const validate = (formGroup: FormGroup): void => {
	formGroup.markAllAsTouched();

	Object.keys(formGroup.controls).forEach((field: string) => {
		const control: AbstractControl | null = formGroup.get(field);
		if (control instanceof FormControl) {
			control.markAsDirty({ onlySelf: true });
		} else if (control instanceof FormGroup) {
			validate(control);
		}
	});
};

export default {
	validate
};
