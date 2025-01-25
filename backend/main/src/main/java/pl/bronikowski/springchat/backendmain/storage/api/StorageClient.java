package pl.bronikowski.springchat.backendmain.storage.api;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import pl.bronikowski.springchat.backendmain.storage.internal.StorageFile;
import pl.bronikowski.springchat.backendmain.user.internal.User;

public interface StorageClient {
    StorageFile uploadUserImage(MultipartFile multipartFile, User user, String name);

    void download(StorageFile storageFile, String range, HttpServletResponse httpResponse);

    default void download(StorageFile storageFile, HttpServletResponse httpServletResponse) {
        download(storageFile, null, httpServletResponse);
    }

    void delete(StorageFile storageFile);
}
