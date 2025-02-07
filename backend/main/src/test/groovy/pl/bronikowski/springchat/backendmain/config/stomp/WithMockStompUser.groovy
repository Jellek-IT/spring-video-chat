package pl.bronikowski.springchat.backendmain.config.stomp

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target([ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
@interface WithMockStompUser {
    String value() default "user";

    String[] roles() default ["USER"];

}