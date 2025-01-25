package pl.bronikowski.springchat.backendmain.channel.internal.specification.memberqueryparams;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pl.bronikowski.springchat.backendmain.channel.api.dto.MemberChannelQueryParams;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel;

@RequiredArgsConstructor
public class MemberChannelQueryParamsSpecification implements Specification<Channel> {
    private final MemberChannelQueryParams queryParams;
    private final String memberAuthResourceId;

    @Override
    @NonNull
    public Predicate toPredicate(@NonNull Root<Channel> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicateProvider = new MemberChannelQueryParamsPredicateProvider(queryParams, memberAuthResourceId);
        return cb.and(predicateProvider.getAllFilters(root, cb));
    }
}
