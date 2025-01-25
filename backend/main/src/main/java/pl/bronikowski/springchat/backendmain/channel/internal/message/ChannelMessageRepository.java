package pl.bronikowski.springchat.backendmain.channel.internal.message;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bronikowski.springchat.backendmain.config.repository.JpaSpecificationWithEntityGraph;

import java.util.UUID;

public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, UUID>,
        JpaSpecificationWithEntityGraph<ChannelMessage> {
}
