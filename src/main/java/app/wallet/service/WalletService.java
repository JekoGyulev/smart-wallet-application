package app.wallet.service;

import app.transaction.model.Transaction;
import app.user.model.User;
import app.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    Wallet createNewWallet(User user);

    Transaction topUp(UUID walletId, BigDecimal amount);

    Transaction charge(User user, UUID walletId, BigDecimal amount, String chargeDescription);

    long countTotalWallets();
    BigDecimal countTotalBalance();



}
