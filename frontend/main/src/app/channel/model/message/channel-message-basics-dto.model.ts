import { MemberBasicsDto } from '../../../user/model/member-basics-dto.model';

export interface ChannelMessageBasicsDto {
  id?: string;
  createdAt: Date;
  text: string;
  sequence?: number;
  member: MemberBasicsDto;
}
