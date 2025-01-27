import { StompResponseType } from '../enum/stomp-response-type.enum';

export interface StompResponse<T> {
  responseType: StompResponseType;
  transactionId?: string;
  data: T;
}
