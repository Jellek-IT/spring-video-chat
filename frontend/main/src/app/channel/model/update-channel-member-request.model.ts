import { ChannelMemberRight } from '../enum/channel-member-right.enum';

export interface UpdateChannelMemberRequest {
  memberId: string;
  rights: ChannelMemberRight[];
}
