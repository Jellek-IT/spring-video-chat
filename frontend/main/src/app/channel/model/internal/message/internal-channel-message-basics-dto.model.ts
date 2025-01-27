import { MessageNode } from '../../../service/message-dom-tag-processor.service';
import { ChannelMessageBasicsDto } from '../../message/channel-message-basics-dto.model';

export interface InternalChannelMessageBasicsDto
  extends ChannelMessageBasicsDto {
  textNodes: MessageNode[];
}
