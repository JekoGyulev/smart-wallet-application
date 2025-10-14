package app.web.dto;

import app.subscription.enums.SubscriptionPeriod;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UpgradeRequest {
    @NotNull
    private SubscriptionPeriod subscriptionPeriod;
    @NotNull
    private UUID walletId;


    public UpgradeRequest() {}

    public UpgradeRequest(SubscriptionPeriod subscriptionPeriod, UUID walletId) {
        this.subscriptionPeriod = subscriptionPeriod;
        this.walletId = walletId;
    }

    public SubscriptionPeriod getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void setSubscriptionPeriod(SubscriptionPeriod subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }
}
