package pl.bronikowski.springchat.backendmain.authserver.internal.keycloak;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import pl.bronikowski.springchat.backendmain.user.internal.User;

import java.util.List;

@Component
public class KeycloakUserMapper {
    public UserRepresentation mapToUserRepresentation(User user, String password) {
        var credential = mapToCredentialRepresentation(password);

        var userRepresentation = new UserRepresentation();
        updateUserRepresentationFromUser(userRepresentation, user);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(List.of(credential));

        return userRepresentation;
    }

    public void updateUserRepresentationFromUser(UserRepresentation userRepresentation, User user) {
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEmailVerified(user.getEmailVerified());
    }

    public CredentialRepresentation mapToCredentialRepresentation(String password) {
        var credentialsRepresentation = new CredentialRepresentation();
        credentialsRepresentation.setTemporary(false);
        credentialsRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialsRepresentation.setValue(password);

        return credentialsRepresentation;
    }
}
