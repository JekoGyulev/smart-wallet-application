package app.user.service;

import app.user.model.User;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User registerUser(RegisterRequest request);

    User loginUser(LoginRequest request);

    List<User> getAllUsers();

    User getById(UUID id);

    long countActiveUsers();
    long countInactiveUsers();
    long countAdminUsers();
    long countNonAdminUsers();
    long countUsersWithWallets(int countWallets);

}
