package app.user.service.impl;

import app.user.enums.Country;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserInit implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public UserInit(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userService.getAllUsers().isEmpty()) {
            RegisterRequest request = new RegisterRequest("Vik123", "Vik123", Country.BULGARIA);

            this.userService.registerUser(request);
        }



    }
}
