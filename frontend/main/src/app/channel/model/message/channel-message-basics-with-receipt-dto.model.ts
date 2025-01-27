import { ChannelMessageBasicsDto } from './channel-message-basics-dto.model';

export interface ChannelMessageBasicsWithTransactionId
  extends ChannelMessageBasicsDto {
  transactionId: string;
}
