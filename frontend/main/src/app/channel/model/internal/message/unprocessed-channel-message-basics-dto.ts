import { InternalMessageStatus } from '../../../enum/internal/internal-message-status.enum';
import { InternalChannelMessageBasicsDto } from './internal-channel-message-basics-dto.model';

export interface UnprocessedChannelMessageBasicsDto
  extends InternalChannelMessageBasicsDto {
  transactionId: string;
  internalStatus: InternalMessageStatus;
}
