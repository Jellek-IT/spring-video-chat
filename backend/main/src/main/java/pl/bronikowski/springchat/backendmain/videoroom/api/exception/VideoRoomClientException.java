package pl.bronikowski.springchat.backendmain.videoroom.api.exception;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;

public class VideoRoomClientException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public VideoRoomClientException(String message) {
        super(message, HTTP_STATUS);
    }

    public VideoRoomClientException(String message, Throwable cause) {
        super(message, cause, HTTP_STATUS);
    }
}
