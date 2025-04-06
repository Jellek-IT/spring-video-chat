package pl.bronikowski.springchat.backendmain.user.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bronikowski.springchat.backendmain.authserver.api.UserContextProvider;
import pl.bronikowski.springchat.backendmain.user.api.dto.UpdateUserProfilePictureRequest;
import pl.bronikowski.springchat.backendmain.user.api.dto.UserProfileDto;
import pl.bronikowski.springchat.backendmain.user.api.dto.VerifyEmailRequest;
import pl.bronikowski.springchat.backendmain.user.internal.UserService;

@Validated
@RestController
@RequestMapping("/users/profile")
@RequiredArgsConstructor
@Tag(name = "[SHARED] Users profile management", description = "user-profile-controller")
public class UserProfileController {
    private final UserService userService;

    @GetMapping
    public UserProfileDto getUserProfile() {
        var authResourceId = UserContextProvider.getAuthResourceId();
        return userService.getUserProfile(authResourceId);
    }

    @PostMapping("/verify-email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        userService.verifyEmail(request, authResourceId);
    }

    @PostMapping("/profile-picture")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfilePicture(@Valid UpdateUserProfilePictureRequest request) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        userService.updateProfilePicture(request, authResourceId);
    }

    @GetMapping("/profile-picture")
    public void getProfilePicture(HttpServletResponse response) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        userService.getProfilePicture(authResourceId, response);
    }
}
