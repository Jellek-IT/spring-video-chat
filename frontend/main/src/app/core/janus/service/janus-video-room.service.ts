import { inject, Injectable } from '@angular/core';
import { VideoRoomSessionDetailsDto } from '../model/video-room-session-details-dto.model';
import Janus, { JanusJS } from '../../../packages/janus';
import { environment } from '../../../../environments/environment';
import { JanusVideoRoomMessageType } from '../enum/janus-video-room-message-type.enum';
import adapter from 'webrtc-adapter';
import { JanusVideoRoomEvent } from '../enum/janus-video-room-event.enum';
import { JanusVideoRoomPublisher } from '../model/janus/janus-video-room-publisher.model';
import { ToastService } from '../../../shared/service/toast.service';
import { VideoRoomStream } from '../model/internal/video-room-stream.model';
import { VideoRoomPublisher } from '../model/internal/video-room-publisher.model';
import {
  PublisherJanusVideoRoomStream,
  SubscriberJanusVideoRoomStream,
  PublisherVideoJanusVideoRoomStream,
  PublisherJanusVideoRoomStreamWithUserId,
  SubscribeJanusVideoRoomStream,
} from '../model/janus/janus-video-room-stream.model';
import { JanusVideoRoomStreamType } from '../enum/janus-video-room-stream-type.enum';
import janusTrackTypeEnum, {
  JanusTrackType,
} from '../enum/janus-track-type.enum';
import {
  BehaviorSubject,
  debounceTime,
  interval,
  Observable,
  Subject,
} from 'rxjs';
import { JanusStatus } from '../enum/jansu-status.enum';
import { VideoRoomUser } from '../model/video-room-user.model';
import { LocalTrackType } from '../enum/local-track-type.enum';
import { VideoRoomDeletedTrack } from '../model/internal/video-room-deleted-track.model';

@Injectable({
  providedIn: 'root',
})
export class JanusVideoRoomService {
  private readonly videoRoomPluginName = 'janus.plugin.videoroom';
  private readonly janusVideoRoomErrorNoSuchRoom = '426';
  private readonly deleteTrackInterval = 1000; // 1 seconds
  private readonly deletedTrackLifeTime = 2 * 1000; // 2 seconds
  private readonly debug = true;

  private readonly toastService = inject(ToastService);
  private sessionDetails: VideoRoomSessionDetailsDto | null = null;
  private janus: Janus | null = null;
  private videoRoomPublisherHandle: JanusJS.PluginHandle | null = null;
  private videoRoomSubscriberHandlePromise: Promise<JanusJS.PluginHandle> | null =
    null;
  private privateId: string | null = null;
  private currentUserId: string | null = null;
  private userIdToPublisher: Map<string, VideoRoomPublisher> = new Map();
  private midToStream: Map<string, VideoRoomStream> = new Map();
  private trackIdToDeletedTrack: Map<string, VideoRoomDeletedTrack> = new Map();
  private activeLocalTracks: Map<LocalTrackType, JanusJS.TrackOption> =
    new Map();
  private enabledLocalTracks: Set<LocalTrackType> = new Set();
  private firstFeedPublished = false;
  private readonly users$ = new Subject<VideoRoomUser[]>();
  private readonly status$ = new BehaviorSubject<JanusStatus>(
    JanusStatus.DISCONNECTED
  );

  constructor() {
    interval(this.deleteTrackInterval).subscribe(() =>
      this.clearExpiredDeletedTracks()
    );
  }

  public updateSession(sessionDetails: VideoRoomSessionDetailsDto): void {
    if (
      this.sessionDetails?.videoRoomAccessToken !==
      sessionDetails.videoRoomAccessToken
    ) {
      if (this.status$.value !== JanusStatus.DISCONNECTED) {
        this.detach();
      }
      this.sessionDetails = sessionDetails;
      this.attach();
    } else {
      this.sessionDetails = sessionDetails;
    }
  }

  public changeCameraState(enabled: boolean): void {
    if (enabled) {
      this.enabledLocalTracks.add(LocalTrackType.VIDEO);
    } else {
      this.enabledLocalTracks.delete(LocalTrackType.VIDEO);
    }
    if (this.firstFeedPublished) {
      this.updateFeed();
    }
  }

