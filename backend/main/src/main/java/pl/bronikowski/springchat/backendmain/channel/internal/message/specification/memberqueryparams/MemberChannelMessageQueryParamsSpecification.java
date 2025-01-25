package pl.bronikowski.springchat.backendmain.channel.internal.message.specification.memberqueryparams;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.MemberChannelMessageQueryParams;
import pl.bronikowski.springchat.backendmain.channel.internal.message.ChannelMessage;

import java.util.UUID;

@RequiredArgsConstructor
public class MemberChannelMessageQueryParamsSpecification implements Specification<ChannelMessage> {
    private final MemberChannelMessageQueryParams queryParams;
    private final UUID channelId;

    @Override
    @NonNull
    public Predicate toPredicate(@NonNull Root<ChannelMessage> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicateProvider = new MemberChannelMessageQueryParamsPredicateProvider(queryParams, channelId);
        return cb.and(predicateProvider.getAllFilters(root, cb));
    }
}
