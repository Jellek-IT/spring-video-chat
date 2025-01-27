import { UserProfileDto } from './user-profile-dto.model';

export interface MemberProfileDto extends UserProfileDto {
	nickname: string;
}