  public changeMicrophoneState(enabled: boolean): void {
    if (enabled) {
      this.enabledLocalTracks.add(LocalTrackType.MICROPHONE);
    } else {
      this.enabledLocalTracks.delete(LocalTrackType.MICROPHONE);
    }
    if (this.firstFeedPublished) {
      this.updateFeed();
    }
  }

  private async attach(): Promise<void> {
    await this.waitForDisconnected();
    this.videoRoomPublisherHandle = null;
    this.videoRoomSubscriberHandlePromise = null;
    this.privateId = null;
    Array.from(this.midToStream.values())
      .filter((stream) => stream.media !== null)
      .forEach((stream) =>
        stream.media!.media.getTracks().forEach((track) => track.stop())
      );
    this.userIdToPublisher.clear();
    this.midToStream.clear();
    this.trackIdToDeletedTrack.clear();
    this.activeLocalTracks.clear();
    this.enabledLocalTracks.clear();
    this.janus = null;
    this.firstFeedPublished = false;
    this.status$.next(JanusStatus.CONNECTING);
    // initialized library will call callback immediately
    Janus.init({
      debug: this.debug ? 'all' : false,
      dependencies: Janus.useDefaultDependencies({ adapter: adapter }),
      callback: () => {
        this.janus = new Janus({
          server: environment.janusWsUrl,
          token: () => this.sessionDetails!.authToken.value,
          success: () => this.attachVideoRoom(),
          error: (error) => {
            this.logError(error);
          },
          destroyed: () => {
            this.status$.next(JanusStatus.DISCONNECTED);
          },
        });
      },
    });
  }

  private async waitForDisconnected(): Promise<void> {
    return new Promise((resolve) => {
      if (this.status$.value === JanusStatus.DISCONNECTED) {
        resolve();
      }
      const subscription = this.getStatusAsObservable().subscribe((status) => {
        if (status === JanusStatus.DISCONNECTED) {
          setTimeout(() => {
            subscription.unsubscribe();
            resolve();
          });
        }
      });
    });
  }

  public async detach(): Promise<void> {
    if (this.janus === null || !this.janus.isConnected()) {
      this.janus = null;
      this.status$.next(JanusStatus.DISCONNECTED);
      return;
    }
    this.status$.next(JanusStatus.DISCONNECTING);
    if (this.videoRoomPublisherHandle !== null) {
      this.videoRoomPublisherHandle.hangup();
    }
    if (this.videoRoomSubscriberHandlePromise !== null) {
      (await this.videoRoomSubscriberHandlePromise).hangup();
    }
    this.janus?.destroy({});
  }

  public getUsersAsObservable(): Observable<VideoRoomUser[]> {
    return this.users$.asObservable().pipe(debounceTime(150));
  }

  public getStatusAsObservable(): Observable<JanusStatus> {
    return this.status$.asObservable();
  }

  private attachVideoRoom(): void {
    if (this.janus === null) {
      return;
    }
    this.janus.attach({
      plugin: this.videoRoomPluginName,
      success: (pluginHandle) => {
        this.videoRoomPublisherHandle = pluginHandle;
        this.videoRoomPublisherHandle.send({
          message: {
            request: JanusVideoRoomMessageType.JOIN,
            room: this.sessionDetails!.videoRoomId,
            ptype: 'publisher',
            token: this.sessionDetails!.videoRoomAccessToken,
            streams: [],
          },
        });
      },
      error: (error) => {
        this.toastService.displayErrorMessage('videoRoom.joinError');
        this.detach();
        this.logError(error);
      },
      onmessage: (msg, jsep) => this.handlePublisherMessage(msg, jsep),
      onlocaltrack: (track, on, mid) => this.handleLocalTrack(track, on, mid),
      oncleanup: () => {},
    });
  }

