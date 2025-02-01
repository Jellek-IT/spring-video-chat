import { ChannelFileDto } from '../file/channel-file-dto.model';
import { ChannelFileIdDto } from '../file/channel-file-id-dto.mode';

export interface CreateChannelMessagePayload {
  text: string;
  files: ChannelFileIdDto[];
}
