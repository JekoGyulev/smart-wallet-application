package app.subscription.service.impl;

import app.subscription.enums.SubscriptionPeriod;
import app.subscription.enums.SubscriptionStatus;
import app.subscription.enums.SubscriptionType;
import app.subscription.model.Subscription;
import app.subscription.repository.SubscriptionRepository;
import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.wallet.enums.WalletStatus;
import app.wallet.model.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public void createDefaultSubscription(User user) {
        Subscription subscription = initSubscription(user);

        this.subscriptionRepository.save(subscription);

        log.info("Successfully created new subscription with id [%s] and type [%s]"
                .formatted(subscription.getId(), subscription.getType()));
    }

    @Override
    public long countTotalDefaultSubscriptions() {
        return this.subscriptionRepository.findAll()
                .stream()
                .filter(sub -> sub.getType() == SubscriptionType.DEFAULT)
                .count();
    }

    @Override
    public long countTotalPremiumSubscriptions() {
        return this.subscriptionRepository.findAll()
                .stream()
                .filter(sub -> sub.getType() == SubscriptionType.PREMIUM)
                .count();
    }

    @Override
    public long countTotalUltimateSubscriptions() {
        return this.subscriptionRepository.findAll()
                .stream()
                .filter(sub -> sub.getType() == SubscriptionType.ULTIMATE)
                .count();
    }

    @Override
    public long countTotalMonthlySubscriptions() {
        return this.subscriptionRepository.findAll()
                .stream()
                .filter(sub -> sub.getPeriod() == SubscriptionPeriod.MONTHLY)
                .count();
    }

    @Override
    public long countTotalYearlySubscriptions() {
        return this.subscriptionRepository.findAll()
                .stream()
                .filter(sub -> sub.getPeriod() == SubscriptionPeriod.YEARLY)
                .count();
    }

    private Subscription initSubscription(User user) {

        /*
            Type: DEFAULT,
            Period: MONTHLY,
            Price: €0 (free by default)
            Renewal Eligibility: Renewals are only allowed for monthly subscriptions
         */

        LocalDateTime now = LocalDateTime.now();

        return new Subscription (
                user,
                SubscriptionStatus.ACTIVE,
                SubscriptionPeriod.MONTHLY,
                SubscriptionType.DEFAULT,
                BigDecimal.valueOf(0.00),
                true,
                now,
                now.plusMonths(1)
        );

    }
}