  private handlePublisherMessage(
    msg: JanusJS.Message,
    jsep: JanusJS.JSEP | undefined
  ): void {
    const event: JanusVideoRoomEvent | undefined = msg['videoroom'];
    if (event !== undefined) {
      switch (event) {
        case JanusVideoRoomEvent.JOINED:
          this.userIdToPublisher.clear();
          this.privateId = msg['private_id'];
          this.currentUserId = msg['id'];
          const localPublisher: JanusVideoRoomPublisher = {
            id: this.currentUserId!,
            streams: [],
          };
          const publishers: JanusVideoRoomPublisher[] = msg['publishers'] || [];
          publishers.push(localPublisher);
          const streams: PublisherJanusVideoRoomStreamWithUserId[] =
            publishers.flatMap(
              (publisher) =>
                publisher.streams?.map((stream) => ({
                  ...stream,
                  user_id: publisher.id,
                })) ?? []
            );
          this.updateFeed();
          this.addPublishers(publishers);
          this.attachStreams(streams);
          this.status$.next(JanusStatus.CONNECTED);
          this.emitPublishersChanges();
          break;
        case JanusVideoRoomEvent.DESTROYED:
          this.toastService.displayErrorMessage('videoRoom.destroyedError');
          this.detach();
          break;
        case JanusVideoRoomEvent.EVENT:
          this.handlePublisherEvent(msg);
          break;
      }
    }
    if (jsep !== undefined) {
      this.videoRoomPublisherHandle?.handleRemoteJsep({ jsep: jsep });
      if (!msg['audio_codec']) {
        this.logWarn('Probably something wrong with the audio');
      }
      if (!msg['video_codec']) {
        this.logWarn('Probably something wrong with the video');
      }
    }
  }

  private handlePublisherEvent(msg: JanusJS.Message): void {
    if (msg['streams'] !== undefined) {
      // current user streams are hear and it's not usable info - sent after onlocaltrack
    } else if (msg['publishers'] !== undefined) {
      const publishers: JanusVideoRoomPublisher[] = msg['publishers'];
      const streams = publishers.flatMap(
        (publisher) =>
          publisher.streams?.map((stream) => ({
            ...stream,
            user_id: publisher.id,
          })) ?? []
      );
      this.addPublishers(publishers);
      this.attachStreams(streams);
      this.emitPublishersChanges();
    } else if (msg['leaving'] !== undefined) {
      this.deletePublisher(msg['leaving']);
      this.emitPublishersChanges();
    } else if (msg['unpublished'] !== undefined) {
      const unpublished = msg['unpublished'];
      if (unpublished === 'ok') {
        this.toastService.displayErrorMessage('videoRoom.detachError');
        this.detach();
      } else {
        this.deletePublisher(unpublished);
        this.emitPublishersChanges();
      }
    } else if (msg['error'] !== undefined) {
      if (msg['error_code'] === this.janusVideoRoomErrorNoSuchRoom) {
        this.toastService.displayErrorMessage('videoRoom.noSuchRoomError');
        this.detach();
      } else {
        this.logError(msg['error']);
      }
    }
  }

  private addPublishers(janusPublishers: JanusVideoRoomPublisher[]) {
    const newStreams: any[] = [];
    janusPublishers.forEach((janusPublisher) => {
      const id = janusPublisher['id'];
      const janusStreams = (janusPublisher['streams'] ?? []).filter(
        (stream) => stream.dummy !== true
      );
      this.deletePublisher(id);
      this.savePublisher(janusPublisher);
      newStreams.push(janusStreams);
    });
    if (newStreams.length) {
      //this.subscribeToStreams(newStreams);
    }
  }

  private savePublisher(janusPublisher: JanusVideoRoomPublisher): void {
    const userId = janusPublisher.id;
    this.deletePublisher(userId);
    const publisher: VideoRoomPublisher = {
      id: userId,
      streams: new Map(),
    };
    this.userIdToPublisher.set(userId, publisher);
  }

