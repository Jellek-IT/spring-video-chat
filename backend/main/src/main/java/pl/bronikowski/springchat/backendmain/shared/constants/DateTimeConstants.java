package pl.bronikowski.springchat.backendmain.shared.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeConstants {
    public static final ZoneId DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;
}
