package app.user.service.impl;

import app.user.enums.Country;
import app.user.model.User;
import app.user.property.UserProperties;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserInit implements CommandLineRunner {

    private final UserService userService;
    private final UserProperties userProperties;

    @Autowired
    public UserInit(UserService userService, UserProperties userProperties) {
        this.userService = userService;
        this.userProperties = userProperties;
    }

    @Override
    public void run(String... args) throws Exception {

        List<User> users = userService.getAllUsers();

        boolean userDefaultDoesNotExist = users
                .stream()
                .noneMatch(user -> user.getUsername()
                        .equals(userProperties.getDefaultUser().getUsername()));

        if (userDefaultDoesNotExist) {

            RegisterRequest request =
                    new RegisterRequest(userProperties.getDefaultUser().getUsername(),
                            userProperties.getDefaultUser().getPassword(),
                            userProperties.getDefaultUser().getCountry());

            this.userService.registerUser(request);
        }
    }
}
