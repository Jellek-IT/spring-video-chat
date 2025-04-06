package pl.bronikowski.springchat.backendnotifications.shared.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.bronikowski.springchat.backendnotifications.shared.enums.LanguageCode;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectConstants {
    public static final ZoneId DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;
    public static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public static final LanguageCode DEFAULT_LANGUAGE_CODE = LanguageCode.EN;
}
