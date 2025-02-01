package pl.bronikowski.springchat.backendmain.channel.api.dto.message;

import lombok.Data;
import pl.bronikowski.springchat.backendmain.channel.api.dto.file.ChannelFileDto;
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberBasicsDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ChannelMessageBasicsDto {
    private UUID id;
    private Instant createdAt;
    private String text;
    private Long sequence;
    private MemberBasicsDto member;
    private List<ChannelFileDto> files;
}
