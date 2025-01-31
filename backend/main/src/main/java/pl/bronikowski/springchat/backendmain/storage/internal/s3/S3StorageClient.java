package pl.bronikowski.springchat.backendmain.storage.internal.s3;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.bronikowski.springchat.backendmain.channel.internal.Channel;
import pl.bronikowski.springchat.backendmain.shared.utils.FileUtils;
import pl.bronikowski.springchat.backendmain.storage.api.StorageClient;
import pl.bronikowski.springchat.backendmain.storage.api.StorageException;
import pl.bronikowski.springchat.backendmain.storage.internal.StorageFile;
import pl.bronikowski.springchat.backendmain.storage.internal.StorageFileRepository;
import pl.bronikowski.springchat.backendmain.user.internal.User;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.InvalidObjectStateException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3StorageClient implements StorageClient {
    private static final String USER_FOLDER = "user/%s";
    private static final String CHANNEL_FOLDER = "channel/%s";

    private final S3StorageProperties s3StorageProperties;
    private final S3Client s3Client;
    private final StorageFileRepository storageFileRepository;

    @Override
    public StorageFile uploadUserImage(MultipartFile multipartFile, User user, String name) {
        var folder = String.format(USER_FOLDER, user.getId());
        return uploadFile(multipartFile, folder, name);
    }

    @Override
    public StorageFile uploadChannelImage(MultipartFile multipartFile, Channel user, String name) {
        var folder = String.format(USER_FOLDER, user.getId());
        return uploadFile(multipartFile, folder, name);
    }

    @Override
    public void download(StorageFile storageFile, String range, HttpServletResponse httpResponse) {
        var request = GetObjectRequest.builder()
                .bucket(s3StorageProperties.bucket())
                .key(getObjectKey(storageFile))
                .range(range)
                .build();
        try (var response = s3Client.getObject(request)) {
            httpResponse.setContentLengthLong(response.response().contentLength());
            httpResponse.setContentType(response.response().contentType());
            httpResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s",
                    storageFile.getName()));
            httpResponse.setStatus(HttpStatus.OK.value());
            IOUtils.copy(response, httpResponse.getOutputStream());
        } catch (NoSuchKeyException | InvalidObjectStateException e) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
        } catch (SdkException | IOException e) {
            throw new StorageException("Failed downloading file with" + storageFile.getId(), e);
        }
    }

    @Override
    public void delete(StorageFile storageFile) {
        var request = DeleteObjectRequest.builder()
                .bucket(s3StorageProperties.bucket())
                .key(getObjectKey(storageFile))
                .build();
        try {
            s3Client.deleteObject(request);
        } catch (SdkException e) {
            throw new StorageException("Failed to delete file", e);
        }
    }

    private StorageFile uploadFile(MultipartFile multipartFile, String folder, String name) {
        var id = UUID.randomUUID();
        var contentType = FileUtils.getMediaType(multipartFile).toString();
        var request = PutObjectRequest.builder()
                .bucket(s3StorageProperties.bucket())
                .key(getObjectKey(folder, id))
                .contentType(contentType)
                .build();
        try {
            var result = s3Client.putObject(request, RequestBody.fromInputStream(multipartFile.getInputStream(),
                    multipartFile.getSize()));
            var storageFile = new StorageFile(id, folder, name);
            return storageFileRepository.save(storageFile);
        } catch (SdkException | IOException e) {
            throw new StorageException("Failed to upload file", e);
        }
    }

    private String getObjectKey(String folder, UUID id) {
        return folder + "/" + id;
    }

    private String getObjectKey(StorageFile storageFile) {
        return getObjectKey(storageFile.getFolder(), storageFile.getId());
    }
}
