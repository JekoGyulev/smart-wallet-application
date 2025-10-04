package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferRequest {
    @NotNull
    private UUID walletId;
    @NotBlank
    private String recipientUsername;
    @Positive
    @NotNull
    private BigDecimal amount;

    public TransferRequest() {}

    public TransferRequest(UUID walletId, String recipientUsername, BigDecimal amount) {
        this.walletId = walletId;
        this.recipientUsername = recipientUsername;
        this.amount = amount;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
