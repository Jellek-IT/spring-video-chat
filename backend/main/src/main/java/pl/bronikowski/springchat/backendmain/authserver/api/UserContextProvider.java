package pl.bronikowski.springchat.backendmain.authserver.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserContextProvider {
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public static String getAuthResourceId() {
        return get().authResourceId();
    }

    public static UserContext get() {
        var authentication = getAuthentication();
        if (authentication != null) {
            return new UserContext(getAuthResourceId(authentication), convertRoles(authentication.getAuthorities()));
        } else {
            throw new EmptySecurityContextException();
        }
    }

    public boolean isAuthenticated() {
        var authentication = getAuthentication();
        return authentication != null && !trustResolver.isAnonymous(authentication) && authentication.isAuthenticated();
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getAuthResourceId(Authentication authentication) {
        return authentication.getName();
    }

    private static List<String> convertRoles(Collection<? extends GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replaceAll(Roles.GRANTED_AUTHORITY_PREFIX, ""))
                .toList();
    }
}
