import { VideoRoomStream } from './video-room-stream.model';

export interface VideoRoomPublisher {
  id: string;
  streams: Map<string, VideoRoomStream>;
}
