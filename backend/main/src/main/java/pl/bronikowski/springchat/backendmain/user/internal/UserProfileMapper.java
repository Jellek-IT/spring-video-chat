package pl.bronikowski.springchat.backendmain.user.internal;

import pl.bronikowski.springchat.backendmain.user.api.UserType;
import pl.bronikowski.springchat.backendmain.user.api.dto.UserProfileDto;

import java.util.Set;

public abstract class UserProfileMapper<E extends User, T extends UserProfileDto> {

    public abstract T mapToDto(E user);

    public abstract Set<UserType> supportedUserTypes();
}
