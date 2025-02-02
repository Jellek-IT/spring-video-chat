import { ChannelFileIdDto } from '../file/channel-file-id-dto.mode';

export interface CreateChannelMessagePayload {
  text: string;
  files: ChannelFileIdDto[];
}
