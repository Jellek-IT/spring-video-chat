package pl.bronikowski.springchat.backendmain.channel.internal.file;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelFileType;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel;
import pl.bronikowski.springchat.backendmain.member.internal.Member;
import pl.bronikowski.springchat.backendmain.shared.AbstractIdEqualsEntity;
import pl.bronikowski.springchat.backendmain.storage.internal.StorageFile;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@NamedEntityGraph(name = "ChannelFile.withFile",
        attributeNodes = {
                @NamedAttributeNode("file")})
public class ChannelFile extends AbstractIdEqualsEntity<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    @JoinColumn(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelFileType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Member author;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Channel channel;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private StorageFile file;

    public ChannelFile(Member author, Channel channel, StorageFile file, Clock clock) {
        this.createdAt = clock.instant();
        this.author = author;
        this.channel = channel;
        this.file = file;
        this.type = ChannelFileType.IMAGE;
    }
}
