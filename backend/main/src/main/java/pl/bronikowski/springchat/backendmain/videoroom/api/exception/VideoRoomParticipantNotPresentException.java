package pl.bronikowski.springchat.backendmain.videoroom.api.exception;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;
import pl.bronikowski.springchat.backendmain.exception.ErrorResponseType;

public class VideoRoomParticipantNotPresentException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public VideoRoomParticipantNotPresentException() {
        super("User is not present in this video room", HTTP_STATUS, ErrorResponseType.VIDEO_ROOM_PARTICIPANT_NOT_PRESENT);
    }
}
