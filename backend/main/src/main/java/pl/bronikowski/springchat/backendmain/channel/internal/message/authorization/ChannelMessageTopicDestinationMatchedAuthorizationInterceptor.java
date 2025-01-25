package pl.bronikowski.springchat.backendmain.channel.internal.message.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.authserver.api.UserContextProvider;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;
import pl.bronikowski.springchat.backendmain.channel.internal.member.ChannelMemberRepository;
import pl.bronikowski.springchat.backendmain.websocket.api.DestinationMatchedAuthorizationInterceptor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChannelMessageTopicDestinationMatchedAuthorizationInterceptor extends DestinationMatchedAuthorizationInterceptor {
    private final ChannelMemberRepository channelMemberRepository;

    @Override
    protected String getTemplate() {
        return "/topic/channels.{id}.message";
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
                    Set.of(ChannelMemberRight.READ));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
