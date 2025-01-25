package pl.bronikowski.springchat.backendmain.user.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.bronikowski.springchat.backendmain.user.internal.UserConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Size(min = 8)
@Pattern(regexp = UserConstants.PASSWORD_REGEX)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidPassword {

    String message() default "Password is invalid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
