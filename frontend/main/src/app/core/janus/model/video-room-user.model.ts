import { VideoRoomStream } from './internal/video-room-stream.model';

export interface VideoRoomUser {
  id: string;
  streams: VideoRoomStream[];
}
