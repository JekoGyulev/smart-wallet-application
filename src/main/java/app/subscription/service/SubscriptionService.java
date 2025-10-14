package app.subscription.service;

import app.subscription.enums.SubscriptionType;
import app.subscription.model.Subscription;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.web.dto.UpgradeRequest;
import jakarta.validation.Valid;

import java.util.UUID;

public interface SubscriptionService {

    Subscription getById(UUID id);

    Subscription createDefaultSubscription(User user);

    long countTotalDefaultSubscriptions();
    long countTotalPremiumSubscriptions();
    long countTotalUltimateSubscriptions();
    long countTotalMonthlySubscriptions();
    long countTotalYearlySubscriptions();

    Transaction upgrade(User user, UpgradeRequest upgradeRequest, SubscriptionType subscriptionType);
}
