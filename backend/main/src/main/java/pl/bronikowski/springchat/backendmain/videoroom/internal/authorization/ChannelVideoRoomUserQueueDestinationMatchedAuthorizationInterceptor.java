package pl.bronikowski.springchat.backendmain.videoroom.internal.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.authserver.api.UserContextProvider;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;
import pl.bronikowski.springchat.backendmain.channel.internal.member.ChannelMemberRepository;
import pl.bronikowski.springchat.backendmain.videoroom.internal.VideoRoomDestinations;
import pl.bronikowski.springchat.backendmain.websocket.api.DestinationMatchedAuthorizationInterceptor;
import pl.bronikowski.springchat.backendmain.websocket.api.StompDestinations;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChannelVideoRoomUserQueueDestinationMatchedAuthorizationInterceptor extends DestinationMatchedAuthorizationInterceptor {
    private final ChannelMemberRepository channelMemberRepository;

    @Override
    protected String getTemplate() {
        return StompDestinations.USER_PREFIX + VideoRoomDestinations.getTokenUserQueueDestination("{id}");
    }

    @Override
    protected StompCommand getCommand() {
        return StompCommand.SUBSCRIBE;
    }

    @Override
    protected boolean hasAccess(Map<String, String> parameters) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        try {
            var id = UUID.fromString(parameters.get("id"));
            return channelMemberRepository.userHasAccessWithRights(id, authResourceId,
                    Set.of(ChannelMemberRight.TALK));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
