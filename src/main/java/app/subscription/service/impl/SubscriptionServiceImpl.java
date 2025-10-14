package app.subscription.service.impl;

import app.subscription.enums.SubscriptionPeriod;
import app.subscription.enums.SubscriptionStatus;
import app.subscription.enums.SubscriptionType;
import app.subscription.model.Subscription;
import app.subscription.repository.SubscriptionRepository;
import app.subscription.service.SubscriptionService;
import app.transaction.enums.TransactionStatus;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.wallet.enums.WalletStatus;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import app.web.dto.UpgradeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final WalletService walletService;

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionServiceImpl(WalletService walletService, SubscriptionRepository subscriptionRepository) {
        this.walletService = walletService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription getById(UUID id) {
        return this.subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    @Override
    public Subscription createDefaultSubscription(User user) {
        Subscription subscription = initSubscription(user);

        this.subscriptionRepository.save(subscription);

        log.info("Successfully created new subscription with id [%s] and type [%s]"
                .formatted(subscription.getId(), subscription.getType()));

        return subscription;
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

    @Override
    public Transaction upgrade(User user, UpgradeRequest upgradeRequest, SubscriptionType subscriptionType) {


        Optional<Subscription> currentlyActiveSubscriptionOptional = this.subscriptionRepository
                .findByStatusAndOwnerId(SubscriptionStatus.ACTIVE, user.getId());

        if (currentlyActiveSubscriptionOptional.isEmpty()) {
            throw new RuntimeException("No current subscription found for user with [%s]"
                    .formatted(user.getId()));
        }

        Subscription currentlyActiveSubscription = currentlyActiveSubscriptionOptional.get();

        BigDecimal subscriptionPrice = getUpgradePrice(subscriptionType,upgradeRequest.getSubscriptionPeriod());

        String chargeDescription = "Upgrade request for %s %s"
                .formatted(upgradeRequest.getSubscriptionPeriod().getDisplayName(),
                        currentlyActiveSubscription.getType());

        Transaction transaction = this.walletService
                .withdrawal(user, upgradeRequest.getWalletId(), subscriptionPrice, chargeDescription);


        if (transaction.getStatus() == TransactionStatus.FAILED) {
            return transaction;
        }


        // 1, Create new subscription
        // 2. Complete their current active subscription

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryOn;

        if (upgradeRequest.getSubscriptionPeriod() == SubscriptionPeriod.MONTHLY) {
            expiryOn = now.plusMonths(1).truncatedTo(ChronoUnit.DAYS);
        } else {
            expiryOn = now.plusYears(1).truncatedTo(ChronoUnit.YEARS);
        }

        Subscription newActiveSubscription = new Subscription(
                user,
                SubscriptionStatus.ACTIVE,
                upgradeRequest.getSubscriptionPeriod(),
                subscriptionType,
                subscriptionPrice,
                upgradeRequest.getSubscriptionPeriod() == SubscriptionPeriod.MONTHLY,
                now,
                expiryOn
        );

        currentlyActiveSubscription.setStatus(SubscriptionStatus.COMPLETED);
        currentlyActiveSubscription.setExpiryOn(now);

        subscriptionRepository.save(currentlyActiveSubscription);
        subscriptionRepository.save(newActiveSubscription);

        return transaction;
    }

    private BigDecimal getUpgradePrice(SubscriptionType subscriptionType, SubscriptionPeriod subscriptionPeriod) {

        if (subscriptionType == SubscriptionType.DEFAULT) {
            return BigDecimal.ZERO;
        } else if (subscriptionType == SubscriptionType.PREMIUM && subscriptionPeriod == SubscriptionPeriod.MONTHLY) {
            return new BigDecimal("19.99");
        } else if (subscriptionType == SubscriptionType.PREMIUM && subscriptionPeriod == SubscriptionPeriod.YEARLY ) {
            return new BigDecimal("199.99");
        } else if (subscriptionType == SubscriptionType.ULTIMATE && subscriptionPeriod == SubscriptionPeriod.MONTHLY) {
            return new BigDecimal("49.99");
        } else if (subscriptionType == SubscriptionType.ULTIMATE && subscriptionPeriod == SubscriptionPeriod.YEARLY) {
            return new BigDecimal("499.99");
        }

        throw new RuntimeException("Price not found for type [%s] and period [%s]"
                .formatted(subscriptionType, subscriptionPeriod));
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
