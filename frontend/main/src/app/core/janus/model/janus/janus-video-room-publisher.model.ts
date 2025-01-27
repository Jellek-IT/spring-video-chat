import { PublisherJanusVideoRoomStream } from './janus-video-room-stream.model';

export interface JanusVideoRoomPublisher {
  id: string;
  streams?: PublisherJanusVideoRoomStream[];
}
