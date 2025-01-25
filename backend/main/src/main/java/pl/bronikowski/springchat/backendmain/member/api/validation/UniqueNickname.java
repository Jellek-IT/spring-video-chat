package pl.bronikowski.springchat.backendmain.member.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueNicknameValidator.class})
public @interface UniqueNickname {
    String message() default "Provided nickname is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
