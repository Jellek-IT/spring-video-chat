package pl.bronikowski.springchat.backendmain.storage.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StorageFileRepository extends JpaRepository<StorageFile, UUID> {
}
