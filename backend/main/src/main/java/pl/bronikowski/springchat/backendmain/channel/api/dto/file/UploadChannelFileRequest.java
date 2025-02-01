package pl.bronikowski.springchat.backendmain.channel.api.dto.file;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import pl.bronikowski.springchat.backendmain.shared.validation.AllowedMediaType;

@Data
public class UploadChannelFileRequest {
    @NotNull
    @AllowedMediaType({MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    private MultipartFile file;
}
