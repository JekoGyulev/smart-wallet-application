package app.web.controller;

import app.security.UserData;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.utility.WalletUtils;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    @Autowired
    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }


    @GetMapping
    public ModelAndView getWalletsPage(@AuthenticationPrincipal UserData userData) {

        User user = this.userService.getById(userData.getId());

        List<Wallet> wallets = user.getWallets();

        Map<UUID, List<Transaction>> transactionsByWalletId = this.walletService.getLastFourTransactions(wallets);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("wallets");
        modelAndView.addObject("wallets", wallets);
        modelAndView.addObject("isEligibleToUnlock", WalletUtils.isEligibleToUnlockNewWallet(user));
        modelAndView.addObject("transactionsByWalletId", transactionsByWalletId);

        return modelAndView;
    }

    @PatchMapping("/{id}/balance")
    public String topUpBalance(@PathVariable UUID id) {
        Transaction transaction = this.walletService.topUpBalance(id);
        return "redirect:/transactions/" + transaction.getId();
    }

    @PatchMapping("/{id}/status")
    public String switchWalletStatus(@PathVariable UUID id) {
        this.walletService.switchStatus(id);
        return "redirect:/wallets";
    }

    @PatchMapping("/{id}/promote")
    public String promoteWalletToPrimary(@PathVariable UUID id) {
        this.walletService.promoteWallet(id);
        return "redirect:/wallets";
    }

    @PostMapping("/create")
    public String unlockNewWallet(@AuthenticationPrincipal UserData userData) {
        User user = this.userService.getById(userData.getId());
        this.walletService.unlockNewWallet(user);
        return "redirect:/wallets";
    }







}
