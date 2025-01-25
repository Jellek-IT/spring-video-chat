package pl.bronikowski.springchat.backendmain.member.internal;

import org.mapstruct.Mapper;
import pl.bronikowski.springchat.backendmain.config.MapstructConfig;
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberBasicsDto;

@Mapper(config = MapstructConfig.class)
public interface MemberMapper {
    MemberBasicsDto mapToMemberBasicsDto(Member member);
}
