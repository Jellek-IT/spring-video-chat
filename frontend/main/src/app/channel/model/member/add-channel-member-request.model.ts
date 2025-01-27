import { MemberIdDto } from '../../../user/model/member-id-dto.model';
import { ChannelMemberRights } from '../../enum/channel-member-rights.enum';

export interface AddChannelMemberRequest {
  member: MemberIdDto;
  rights: ChannelMemberRights[];
}
