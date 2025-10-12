package app.user.service;

import app.user.model.User;
import app.web.dto.LoginRequest;
import app.web.dto.ProfileEditRequest;
import app.web.dto.RegisterRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User getDefaultUser();

    User registerUser(RegisterRequest request);

    User loginUser(LoginRequest request);

    User getByUsername(String username);

    List<User> getAllUsers();

    User getById(UUID id);

    void switchStatus(UUID id);
    void switchRole(UUID id);

    long countActiveUsers();
    long countInactiveUsers();
    long countAdminUsers();
    long countNonAdminUsers();
    long countUsersWithWallets(int countWallets);

    void updateProfile(User user, ProfileEditRequest profileEditRequest);
}
