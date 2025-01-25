package pl.bronikowski.springchat.backendmain.member.internal;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bronikowski.springchat.backendmain.authserver.api.AuthResourceClient;
import pl.bronikowski.springchat.backendmain.exception.AppNotFoundException;
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberBasicsDto;
import pl.bronikowski.springchat.backendmain.member.api.dto.RegisterMemberRequest;
import pl.bronikowski.springchat.backendmain.storage.api.StorageClient;
import pl.bronikowski.springchat.backendmain.user.internal.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final AuthResourceClient authResourceClient;
    private final MemberMapper memberMapper;
    private final StorageClient storageClient;

    @Transactional
    public MemberBasicsDto register(RegisterMemberRequest request) {
        var member = new Member(request);
        memberRepository.save(member);

        var authResourceId = authResourceClient.createUser(member, request.password());
        member.setAuthResourceId(authResourceId);

        return memberMapper.mapToMemberBasicsDto(member);
    }

    @Transactional(readOnly = true)
    public void getProfilePicture(UUID id, HttpServletResponse response) {
        var member = memberRepository.findWithProfilePictureById(id)
                .orElseThrow(AppNotFoundException::new);
        storageClient.download(member.getProfilePicture(), response);
    }
}
