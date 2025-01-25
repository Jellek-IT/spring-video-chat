package pl.bronikowski.springchat.backendmain.user.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConstants {

    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*)(+=_\\-\\\\}{\\[\\]|:;\"/?.><,`~]).*$";
    public static final String NICKNAME_REGEX = "^[ a-zA-Z0-9_-]*$";
}
