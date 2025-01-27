import { ChannelBasicsDto } from './channel-basics-dto.model';
import { ChannelMemberDto } from './member/channel-member-dto.model';

export interface ChannelDetailsDto extends ChannelBasicsDto {
  members: ChannelMemberDto[];
}
