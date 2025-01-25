package pl.bronikowski.springchat.backendmain.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import pl.bronikowski.springchat.backendmain.shared.utils.FileUtils;

import java.util.Arrays;
import java.util.List;

public class AllowedMimeTypeValidator implements ConstraintValidator<AllowedMediaType, MultipartFile> {
    private List<MediaType> allowedMediaTypes;

    @Override
    public void initialize(AllowedMediaType allowedMediaType) {
        this.allowedMediaTypes = Arrays.stream(allowedMediaType.value())
                .map(MediaType::parseMediaType)
                .toList();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        return value == null || isMediaTypeValid(value);
    }

    private boolean isMediaTypeValid(MultipartFile value) {
        var mediaType = FileUtils.getMediaType(value);
        return allowedMediaTypes.contains(mediaType);
    }
}
