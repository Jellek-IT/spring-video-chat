package pl.bronikowski.springchat.backendmain.user.internal;

import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bronikowski.springchat.backendmain.exception.AppNotFoundException;
import pl.bronikowski.springchat.backendmain.shared.utils.FileUtils;
import pl.bronikowski.springchat.backendmain.storage.api.StorageClient;
import pl.bronikowski.springchat.backendmain.user.api.dto.UpdateUserProfilePictureRequest;
import pl.bronikowski.springchat.backendmain.user.api.dto.UserProfileDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileMapperFactory userProfileMapperFactory;
    private final StorageClient storageClient;

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(String authResourceId) {
        var user = userRepository.findByAuthResourceId(authResourceId)
                .orElseThrow(EntityExistsException::new);
        return userProfileMapperFactory.mapToDto(user);
    }

    @Transactional
    public void updateProfilePicture(UpdateUserProfilePictureRequest request, String authResourceId) {
        var user = userRepository.findWithProfilePictureByAuthResourceId(authResourceId)
                .orElseThrow(EntityExistsException::new);
        if (user.getProfilePicture() != null) {
            storageClient.delete(user.getProfilePicture());
        }
        if (request.getFile() == null) {
            user.setProfilePicture(null);
        } else {
            var filename = FileUtils.replaceFilename(request.getFile(), "profile-picture");
            var profilePicture = storageClient.uploadUserImage(request.getFile(), user, filename);
            user.setProfilePicture(profilePicture);
        }
    }

    @Transactional(readOnly = true)
    public void getProfilePicture(String authResourceId, HttpServletResponse response) {
        var user = userRepository.findWithProfilePictureByAuthResourceId(authResourceId)
                .orElseThrow(EntityExistsException::new);
        if(user.getProfilePicture() == null) {
            throw new AppNotFoundException();
        }
        storageClient.download(user.getProfilePicture(), response);
    }
}
