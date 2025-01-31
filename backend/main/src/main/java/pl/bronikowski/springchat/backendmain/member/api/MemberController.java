package pl.bronikowski.springchat.backendmain.member.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bronikowski.springchat.backendmain.member.internal.MemberService;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "[SHARED] member details", description = "member-controller")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{id}/profile-picture")
    public void getProfilePicture(@PathVariable UUID id, HttpServletResponse response) {
        memberService.getProfilePicture(id, response);
    }
}
