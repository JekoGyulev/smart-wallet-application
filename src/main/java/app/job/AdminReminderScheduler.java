package app.job;

import app.email.EmailService;
import app.user.enums.UserRole;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminReminderScheduler {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public AdminReminderScheduler(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "00 00 10 * * Mon")
    public void sendReminderToAdmins() {
        List<User> admins = this.userService.getAllUsers()
                .stream()
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .toList();

        admins.forEach(this.emailService::sendReminderEmail);
    }


}
