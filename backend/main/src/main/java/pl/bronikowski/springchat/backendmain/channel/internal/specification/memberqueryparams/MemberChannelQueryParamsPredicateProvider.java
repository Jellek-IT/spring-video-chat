package pl.bronikowski.springchat.backendmain.channel.internal.specification.memberqueryparams;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import pl.bronikowski.springchat.backendmain.channel.api.dto.MemberChannelQueryParams;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel;
import pl.bronikowski.springchat.backendmain.channel.internal.member.ChannelMember;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel_;
import pl.bronikowski.springchat.backendmain.channel.internal.member.ChannelMember_;
import pl.bronikowski.springchat.backendmain.member.internal.Member_;
import pl.bronikowski.springchat.backendmain.shared.utils.JpaUtils;

import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class MemberChannelQueryParamsPredicateProvider {
    private final MemberChannelQueryParams queryParams;
    private final String memberAuthResourceId;

    public Predicate[] getAllFilters(Root<Channel> root, CriteriaBuilder cb) {
        return Stream.of(isMemberAssignedToChannel(root, cb),
                        isNotDeleted(root, cb),
                        getNameFilter(root, cb))
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);
    }

    private Predicate isNotDeleted(Root<Channel> root, CriteriaBuilder cb) {
        return cb.isNull(root.get(Channel_.deletedAt));
    }

    /**
     * access rights related to ChannelMemberRepository
     * @see pl.bronikowski.springchat.backendmain.channel.internal.member.ChannelMemberRepository
     */
    private Predicate isMemberAssignedToChannel(Root<Channel> root, CriteriaBuilder cb) {
        var subquery = cb.createQuery().subquery(Integer.class);
        var subqueryRoot = subquery.from(ChannelMember.class);
        var memberJoin = subqueryRoot.join(ChannelMember_.member);

        var predicate = cb.and(
                cb.equal(subqueryRoot.get(ChannelMember_.channel), root),
                cb.isNull(subqueryRoot.get(ChannelMember_.deletedAt)),
                cb.equal(memberJoin.get(Member_.authResourceId), memberAuthResourceId));
        subquery.select(cb.literal(1)).where(predicate);

        return cb.exists(subquery);
    }

    private Predicate getNameFilter(Root<Channel> root, CriteriaBuilder cb) {
        return queryParams.name() != null
                ? JpaUtils.likeIgnoreCase(root.get(Channel_.name), queryParams.name(), cb)
                : null;
    }
}
