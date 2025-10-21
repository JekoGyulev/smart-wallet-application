package app.email;

import app.event.SuccessfulChargeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @EventListener
    @Order(1)
    public void sendEmailWhenChargeHappens(SuccessfulChargeEvent successfulChargeEvent) {
        System.out.printf("Sending email for new payment happened for user with email [%s]"
                .formatted(successfulChargeEvent.getEmail()));
    }

}
