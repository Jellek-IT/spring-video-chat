package pl.bronikowski.springchat.backendmain.storage.internal.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.client.s3")
public record S3StorageProperties(
        String accessKey,
        String secretKey,
        String endpoint,
        String bucket,
        String region
) {
}
