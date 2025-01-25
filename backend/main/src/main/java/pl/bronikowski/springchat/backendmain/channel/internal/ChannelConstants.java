package pl.bronikowski.springchat.backendmain.channel.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConstants {
    public static final String NAME_REGEX = "^[ a-zA-Z0-9_-]*$";
}
