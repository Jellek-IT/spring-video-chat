package pl.bronikowski.springchat.backendmain.channel.internal;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.bronikowski.springchat.backendmain.config.repository.JpaSpecificationWithEntityGraph;

import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID>, JpaSpecificationWithEntityGraph<Channel> {

    @EntityGraph(Channel_.GRAPH_CHANNEL_WITH_MEMBERS)
    Optional<Channel> findWithMembersById(UUID id);

    @EntityGraph(Channel_.GRAPH_CHANNEL_WITH_THUMBNAIL)
    Optional<Channel> findWithThumbnailById(UUID id);
}
