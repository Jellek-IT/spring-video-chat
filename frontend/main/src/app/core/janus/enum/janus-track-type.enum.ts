import { JanusVideoRoomStreamType } from './janus-video-room-stream-type.enum';

export enum JanusTrackType {
  VIDEO = 'video',
  SCREEN = 'screen',
  AUDIO = 'audio',
  DATA = 'data',
}

export const toJanusStreamType = (
  trackType: JanusTrackType
): JanusVideoRoomStreamType => {
  switch (trackType) {
    case JanusTrackType.VIDEO:
      return JanusVideoRoomStreamType.VIDEO;
    case JanusTrackType.SCREEN:
      return JanusVideoRoomStreamType.VIDEO;
    case JanusTrackType.AUDIO:
      return JanusVideoRoomStreamType.AUDIO;
    case JanusTrackType.DATA:
      return JanusVideoRoomStreamType.DATA;
  }
};

export default {
  toJanusStreamType,
};
