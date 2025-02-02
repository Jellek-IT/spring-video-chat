package pl.bronikowski.springchat.backendmain.channel.internal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;
import pl.bronikowski.springchat.backendmain.channel.api.dto.CreateChannelRequest;
import pl.bronikowski.springchat.backendmain.channel.api.dto.UpdateChannelRequest;
import pl.bronikowski.springchat.backendmain.channel.internal.member.ChannelMember;
import pl.bronikowski.springchat.backendmain.member.internal.Member;
import pl.bronikowski.springchat.backendmain.storage.internal.StorageFile;

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@NamedEntityGraph(name = "Channel.withMembers",
        attributeNodes = {
                @NamedAttributeNode(value = "members", subgraph = "members")},
        subgraphs = {
                @NamedSubgraph(name = "members", type = ChannelMember.class,
                        attributeNodes = @NamedAttributeNode("member"))})
@NamedEntityGraph(name = "Channel.withThumbnail",
        attributeNodes = {
                @NamedAttributeNode("thumbnail")})
public class Channel {
    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    private String name;

    private Instant deletedAt;

    // lazy works for OneToOne when id is known for User
    @OneToOne(fetch = FetchType.LAZY)
    @Setter
    private StorageFile thumbnail;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final Set<ChannelMember> members = new HashSet<>();

    public Channel(CreateChannelRequest request, Member owner) {
        this.name = request.name().strip();
        this.addMember(owner, ChannelMemberRight.OWNER_RIGHTS);
    }

    public void update(UpdateChannelRequest request) {
        this.name = request.name().strip();
    }

    public void addMember(Member member, Set<ChannelMemberRight> rights) {
        var channelMember = new ChannelMember(this, member, rights);
        this.members.add(channelMember);
    }

    public Optional<ChannelMember> findMemberByUserAuthResourceId(String authResourceId) {
        return members.stream()
                .filter(channelMember -> authResourceId.equals(channelMember.getMember().getAuthResourceId()))
                .findAny();
    }

    public Optional<ChannelMember> findMemberById(UUID id) {
        return members.stream()
                .filter(channelMember -> id.equals(channelMember.getMember().getId()))
                .findAny();
    }

    public Optional<ChannelMember> findNotDeletedMemberById(UUID id) {
        return members.stream()
                .filter(channelMember -> id.equals(channelMember.getMember().getId()))
                .filter(channelMember -> !channelMember.isDeleted())
                .findAny();
    }

    public void delete(Clock clock) {
        this.deletedAt = clock.instant();
    }

    public Boolean getHasThumbnail() {
        return this.thumbnail != null;
    }

}
