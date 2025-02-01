package pl.bronikowski.springchat.backendmain.channel.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.bronikowski.springchat.backendmain.channel.api.dto.message.CreateChannelMessagePayload;

public class ChannelMessageWithContentValidator implements ConstraintValidator<ChannelMessageWithContent, CreateChannelMessagePayload> {

    @Override
    public boolean isValid(CreateChannelMessagePayload value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || !value.text().isBlank() || !value.files().isEmpty();
    }
}
