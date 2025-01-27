export enum ErrorResponseType {
	CHANNEL_OPERATION_NOT_ENOUGH_RIGHTS = 'CHANNEL_OPERATION_NOT_ENOUGH_RIGHTS',
	CHANNEL_MEMBER_ALREADY_ADDED = 'CHANNEL_MEMBER_ALREADY_ADDED',
	UNIQUE_EMAIL = 'UNIQUE_EMAIL',
	UNIQUE_NICKNAME = 'UNIQUE_NICKNAME'
}

export const getTranslationKey = (errorResponseType: ErrorResponseType) => {
	return `error.responseType.${errorResponseType}`;
};
export default {
	getTranslationKey
};
