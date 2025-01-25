package pl.bronikowski.springchat.backendmain.member.internal;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByAuthResourceId(String authResourceId);

    boolean existsByNickname(String nickname);

    @EntityGraph("Member.withProfilePicture")
    Optional<Member> findWithProfilePictureById(UUID id);
}
