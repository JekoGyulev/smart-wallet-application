package app.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuccessfulChargeEvent {

    private UUID userId;
    private UUID walletId;
    private BigDecimal amount;
    private LocalDateTime createdOn;
    private String email;
}
