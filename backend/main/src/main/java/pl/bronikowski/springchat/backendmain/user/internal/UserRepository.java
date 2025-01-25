package pl.bronikowski.springchat.backendmain.user.internal;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByAuthResourceId(String authResourceId);

    @EntityGraph(User_.GRAPH_USER_WITH_PROFILE_PICTURE)
    Optional<User> findWithProfilePictureByAuthResourceId(String authResourceId);
}
