package pl.bronikowski.springchat.backendmain.member.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberBasicsDto;
import pl.bronikowski.springchat.backendmain.member.api.dto.RegisterMemberRequest;
import pl.bronikowski.springchat.backendmain.member.internal.MemberService;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/public/members")
@RequiredArgsConstructor
@Tag(name = "[PUBLIC] public member management", description = "public-member-controller")
public class PublicMemberController {
    private final MemberService memberService;

    @PostMapping
    public MemberBasicsDto register(@Valid @RequestBody RegisterMemberRequest request) {
        return memberService.register(request);
    }
}
