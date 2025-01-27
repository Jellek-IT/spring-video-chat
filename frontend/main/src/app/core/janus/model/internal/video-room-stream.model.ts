import { JanusVideoRoomStreamType } from '../../enum/janus-video-room-stream-type.enum';
import { VideoRoomStreamMedia } from './video-room-stream-media.model';

export interface VideoRoomStream {
  userId: string;
  mid: string;
  userMid: string;
  type: JanusVideoRoomStreamType;
  media: VideoRoomStreamMedia | null;
}
