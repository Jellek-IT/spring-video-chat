package pl.bronikowski.springchat.backendmain.videoroom.api.exception;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;
import pl.bronikowski.springchat.backendmain.exception.ErrorResponseType;

public class VideoRoomAlreadyJoinedException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    public VideoRoomAlreadyJoinedException() {
        super("User has already joined Video Room from another session", HTTP_STATUS, ErrorResponseType.VIDEO_ROOM_PARTICIPANT_NOT_PRESENT);
    }
}
