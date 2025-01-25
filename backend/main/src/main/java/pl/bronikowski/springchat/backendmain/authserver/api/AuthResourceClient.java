package pl.bronikowski.springchat.backendmain.authserver.api;

import pl.bronikowski.springchat.backendmain.user.internal.User;

public interface AuthResourceClient {

    String createUser(User user, String password);
}
