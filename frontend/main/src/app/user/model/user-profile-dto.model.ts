import { UserType } from '../enum/user-type.enum';

export interface UserProfileDto {
	readonly id: string;
	readonly email: string;
	readonly type: UserType;
	readonly emailVerified: boolean;
}
