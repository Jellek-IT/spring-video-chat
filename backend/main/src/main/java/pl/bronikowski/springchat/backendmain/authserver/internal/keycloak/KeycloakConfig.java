package pl.bronikowski.springchat.backendmain.authserver.internal.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles;

import java.util.Collection;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {
    private static final String REALM_ACCESS_CLAIM_KEY = "realm_access";
    private static final String REALM_ACCESS_CLAIM_ROLES_KEY = "roles";

    private final KeycloakProperties props;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(props.serverUrl())
                .realm(props.realm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(props.clientId())
                .clientSecret(props.clientSecret())
                .build();
    }

    @Bean
    @SuppressWarnings("unused")
    public JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
        Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
            Map<String, Collection<String>> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM_KEY);
            Collection<String> roles = realmAccess.get(REALM_ACCESS_CLAIM_ROLES_KEY);
            return roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(Roles.GRANTED_AUTHORITY_PREFIX + role))
                    .toList();
        };

        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
