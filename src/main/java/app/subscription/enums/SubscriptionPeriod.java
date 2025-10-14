package app.subscription.enums;

public enum SubscriptionPeriod {
    MONTHLY("Monthly"),
    YEARLY("Yearly");

    private String displayName;

    SubscriptionPeriod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
