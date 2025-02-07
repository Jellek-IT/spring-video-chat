import { MemberIdDto } from '../../../user/model/member-id-dto.model';
import { ChannelMemberRight } from '../../enum/channel-member-right.enum';

export interface AddChannelMemberRequest {
  member: MemberIdDto;
  rights: ChannelMemberRight[];
}
