package pl.bronikowski.springchat.backendmain.channel.api.dto;

import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import pl.bronikowski.springchat.backendmain.shared.validation.AllowedMediaType;

@Data
public class UpdateChannelThumbnailRequest {
    @AllowedMediaType({MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    private MultipartFile file;
}
