package app.subscription.service;

import app.subscription.model.Subscription;
import app.user.model.User;

public interface SubscriptionService {

    void createDefaultSubscription(User user);

    long countTotalDefaultSubscriptions();
    long countTotalPremiumSubscriptions();
    long countTotalUltimateSubscriptions();
    long countTotalMonthlySubscriptions();
    long countTotalYearlySubscriptions();
}
