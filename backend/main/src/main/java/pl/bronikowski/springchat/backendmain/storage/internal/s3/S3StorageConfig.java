package pl.bronikowski.springchat.backendmain.storage.internal.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.bronikowski.springchat.backendmain.config.Profiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Configuration
@Profile("!" + Profiles.TEST)
public class S3StorageConfig {
    private final S3StorageProperties s3StorageProperties;

    @Bean
    public S3Client S3Client() {
        return S3Client.builder()
                .region(getRegion())
                .credentialsProvider(getCredentialsProvider())
                .endpointProvider(this::provideEndpoint)
                .build();
    }

    private AwsCredentialsProvider getCredentialsProvider() {
        var credentials = AwsBasicCredentials.create(
                s3StorageProperties.accessKey(),
                s3StorageProperties.secretKey());
        return StaticCredentialsProvider.create(credentials);
    }

    private CompletableFuture<Endpoint> provideEndpoint(S3EndpointParams s3EndpointParams) {
        var endpoint = Endpoint.builder()
                .url(URI.create(s3StorageProperties.endpoint() + "/" + s3EndpointParams.bucket()))
                .build();
        return CompletableFuture.completedFuture(endpoint);
    }

    private Region getRegion() {
        return Region.regions().stream()
                .filter(region -> SdkHttpUtils.urlEncode(s3StorageProperties.region()).equals(region.id()))
                .findAny()
                .orElseGet(() -> Region.of(s3StorageProperties.region()));
    }
}
