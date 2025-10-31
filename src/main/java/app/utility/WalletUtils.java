package app.utility;

import app.subscription.enums.SubscriptionType;
import app.subscription.model.Subscription;
import app.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WalletUtils {

    public static boolean isEligibleToUnlockNewWallet(User user) {

        // DEFAULT -> can't
        // PREMIUM && user.getWallets().size() < 2-> can
        // ULTIMATE && user.getWallets().size() < 3 -> can

        Subscription currentSubscription = user.getSubscriptions().get(0);

        int countWallets = user.getWallets().size();

        return (currentSubscription.getType() == SubscriptionType.PREMIUM && countWallets < 2) ||
                (currentSubscription.getType() == SubscriptionType.ULTIMATE && countWallets < 3);
    }


}
