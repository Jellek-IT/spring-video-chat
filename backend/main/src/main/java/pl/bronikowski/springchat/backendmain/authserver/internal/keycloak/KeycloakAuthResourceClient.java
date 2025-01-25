package pl.bronikowski.springchat.backendmain.authserver.internal.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.bronikowski.springchat.backendmain.authserver.api.AuthClientException;
import pl.bronikowski.springchat.backendmain.authserver.api.AuthResourceClient;
import pl.bronikowski.springchat.backendmain.authserver.api.KeycloakUsernameAlreadyExistsException;
import pl.bronikowski.springchat.backendmain.authserver.internal.Roles;
import pl.bronikowski.springchat.backendmain.user.internal.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAuthResourceClient implements AuthResourceClient {
    private final KeycloakProperties properties;
    private final Keycloak keycloak;
    private final KeycloakUserMapper userMapper;

    @Override
    public String createUser(User user, String password) {
        var userRepresentation = userMapper.mapToUserRepresentation(user, password);
        var userResource = getUsersResource();
        try (var response = userResource.create(userRepresentation)) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                var authResourceId = CreatedResponseUtil.getCreatedId(response);
                assignRealmRoleToUser(authResourceId, List.of(user.getType().getRole()));
                return authResourceId;
            }
            handleErrorResponse(response, userRepresentation);
        }
        throw new AuthClientException("Could not create user with keycloak");
    }

    private void assignRealmRoleToUser(String authResourceId, List<String> roles) {
        var userResource = getUserResource(authResourceId);
        List<RoleRepresentation> rolesToAdd = getRolesByName(roles);
        var rolesToRemove = getAppUserRoles(userResource.roles().realmLevel().listAll());
        userResource.roles().realmLevel().remove(rolesToRemove);
        userResource.roles().realmLevel().add(rolesToAdd);
    }

    private List<RoleRepresentation> getAppUserRoles(List<RoleRepresentation> roles) {
        return roles.stream()
                .filter(roleRepresentation -> Roles.ALL_USER_ROLES.contains(roleRepresentation.getName()))
                .toList();
    }

    private List<RoleRepresentation> getRolesByName(List<String> roles) {
        return roles.stream()
                .map(role -> keycloak.realm(properties.realm()).roles().get(role).toRepresentation())
                .toList();
    }

    private void handleErrorResponse(Response response, UserRepresentation userRepresentation) {
        if (response.getStatus() == HttpStatus.CONFLICT.value()) {
            throw new KeycloakUsernameAlreadyExistsException(userRepresentation.getEmail());
        } else {
            throw new AuthClientException(String.valueOf(response.getStatus()));
        }
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(properties.realm()).users();
    }

    private UserResource getUserResource(String authResourceId) {
        return getUsersResource().get(authResourceId);
    }
}
