package pl.bronikowski.springchat.backendmain.channel.internal.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, UUID> {

    /**
     * Access rights related to MemberChannelQueryParamsPredicateProvider
     * @see pl.bronikowski.springchat.backendmain.channel.internal.specification.memberqueryparams.MemberChannelQueryParamsPredicateProvider
     * https://github.com/spring-projects/spring-data-jpa/issues/2551
     * Question marks does not work in jpa queries.
     * in other hand for functions like "jsonb_exists" indexing does not work.
     * For existing to work custom operator can be created: https://stackoverflow.com/a/50488457/22808245 */
    @Query("select count(*)>0 FROM #{#entityName} cm " +
            "join cm.member m " +
            "join cm.channel c " +
            "WHERE cm.channel.id = :channelId " +
            "and cm.deletedAt is null " +
            "and c.deletedAt is null " +
            "and m.authResourceId = :memberAuthResourceId " +
            "and jsonb_exists_all(cm.rights, :rightNames)")
    boolean memberHasAccessWithRightName(UUID channelId, String memberAuthResourceId,
                                         Set<String> rightNames);
    @Query("select count(*)>0 FROM #{#entityName} cm " +
            "join cm.member m " +
            "join cm.channel c " +
            "WHERE cm.channel.id = :channelId " +
            "and cm.deletedAt is null " +
            "and c.deletedAt is null " +
            "and m.authResourceId = :memberAuthResourceId")
    boolean memberHasAccess(UUID channelId, String memberAuthResourceId);

    default boolean userHasAccessWithRights(UUID channelId, String memberAuthResourceId,
                                            Set<ChannelMemberRight> rights) {
        var rightNames = rights.stream().map(Enum::name).collect(Collectors.toSet());
        return memberHasAccessWithRightName(channelId, memberAuthResourceId, rightNames);
    }
}
