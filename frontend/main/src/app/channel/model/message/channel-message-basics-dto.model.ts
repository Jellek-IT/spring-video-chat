import { MemberBasicsDto } from '../../../user/model/member-basics-dto.model';
import { ChannelFileDto } from '../file/channel-file-dto.model';

export interface ChannelMessageBasicsDto {
  id?: string;
  createdAt: Date;
  text: string;
  sequence?: number;
  member: MemberBasicsDto;
  files: ChannelFileDto[];
}
