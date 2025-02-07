package pl.bronikowski.springchat.backendmain.shared.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {
    public static MediaType getMediaType(MultipartFile file) {
        try {
            return MediaType.parseMediaType(guessMediaType(file.getInputStream(), file.getOriginalFilename()));
        } catch (Exception e) {
            log.debug("Could not guess media type: {}", e.getMessage());
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public static MediaType getMediaType(InputStream is, String filename) {
        try {
            return MediaType.parseMediaType(guessMediaType(is, filename));
        } catch (Exception e) {
            log.debug("Could not guess media type: {}", e.getMessage());
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private static String guessMediaType(InputStream is, String filename) throws IOException {
        var mediaType = URLConnection.guessContentTypeFromStream(new BufferedInputStream(is));
        return mediaType == null
                ? URLConnection.guessContentTypeFromName(filename)
                : mediaType;
    }

    public static String replaceFilename(MultipartFile file, String name) {
        var ext = FilenameUtils.getExtension(file.getOriginalFilename());
        return name + "." + ext;
    }
}
