package pl.bronikowski.springchat.backendmain.videoroom.internal;

import pl.bronikowski.springchat.backendmain.channel.internal.Channel;
import pl.bronikowski.springchat.backendmain.user.internal.User;
import pl.bronikowski.springchat.backendmain.videoroom.api.dto.VideoRoomSessionDetailsDto;
import pl.bronikowski.springchat.backendmain.websocket.api.UserConnectionDetails;
import pl.bronikowski.springchat.backendmain.websocket.api.UserSubscriptionDetails;

public interface VideoRoomClient {
    VideoRoomSessionDetailsDto join(Channel channel, User user, UserSubscriptionDetails userSubscriptionDetails);

    /**
     * User unsubscribed some topic, but it's not sure that topic was correlated with video room
     */
    void leaveWithSubscriptionDetails(UserSubscriptionDetails userSubscriptionDetails);

    /**
     * User lost connection so there is no need to check topic subscription id
     */
    void leave(UserConnectionDetails userConnectionDetails);

    VideoRoomSessionDetailsDto extendUserSession(UserConnectionDetails userConnectionDetails);

    void clearExpired();
}
