package pl.bronikowski.springchat.backendmain.user.api.dto;

import lombok.Data;
import pl.bronikowski.springchat.backendmain.user.api.UserType;

import java.util.UUID;

@Data
public class UserProfileDto {
    private UUID id;
    private String email;
    private UserType type;
    private Boolean emailVerified;
    private Boolean hasProfilePicture;
}
