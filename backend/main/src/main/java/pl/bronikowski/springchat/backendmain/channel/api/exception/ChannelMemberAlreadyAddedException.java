package pl.bronikowski.springchat.backendmain.channel.api.exception;

import org.springframework.http.HttpStatus;
import pl.bronikowski.springchat.backendmain.exception.AppApplicationException;
import pl.bronikowski.springchat.backendmain.exception.ErrorResponseType;

public class ChannelMemberAlreadyAddedException extends AppApplicationException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public ChannelMemberAlreadyAddedException() {
        super("This member was already added to that channel", HTTP_STATUS, ErrorResponseType.CHANNEL_MEMBER_ALREADY_ADDED);
    }
}
