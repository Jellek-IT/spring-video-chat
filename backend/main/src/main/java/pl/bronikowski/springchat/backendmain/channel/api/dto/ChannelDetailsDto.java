package pl.bronikowski.springchat.backendmain.channel.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.bronikowski.springchat.backendmain.channel.api.dto.member.ChannelMemberDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChannelDetailsDto extends ChannelBasicsDto {
    private List<ChannelMemberDto> members;
}
