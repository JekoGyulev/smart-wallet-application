package app.notification.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateNotificationRequest {
    private UUID userId;
    private String subject;
    private String body;
}
