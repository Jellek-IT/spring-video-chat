package pl.bronikowski.springchat.backendmain.storage.internal.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.config.Profiles;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!" + Profiles.TEST)
public class S3StorageBucketStartupCreator implements ApplicationRunner {
    private final S3Client s3Client;
    private final S3StorageProperties s3StorageProperties;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Creating s3 default bucket [name={}]", s3StorageProperties.bucket());
        if (defaultBucketExists()) {
            log.info("S3 default bucket already exists");
            return;
        }
        createDefaultBucket();
        log.info("S3 default bucket created");
    }

    private boolean defaultBucketExists() {
        var request = HeadBucketRequest.builder()
                .bucket(s3StorageProperties.bucket())
                .build();
        try {
            s3Client.headBucket(request);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    private void createDefaultBucket() {
        var request = CreateBucketRequest.builder()
                .bucket(s3StorageProperties.bucket())
                .build();
        s3Client.createBucket(request);
    }
}
