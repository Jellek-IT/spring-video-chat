package pl.bronikowski.springchat.backendmain.user.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.bronikowski.springchat.backendmain.storage.internal.StorageFile;
import pl.bronikowski.springchat.backendmain.user.api.UserType;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedEntityGraph(name = "User.withProfilePicture",
        attributeNodes = {
                @NamedAttributeNode("profilePicture")}
)
public abstract class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    private String authResourceId;

    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String registerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @Column(nullable = false)
    @Setter
    private Boolean emailVerified;

    // lazy works for OneToOne when id is known for User
    @OneToOne(fetch = FetchType.LAZY)
    @Setter
    private StorageFile profilePicture;

    protected User(UserType type, String email, boolean emailVerified) {
        this.type = type;
        this.email = email;
        this.emailVerified = emailVerified;
        this.registerEmail = email;
    }
}
