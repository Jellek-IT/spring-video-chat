package pl.bronikowski.springchat.backendmain.channel.internal.message;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.CreateChannelMessagePayload;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel;
import pl.bronikowski.springchat.backendmain.channel.internal.file.ChannelFile;
import pl.bronikowski.springchat.backendmain.member.internal.Member;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@NamedEntityGraph(name = "ChannelMessage.withMemberWithFiles",
        attributeNodes = {
                @NamedAttributeNode("member"),
                @NamedAttributeNode("files")})
public class ChannelMessage {
    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    private String text;

    @Column(insertable = false, updatable = false)
    private Long sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Member member;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_message_id", nullable = false)
    private Set<ChannelFile> files = new HashSet<>();

    public ChannelMessage(CreateChannelMessagePayload payload, Channel channel, Member member, List<ChannelFile> files) {
        this.text = payload.text().strip();
        this.channel = channel;
        this.member = member;
        this.files.addAll(files);
    }
}
