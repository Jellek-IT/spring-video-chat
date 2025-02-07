import { MemberBasicsDto } from '../../../user/model/member-basics-dto.model';
import { ChannelMemberRight } from '../../enum/channel-member-right.enum';

export interface ChannelMemberDto {
  member: MemberBasicsDto;
  rights: ChannelMemberRight[];
  deleted: boolean;
}
