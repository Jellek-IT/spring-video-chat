package pl.bronikowski.springchat.backendmain.channel.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.authserver.api.UserContextProvider;
import pl.bronikowski.springchat.backendmain.channel.api.ChannelMemberRight;
import pl.bronikowski.springchat.backendmain.channel.internal.member.ChannelMemberRepository;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExistingMemberChannelWithRightsValidator implements ConstraintValidator<ExistingMemberChannelWithRights, UUID> {
    private final ChannelMemberRepository channelMemberRepository;

    private Set<ChannelMemberRight> rights = Set.of();

    @Override
    public void initialize(ExistingMemberChannelWithRights allowedMediaType) {
        this.rights = Set.of(allowedMediaType.value());
    }

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || currentUserHasReadAccess(value);
    }

    private boolean currentUserHasReadAccess(UUID channelId) {
        var authResourceId = UserContextProvider.getAuthResourceId();
        return rights.isEmpty()
                ? channelMemberRepository.memberHasAccess(channelId, authResourceId)
                : channelMemberRepository.userHasAccessWithRights(channelId, authResourceId, rights);
    }
}
