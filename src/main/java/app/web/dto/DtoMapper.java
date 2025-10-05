package app.web.dto;

import app.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    // Converts User object towards ProfileEditRequest object
    public static ProfileEditRequest fromUser(User user) {
        return new ProfileEditRequest(
                                            user.getFirstName(),
                                            user.getLastName(),
                                            user.getEmail(),
                                            user.getProfilePicture()
        );
    }
}
