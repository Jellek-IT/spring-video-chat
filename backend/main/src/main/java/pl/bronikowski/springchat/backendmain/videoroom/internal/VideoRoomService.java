package pl.bronikowski.springchat.backendmain.videoroom.internal;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bronikowski.springchat.backendmain.channel.internal.ChannelRepository;
import pl.bronikowski.springchat.backendmain.user.internal.UserRepository;
import pl.bronikowski.springchat.backendmain.videoroom.api.dto.VideoRoomSessionDetailsDto;
import pl.bronikowski.springchat.backendmain.websocket.api.UserConnectionDetails;
import pl.bronikowski.springchat.backendmain.websocket.api.UserSubscriptionDetails;

import java.util.UUID;

/**
 * Transactions do not work with redis repositories so if there is no requirement for transaction from SQL db, it should
 * not be included
 */
@Service
@RequiredArgsConstructor
public class VideoRoomService {
    private final VideoRoomClient videoRoomClient;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Transactional
    public VideoRoomSessionDetailsDto join(UUID channelId, UserSubscriptionDetails userSubscriptionDetails) {
        var user = userRepository.findByAuthResourceId(userSubscriptionDetails.authResourceId())
                .orElseThrow(EntityExistsException::new);
        // we now that channel exists by authorization to app destination, it's only wrapper for id
        var channel = channelRepository.getReferenceById(channelId);
        return videoRoomClient.join(channel, user, userSubscriptionDetails);
    }

    public void leave(UserSubscriptionDetails userSubscriptionDetails) {
        videoRoomClient.leaveWithSubscriptionDetails(userSubscriptionDetails);
    }

    public void leave(UserConnectionDetails userConnectionDetails) {
        videoRoomClient.leave(userConnectionDetails);
    }

    public VideoRoomSessionDetailsDto extendUserSession(UserConnectionDetails userConnectionDetails) {
        return videoRoomClient.extendUserSession(userConnectionDetails);
    }

    public void clearExpired() {
        videoRoomClient.clearExpired();
    }
}