  private saveStreams(janusStreams: SubscriberJanusVideoRoomStream[]): void {
    const streams = janusStreams.map((janusStream) => ({
      userId: janusStream.feed_id,
      mid: janusStream.mid,
      userMid: janusStream.feed_mid,
      type: janusStream.type,
      media: null,
    }));
    streams.forEach((stream) => {
      const publisher = this.userIdToPublisher.get(stream.userId);
      if (publisher === undefined) {
        return;
      }
      const remote = this.isRemote(stream.userId);
      this.deleteStream(stream.mid, { remote });
      const midKey = this.getMidKey(stream.mid, {
        remote,
      });
      this.midToStream.set(midKey, stream);
      publisher.streams!.set(stream.userMid, stream);
    });
  }

  private getMidKey(mid: string, { remote }: { remote: boolean }): string {
    const prefix = remote ? 'remote' : 'local';
    return `${prefix}--${mid}`;
  }

  private isRemote(userId: string): boolean {
    return userId !== this.currentUserId;
  }

  private getTrackConfig(trackType: LocalTrackType): JanusJS.TrackOption {
    switch (trackType) {
      case LocalTrackType.MICROPHONE:
        return { type: 'audio', capture: true, recv: false };
      case LocalTrackType.VIDEO:
        return {
          type: 'video',
          capture: true,
          recv: false,
          simulcast: false,
        };
    }
  }

  private updateFeed(): void {
    if (this.videoRoomPublisherHandle === null) {
      return;
    }
    this.firstFeedPublished = true;
    let nextCreateMid = this.activeLocalTracks.size;
    const toCreate: [LocalTrackType, JanusJS.TrackOption][] = Array.from(
      this.enabledLocalTracks
    )
      .filter((trackType) => !this.activeLocalTracks.has(trackType))
      .map((trackType) => [
        trackType,
        {
          ...this.getTrackConfig(trackType),
          mid: `${nextCreateMid++}`,
        },
      ]);
    toCreate.forEach(([trackType, trackConfig]) =>
      // so janus library will not set add: true to saved trackConfig
      this.activeLocalTracks.set(trackType, { ...trackConfig })
    );
    const toUpdate: JanusJS.TrackOption[] = [];
    Array.from(this.activeLocalTracks.entries()).forEach(
      ([trackType, trackConfig]) => {
        if (
          !this.enabledLocalTracks.has(trackType) &&
          trackConfig.capture !== false
        ) {
          trackConfig.capture = false;
          toUpdate.push({ ...trackConfig, remove: true });
        }
        if (
          this.enabledLocalTracks.has(trackType) &&
          trackConfig.capture === false
        ) {
          trackConfig.capture = true;
          toUpdate.push({ ...trackConfig, replace: true });
        }
      }
    );
    if (toCreate.length > 0) {
      const streams = toCreate.map(([_, trackConfig]) => ({
        type: janusTrackTypeEnum.toJanusStreamType(
          trackConfig.type as JanusTrackType
        ),
        mid: trackConfig.mid!,
        feed_mid: trackConfig.mid!,
        feed_id: this.currentUserId!,
      }));
      const tracks = toCreate.map(([_, trackConfig]) => trackConfig);
      this.saveStreams(streams);
      this.videoRoomPublisherHandle.createOffer({
        tracks,
        success: (jsep) => {
          if (this.videoRoomPublisherHandle === null) {
            return;
          }
          const publish = { request: 'configure', audio: true, video: true };
          this.videoRoomPublisherHandle.send({ message: publish, jsep });
        },
        error: (error) => {
          this.logError(error.message);
          this.toastService.displayErrorMessage('videoRoom.publishFeedError');
          this.detach();
        },
      });
      this.emitPublishersChanges();
    }
    if (toUpdate.length > 0) {
      this.videoRoomPublisherHandle.replaceTracks({
        tracks: toUpdate,
        error: (err) => {
          this.logError(err);
          this.toastService.displayErrorMessage('videoRoom.publishFeedError');
          this.detach();
        },
      });
    }
  }

  private deletePublisher(userId: string): void {
    const publisher = this.userIdToPublisher.get(userId);
    if (publisher === undefined) {
      return;
    }
    publisher.streams.forEach((stream) => {
      this.deleteStream(stream.mid, { remote: this.isRemote(publisher.id) });
    });
    this.userIdToPublisher.delete(userId);
  }

