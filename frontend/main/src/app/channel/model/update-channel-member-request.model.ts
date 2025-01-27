import { ChannelMemberRights } from '../enum/channel-member-rights.enum';

export interface UpdateChannelMemberRequest {
	memberId: string;
	rights: ChannelMemberRights[];
}
