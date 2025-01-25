package pl.bronikowski.springchat.backendmain.channel.internal.message.specification.memberqueryparams;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.MemberChannelMessageQueryParams;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel_;
import pl.bronikowski.springchat.backendmain.channel.internal.message.ChannelMessage;
import pl.bronikowski.springchat.backendmain.channel.internal.message.ChannelMessage_;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class MemberChannelMessageQueryParamsPredicateProvider {
    private final MemberChannelMessageQueryParams queryParams;
    private final UUID channelId;

    public Predicate[] getAllFilters(Root<ChannelMessage> root, CriteriaBuilder cb) {
        return Stream.of(channelIdEquals(channelId, root, cb),
                        getBeforeSequenceFilter(root, cb))
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);
    }

    private Predicate channelIdEquals(UUID channelId, Root<ChannelMessage> root, CriteriaBuilder cb) {
        return cb.equal(root.get(ChannelMessage_.channel).get(Channel_.id), channelId);
    }

    private Predicate getBeforeSequenceFilter(Root<ChannelMessage> root, CriteriaBuilder cb) {
        var beforeSequence = queryParams.beforeSequence();
        return beforeSequence != null ?
                cb.lessThan(root.get(ChannelMessage_.sequence), beforeSequence)
                : null;
    }
}