  private deleteStream(mid: string, { remote }: { remote: boolean }): void {
    const midKey = this.getMidKey(mid, { remote });
    const stream = this.midToStream.get(midKey);
    if (stream === undefined) {
      return;
    }
    this.userIdToPublisher.get(stream.userId)?.streams.delete(mid);
    this.midToStream.delete(midKey);
  }

  private handleLocalTrack(
    track: MediaStreamTrack,
    on: boolean,
    mid: string
  ): void {
    // local audio will cause echo when included
    if (track.kind === 'audio') {
      return;
    }
    this.handleTrack(mid, track, on, { remote: false });
  }

  private handleTrack(
    mid: string,
    track: MediaStreamTrack,
    on: boolean,
    { remote }: { remote: boolean }
  ): void {
    const midKey = this.getMidKey(mid, { remote });
    const stream = this.midToStream.get(midKey);
    if (stream === undefined) {
      return;
    }
    if (on) {
      if (this.trackIdToDeletedTrack.has(track.id)) {
        this.trackIdToDeletedTrack.delete(track.id);
      } else {
        if (stream.media === null) {
          stream.media = {
            trackIds: [track.id],
            media: new MediaStream([track]),
          };
          this.emitPublishersChanges();
        } else if (stream.media.media.getTrackById(track.id) === null) {
          stream.media.media.addTrack(track);
          stream.media.trackIds.push(track.id);
          this.emitPublishersChanges();
        }
      }
    } else {
      if (stream.media === null || !stream.media.trackIds.includes(track.id)) {
        return;
      }
      const expireAt = new Date();
      expireAt.setMilliseconds(
        expireAt.getMilliseconds() + this.deletedTrackLifeTime
      );
      this.trackIdToDeletedTrack.set(track.id, {
        mid,
        remote,
        track,
        expireAt,
      });
    }
  }

  /**
   * debouncing for removing tracks so for small disconnects it will not create new media;
   */
  private clearExpiredDeletedTracks(): void {
    const deletedTracks = Array.from(
      this.trackIdToDeletedTrack.values()
    ).filter((deletedTrack) => deletedTrack.expireAt < new Date());
    deletedTracks.forEach((deletedTrack) => {
      const midKey = this.getMidKey(deletedTrack.mid, {
        remote: deletedTrack.remote,
      });
      const stream = this.midToStream.get(midKey);
      if (stream === undefined || stream?.media === null) {
        this.trackIdToDeletedTrack.delete(deletedTrack.track.id);
        return;
      }

      stream.media.media.removeTrack(deletedTrack.track);
      stream.media!.trackIds = stream.media!.trackIds.filter(
        (el) => el != deletedTrack.track.id
      );
      if (stream.media.media.getTracks().length === 0) {
        stream.media = null;
      }
      this.emitPublishersChanges();
      this.trackIdToDeletedTrack.delete(deletedTrack.track.id);
    });
  }

  private emitPublishersChanges(): void {
    const users: VideoRoomUser[] = Array.from(
      this.userIdToPublisher.values()
    ).map((publisher) => {
      const streams: VideoRoomStream[] = Array.from(
        publisher.streams.values()
      ).map((stream) => {
        return {
          ...stream,
          media:
            stream.media !== null
              ? {
                  ...stream.media,
                }
              : null,
        };
      });
      return {
        ...publisher,
        streams,
      };
    });
    this.users$.next(users);
  }

