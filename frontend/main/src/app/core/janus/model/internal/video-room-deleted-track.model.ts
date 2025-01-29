export interface VideoRoomDeletedTrack {
  track: MediaStreamTrack;
  mid: string;
  remote: boolean;
  expireAt: Date;
}
