package pl.bronikowski.springchat.backendmain.channel.api;

import java.util.Set;

public enum ChannelMemberRight {
    MANAGE,
    INVITE,
    KICK,
    TALK,
    WRITE,
    READ;

    public static final Set<ChannelMemberRight> OWNER_RIGHTS = Set.of(MANAGE, INVITE, KICK, TALK, WRITE, READ);
    public static final Set<ChannelMemberRight> DEFAULT_RIGHTS = Set.of(TALK, WRITE, READ);
}
