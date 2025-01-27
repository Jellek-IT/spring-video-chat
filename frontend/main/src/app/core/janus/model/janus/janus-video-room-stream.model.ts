import { JanusVideoRoomStreamType } from '../../enum/janus-video-room-stream-type.enum';

export interface SubscriberJanusVideoRoomStream {
  type: JanusVideoRoomStreamType;
  // global for subscriber mid
  mid: string;
  feed_id: string;
  feed_mid: string;
  active?: boolean;
}

export interface PublisherJanusVideoRoomStream {
  type: JanusVideoRoomStreamType;
  // local mid per publisher
  mid: string;
  mindex?: string;
  dummy?: boolean;
  disabled?: boolean;
}

export interface PublisherJanusVideoRoomStreamWithUserId
  extends PublisherJanusVideoRoomStream {
  user_id: string;
}

export interface PublisherVideoJanusVideoRoomStream
  extends PublisherJanusVideoRoomStream {
  type: JanusVideoRoomStreamType.VIDEO;
  codec?: 'vp8' | 'vp9' | 'h264' | 'av1' | 'h265';
}

export interface PublisherAudioJanusVideoRoomStream
  extends PublisherJanusVideoRoomStream {
  type: JanusVideoRoomStreamType.AUDIO;
  talking: boolean;
  codec?: 'opus' | 'g722' | 'pcmu' | 'pcma' | 'isac32' | 'isac16';
}

export interface SubscribeJanusVideoRoomStream {
  mid: string;
  feed: string;
}
