package pl.bronikowski.springchat.backendmain.member.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.member.api.dto.MemberIdDto;
import pl.bronikowski.springchat.backendmain.member.internal.MemberRepository;

@Component
@RequiredArgsConstructor
public class ExistingMemberValidator implements ConstraintValidator<ExistingMember, MemberIdDto> {
    private final MemberRepository memberRepository;

    @Override
    public boolean isValid(MemberIdDto memberIdDto, ConstraintValidatorContext constraintValidatorContext) {
        return memberIdDto == null || memberIdDto.id() == null || memberRepository.existsById(memberIdDto.id());
    }
}
