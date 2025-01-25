package pl.bronikowski.springchat.backendmain.member.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.bronikowski.springchat.backendmain.user.internal.UserConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@Size(min = 3, max = 50)
@Pattern(regexp = UserConstants.NICKNAME_REGEX)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidNickname {

    String message() default "Nickname is invalid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
