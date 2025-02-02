package pl.bronikowski.springchat.backendmain.channel.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Size(max = 50000)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidChannelMessageText {

    String message() default "Message text is invalid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
