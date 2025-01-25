package pl.bronikowski.springchat.backendmain.member.internal;

import org.mapstruct.Mapper;
import pl.bronikowski.springchat.backendmain.config.MapstructConfig;
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberProfileDto;
import pl.bronikowski.springchat.backendmain.user.api.UserType;
import pl.bronikowski.springchat.backendmain.user.internal.UserProfileMapper;

import java.util.Set;

@Mapper(config = MapstructConfig.class)
public abstract class MemberProfileMapper extends UserProfileMapper<Member, MemberProfileDto> {
    @Override
    public Set<UserType> supportedUserTypes() {
        return Set.of(UserType.MEMBER);
    }
}
