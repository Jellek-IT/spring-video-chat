package pl.bronikowski.springchat.backendmain.config.stomp

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles

class StompTestAuthentication implements Authentication {

    private final String name
    private final List<SimpleGrantedAuthority> authorities

    StompTestAuthentication(String name, String[] roles) {
        this.name = name
        this.authorities = roles.collect { new SimpleGrantedAuthority(Roles.GRANTED_AUTHORITY_PREFIX + it) }
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities
    }

    @Override
    Object getCredentials() {
        return null
    }

    @Override
    Object getDetails() {
        return null
    }

    @Override
    Object getPrincipal() {
        return null
    }

    @Override
    boolean isAuthenticated() {
        return true
    }

    @Override
    void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // No-op
    }

    @Override
    String getName() {
        return name
    }
}
