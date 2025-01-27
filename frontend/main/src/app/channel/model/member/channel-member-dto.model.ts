import { MemberBasicsDto } from '../../../user/model/member-basics-dto.model';
import { ChannelMemberRights } from '../../enum/channel-member-rights.enum';

export interface ChannelMemberDto {
  member: MemberBasicsDto;
  rights: ChannelMemberRights[];
  deleted: boolean;
}
