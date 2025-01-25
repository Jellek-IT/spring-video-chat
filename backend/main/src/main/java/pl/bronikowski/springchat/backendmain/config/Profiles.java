package pl.bronikowski.springchat.backendmain.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Profiles {
    public static final String OPENAPI = "openapi";
    public static final String ASYNC = "async";
    public static final String TEST = "test";
    public static final String REQUEST_LOGGING = "request-logging";
}
