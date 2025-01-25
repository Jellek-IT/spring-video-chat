package pl.bronikowski.springchat.backendmain.channel.internal.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;
import pl.bronikowski.springchat.backendmain.channel.api.dto.UpdateChannelMemberRequest;
import pl.bronikowski.springchat.backendmain.channel.api.exception.ChannelMemberAlreadyAddedException;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel;
import pl.bronikowski.springchat.backendmain.member.internal.Member;
import pl.bronikowski.springchat.backendmain.shared.AbstractIdEqualsEntity;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChannelMember extends AbstractIdEqualsEntity<ChannelMember.ChannelMemberId> {
    @EmbeddedId
    private ChannelMemberId id;

    @CreatedDate
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(nullable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("channelId")
    @JoinColumn(nullable = false, updatable = false)
    private Channel channel;

    private Instant deletedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    @Getter
    private final Set<ChannelMemberRight> rights = new HashSet<>();

    public ChannelMember(Channel channel, Member member, Set<ChannelMemberRight> rights) {
        this.channel = channel;
        this.member = member;
        this.id = new ChannelMemberId(this.channel.getId(), this.member.getId());
        this.rights.addAll(rights);
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void delete(Clock clock) {
        this.deletedAt = clock.instant();
    }

    public void restore(Set<ChannelMemberRight> rights) {
        if (!isDeleted()) {
            throw new ChannelMemberAlreadyAddedException();
        }
        this.updateRights(rights);
    }

    public void update(UpdateChannelMemberRequest request) {
        this.updateRights(request.rights());
    }

    protected void updateRights(Set<ChannelMemberRight> rights) {
        this.rights.clear();
        this.rights.addAll(rights);
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ChannelMemberId implements Serializable {
        private UUID channelId;
        private UUID memberId;
    }
}
