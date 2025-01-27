import { VideoRoomAuthTokenDto } from './video-room-auth-token-dto.model';

export interface VideoRoomSessionDetailsDto {
  channelId: string;
  videoRoomId: string;
  authToken: VideoRoomAuthTokenDto;
  videoRoomAccessToken: string;
}
