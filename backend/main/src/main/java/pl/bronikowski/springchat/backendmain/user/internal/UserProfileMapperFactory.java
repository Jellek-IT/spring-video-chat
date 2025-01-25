package pl.bronikowski.springchat.backendmain.user.internal;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import pl.bronikowski.springchat.backendmain.config.MapstructConfig;
import pl.bronikowski.springchat.backendmain.user.api.UserType;
import pl.bronikowski.springchat.backendmain.user.api.dto.UserProfileDto;

import java.util.List;

@Mapper(config = MapstructConfig.class)
public class UserProfileMapperFactory {
    @Autowired
    private List<UserProfileMapper<? extends User, ? extends UserProfileDto>> userProfileMappers;

    public UserProfileDto mapToDto(User user) {
        return getMapper(user.getType()).mapToDto(user);
    }

    @SuppressWarnings("unchecked")
    private UserProfileMapper<User, UserProfileDto> getMapper(UserType type) {
        return (UserProfileMapper<User, UserProfileDto>) userProfileMappers.stream()
                .filter(mapper -> mapper.supportedUserTypes().contains(type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not find mapper for given user type"));
    }
}
