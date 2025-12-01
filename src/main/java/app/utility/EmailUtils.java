package app.utility;

import app.notification.client.dto.NotificationResponse;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class EmailUtils {

    public static long countSucceededEmails(List<NotificationResponse> emails ) {
        return emails.stream().filter(email -> email.getStatus().equals("SUCCEEDED")).count();
    }

    public static long countFailedEmails(List<NotificationResponse> emails ) {
        return emails.stream().filter(email -> email.getStatus().equals("FAILED")).count();
    }

}
