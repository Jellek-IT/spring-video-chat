package pl.bronikowski.springchat.backendmain.websocket.internal.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AuthenticatedAuthenticationPrincipal implements Authentication {
    private final String name;
    private final List<SimpleGrantedAuthority> authorities;
    public AuthenticatedAuthenticationPrincipal(String name, String[] roles) {
        this.name = name;
        this.authorities = Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return name;
    }
}
