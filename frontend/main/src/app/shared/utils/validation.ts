import { AbstractControl, ValidationErrors, Validators } from '@angular/forms';
const passwordRegex: RegExp =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*)(+=_\-\\}{\[\]|:;"/?.><,`~]).*$/;
const nicknameRegex: RegExp = /^[a-zA-Z0-9 _-]*$/;
const channelNameRegex: RegExp = /^[a-zA-Z0-9 _-]*$/;
const uuidV4Regex: RegExp =
  /^[A-Za-z0-9]{8}-[A-Za-z0-9]{4}-4[A-Za-z0-9]{3}-[A-Za-z0-9]{4}-[A-Za-z0-9]{12}$/;
export const channelMessageTextMaxCharacters = 50000;

export const userPasswordRegExValidator = (
  formControl: AbstractControl
): ValidationErrors | null => {
  return validRegexp(formControl, passwordRegex, { userPassword: true });
};

export const userNicknameRegExValidator = (
  formControl: AbstractControl
): ValidationErrors | null => {
  return validRegexp(formControl, nicknameRegex, { userNickname: true });
};

export const channelNameRegExValidator = (
  formControl: AbstractControl
): ValidationErrors | null => {
  return validRegexp(formControl, channelNameRegex, { channelName: true });
};

const validRegexp = (
  formControl: AbstractControl,
  regExp: RegExp,
  invalidResult: ValidationErrors
): ValidationErrors | null => {
  if (!formControl.value) {
    return null;
  }

  const valid: boolean = regExp.test(formControl.value);

  return valid ? null : invalidResult;
};

export const notBlank = (
  formControl: AbstractControl
): ValidationErrors | null => {
  return (formControl.value || '').trim().length > 0
    ? null
    : { notBlank: true };
};

export const uuidV4Validator = (
  formControl: AbstractControl
): ValidationErrors | null => {
  if (!formControl.value) {
    return null;
  }
  return validRegexp(formControl, uuidV4Regex, { uuidV4: true });
};

export default {
  userPassword: [
    Validators.required,
    userPasswordRegExValidator,
    Validators.minLength(8),
  ],
  userNickname: [
    Validators.required,
    userNicknameRegExValidator,
    Validators.minLength(3),
    Validators.maxLength(50),
  ],
  channelName: [
    Validators.required,
    channelNameRegExValidator,
    Validators.minLength(3),
    Validators.maxLength(50),
  ],
  channelMessageText: [
    notBlank,
    Validators.maxLength(channelMessageTextMaxCharacters),
  ],
  uuidV4: [Validators.required, uuidV4Validator],
};
