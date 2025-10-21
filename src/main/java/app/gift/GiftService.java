package app.gift;


import app.event.SuccessfulChargeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
public class GiftService {

    @EventListener
    @Order(2)
    public void sendGift(SuccessfulChargeEvent  successfulChargeEvent) {
        System.out.printf("Sending 1 Euro for charge compensation for user with email [%s]."
                .formatted(successfulChargeEvent.getEmail()));
    }
}
