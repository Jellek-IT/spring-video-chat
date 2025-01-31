package pl.bronikowski.springchat.backendmain.channel.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChannelBasicsDto {
    private UUID id;
    private String name;
    private Boolean hasThumbnail;
}
