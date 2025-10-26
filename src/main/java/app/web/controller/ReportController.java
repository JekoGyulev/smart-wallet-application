package app.web.controller;

import app.subscription.service.SubscriptionService;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final UserService userService;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;
    private final TransactionService transactionService;


    @Autowired
    public ReportController(UserService userService, WalletService walletService, SubscriptionService subscriptionService, TransactionService transactionService) {
        this.userService = userService;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
        this.transactionService = transactionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getReportsPage() {
        Map<String, Object> stats = getStats();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reports");
        modelAndView.addObject("stats", stats);

        return modelAndView;
    }

    private Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // Get total users
        stats.put("totalUsers", this.userService.getAllUsers().size());
        // Get total ACTIVE users
        stats.put("activeUsers", this.userService.countActiveUsers());
        // Get total INACTIVE users
        stats.put("inactiveUsers", this.userService.countInactiveUsers());
        // Get total ADMIN users
        stats.put("adminUsers", this.userService.countAdminUsers());
        // Get total NON-ADMIN users
        stats.put("nonAdminUsers", this.userService.countNonAdminUsers());

        // Get total wallets
        stats.put("totalWallets", this.walletService.countTotalWallets());
        // Get total amount OF ALL WALLETS COMBINED TO ONE
        stats.put("totalWalletsBalance", this.walletService.countTotalBalance());

        // Get percentage of users with 1, 2, 3 wallets
        int totalUsers = this.userService.getAllUsers().size();

        if (totalUsers == 0) {

            stats.put("percentageWith1Wallets", 0.0);
            stats.put("percentageWith2Wallets", 0.0);
            stats.put("percentageWith3Wallets", 0.0);

        } else {

            for (int i = 1; i <= 3; i++) {
                double percentage = ((double) this.userService.countUsersWithWallets(i) / totalUsers) * 100;
                stats.put("percentageWith" + i + "Wallets", percentage);
            }

        }


        // Get total transactions
        stats.put("totalTransactions", this.transactionService.countTotalTransactions());
        // Get total amount of ALL TRANSACTIONS COMBINED TO ONE
        stats.put("totalAmount", this.transactionService.countTotalAmount());
        // Get total WITHDRAWAL transactions
        stats.put("totalWithdrawalTransactions", this.transactionService.countTotalWithdrawalTransactions());
        // Get total DEPOSIT transactions
        stats.put("totalDepositTransactions", this.transactionService.countTotalDepositTransactions());
        // Get total SUCCEEDED transactions
        stats.put("totalSucceededTransactions", this.transactionService.countTotalSucceededTransactions());
        // Get total FAILED transactions
        stats.put("totalFailedTransactions", this.transactionService.countTotalFailedTransactions());


        // Get total subscriptions of type DEFAULT
        stats.put("totalDefaultSubscriptions", this.subscriptionService.countTotalDefaultSubscriptions());
        // Get total subscriptions of type PREMIUM
        stats.put("totalPremiumSubscriptions", this.subscriptionService.countTotalPremiumSubscriptions());
        // Get total subscriptions of type ULTIMATE
        stats.put("totalUltimateSubscriptions", this.subscriptionService.countTotalUltimateSubscriptions());
        // Get total subscriptions of period MONTHLY
        stats.put("totalMonthlySubscriptions", this.subscriptionService.countTotalMonthlySubscriptions());
        // Get total subscriptions of period YEARLY
        stats.put("totalYearlySubscriptions", this.subscriptionService.countTotalYearlySubscriptions());


        return stats;
    }
}
