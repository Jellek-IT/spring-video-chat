package pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.dto.messagerequestbody.videoroom;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.AudioCodec;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.VideoCodec;
import pl.bronikowski.springchat.backendmain.videoroom.internal.janus.api.videoroom.VideoRoomJanusPayloadType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CreateVideoRoomMessageJanusRequestBodyPayload extends VideoRoomMessageJanusRequestBodyPayload {
    private UUID room;
    private String description;
    private String adminKey;
    private Boolean isPrivate;
    private String secret;
    private String pin;
    private Boolean requirePvtid;
    private Boolean signedTokens;
    private Integer publishers;
    private Integer bitrate;
    private Boolean bitrateCap;
    private Integer firFreq;
    private AudioCodec audiocodec;
    private VideoCodec videocodec;
    private Integer vp9Profile;
    private String h264Profile;
    private Boolean opusFec;
    private Boolean opusDtx;
    private Boolean audiolevelExt;
    private Boolean audiolevelEvent;
    private Integer audioActivePackets;
    private Integer audioLevelAverage;
    private Boolean videoorientExt;
    private Boolean playoutdelayExt;
    private Boolean transportWideCcExt;
    private Boolean record;
    private String recDir;
    private Boolean lockRecord;
    private Boolean notifyJoining;
    private Boolean requireE2ee;
    private Boolean dummyPublisher;
    private List<String> dummyStreams;
    private Integer threads;
    private List<String> allowed;

    public CreateVideoRoomMessageJanusRequestBodyPayload() {
        super(VideoRoomJanusPayloadType.CREATE);
    }
}
