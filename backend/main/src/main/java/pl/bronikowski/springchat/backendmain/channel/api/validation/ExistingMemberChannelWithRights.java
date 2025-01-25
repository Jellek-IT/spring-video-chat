package pl.bronikowski.springchat.backendmain.channel.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExistingMemberChannelWithRightsValidator.class})
public @interface ExistingMemberChannelWithRights {
    String message() default "Provided channel does not exist or user does not have enough rights";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ChannelMemberRight[] value() default {};
}
