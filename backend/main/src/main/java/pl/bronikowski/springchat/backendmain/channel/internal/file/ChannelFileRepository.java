package pl.bronikowski.springchat.backendmain.channel.internal.file;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChannelFileRepository extends JpaRepository<ChannelFile, UUID> {
    @EntityGraph(ChannelFile_.GRAPH_CHANNEL_FILE_WITH_FILE)
    Optional<ChannelFile> findWithFileByIdAndChannelId(UUID id, UUID channelId);

    Optional<ChannelFile> findByIdAndChannelId(UUID id, UUID channelId);
}
