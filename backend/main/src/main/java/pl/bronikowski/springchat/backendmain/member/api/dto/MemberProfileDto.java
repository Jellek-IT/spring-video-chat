package pl.bronikowski.springchat.backendmain.member.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.bronikowski.springchat.backendmain.user.api.dto.UserProfileDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberProfileDto extends UserProfileDto {
    private String nickname;
}
