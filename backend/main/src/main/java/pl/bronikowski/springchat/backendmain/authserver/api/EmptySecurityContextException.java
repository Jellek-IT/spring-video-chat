package pl.bronikowski.springchat.backendmain.authserver.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class EmptySecurityContextException extends RuntimeException {
}
