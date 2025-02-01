import { ChannelFileType } from '../../enum/channel-file-type.enum';

export interface ChannelFileDto {
  id: string;
  createdAt: Date;
  type: ChannelFileType;
}