  private getVideoRoomSubscriberHandle(
    subscribe: SubscribeJanusVideoRoomStream[]
  ): { first: boolean; handlePromise: Promise<JanusJS.PluginHandle> } {
    if (this.videoRoomSubscriberHandlePromise !== null) {
      return {
        first: false,
        handlePromise: this.videoRoomSubscriberHandlePromise,
      };
    }
    this.videoRoomSubscriberHandlePromise = new Promise((resolve, reject) => {
      if (this.janus === null) {
        reject();
      }
      let handle: JanusJS.PluginHandle | null = null;
      this.janus!.attach({
        plugin: this.videoRoomPluginName,
        success: (pluginHandle) => {
          handle = pluginHandle;
          handle.send({
            message: {
              request: JanusVideoRoomMessageType.JOIN,
              room: this.sessionDetails!.videoRoomId,
              ptype: 'subscriber',
              token: this.sessionDetails!.videoRoomAccessToken,
              private_id: this.privateId,
              streams: subscribe,
            },
          });
        },
        error: (error) => {
          this.toastService.displayErrorMessage('videoRoom.joinError');
          this.detach();
          this.logError(error);
          reject();
        },
        onmessage: (msg, jsep) => {
          if (msg['videoroom'] === JanusVideoRoomEvent.ATTACHED) {
            resolve(handle!);
          }
          this.handleSubscriberMessage(handle!, msg, jsep);
        },
        onremotetrack: (track, mid, on) =>
          this.handleTrack(mid, track, on, { remote: true }),
        oncleanup: () => {},
      });
    });
    return {
      first: true,
      handlePromise: this.videoRoomSubscriberHandlePromise,
    };
  }

  private handleSubscriberMessage(
    handle: JanusJS.PluginHandle,
    msg: JanusJS.Message,
    jsep: JanusJS.JSEP | undefined
  ): void {
    if (msg['videoroom'] === JanusVideoRoomEvent.EVENT) {
      // simulcast/temporal settings can be included hear (like quality switch buttons)
    }
    if (msg['streams'] !== undefined) {
      const streams: SubscriberJanusVideoRoomStream[] = msg['streams'];
      this.saveStreams(streams);
      this.emitPublishersChanges();
    }
    if (jsep !== undefined) {
      handle.createAnswer({
        jsep,
        tracks: [{ type: 'data', capture: true }],
        success: (jsep) => {
          const body = {
            request: 'start',
            room: this.sessionDetails?.videoRoomId,
          };
          handle.send({ message: body, jsep: jsep });
        },
        error: (error) => {
          this.logError(error);
        },
      });
    }
  }

  private async attachStreams(
    streams: PublisherJanusVideoRoomStreamWithUserId[]
  ): Promise<void> {
    const subscribe: SubscribeJanusVideoRoomStream[] = [];
    const unsubscribe: SubscribeJanusVideoRoomStream[] = [];
    streams.forEach((stream) => {
      const mid = this.userIdToPublisher
        .get(stream.user_id)
        ?.streams?.get(stream.mid)?.mid;
      if (!this.isStreamSupported(stream)) {
        return;
      }
      if (stream.disabled === true) {
        if (mid !== undefined) {
          this.deleteStream(mid, { remote: true });
        }
        unsubscribe.push({
          feed: stream.user_id,
          mid: stream.mid,
        });
      } else if (mid === undefined) {
        subscribe.push({
          feed: stream.user_id,
          mid: stream.mid,
        });
      }
    });
    if (subscribe.length === 0 && unsubscribe.length === 0) {
      return;
    }
    const { first, handlePromise } =
      this.getVideoRoomSubscriberHandle(subscribe);
    const handle = await handlePromise;
    if (!first) {
      const update: {
        request: string;
        subscribe?: SubscribeJanusVideoRoomStream[];
        unsubscribe?: SubscribeJanusVideoRoomStream[];
      } = { request: 'update' };
      if (subscribe.length > 0) {
        update.subscribe = subscribe;
      }
      if (unsubscribe.length > 0) {
        update.unsubscribe = unsubscribe;
      }
      handle.send({ message: update });
    }
  }

  private isStreamSupported(stream: PublisherJanusVideoRoomStream): boolean {
    if (stream.type !== JanusVideoRoomStreamType.VIDEO) {
      return true;
    }
    const videoStream = stream as PublisherVideoJanusVideoRoomStream;
    return (
      Janus.webRTCAdapter.browserDetails.browser !== 'safari' ||
      ((videoStream.codec !== 'vp9' || Janus.safariVp9) &&
        (videoStream.codec !== 'vp8' || Janus.safariVp8))
    );
  }

  private logError(error: string): void {
    if (this.debug) {
      console.error(`Janus video room error: ${error}`);
    }
  }

  private logWarn(text: string): void {
    if (this.debug) {
      console.warn(`Janus video room warn: ${text}`);
    }
  }
}
