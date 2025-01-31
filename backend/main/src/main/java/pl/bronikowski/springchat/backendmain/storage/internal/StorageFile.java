package pl.bronikowski.springchat.backendmain.storage.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorageFile {
    @Id
    UUID id;

    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    private String folder;

    @Column(nullable = false)
    private String name;

    public StorageFile(UUID id, String folder, String name) {
        this.id = id;
        this.folder = folder;
        this.name = name;
    }
}
