package app.email;

import app.event.SuccessfulChargeEvent;
import app.user.model.User;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @EventListener
    @Async
    @Order(1)
    public void sendEmailWhenChargeHappens(SuccessfulChargeEvent successfulChargeEvent) {
        System.out.printf("Sending email for new payment happened for user with email [%s]"
                .formatted(successfulChargeEvent.getEmail()));
    }


    public void sendReminderEmail(User admin) {
        System.out.printf("Email sent to [%s] with username [%s]%n", admin.getRole(), admin.getUsername());
    }
}
