package pl.bronikowski.springchat.backendmain.channel.api.exception;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;
import pl.bronikowski.springchat.backendmain.exception.ExceptionResponseType;

public class ChannelOperationNotEnoughRightsException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;

    public ChannelOperationNotEnoughRightsException() {
        super("User has not enough rights for this operation", HTTP_STATUS, ExceptionResponseType.CHANNEL_OPERATION_NOT_ENOUGH_RIGHTS);
    }
}
