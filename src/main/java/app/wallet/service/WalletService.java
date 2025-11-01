package app.wallet.service;

import app.transaction.model.Transaction;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.web.dto.TransferRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WalletService {

    Wallet createNewWallet(User user);

    Transaction deposit(UUID walletId, BigDecimal amount, String description);

    Transaction withdrawal(User user, UUID walletId, BigDecimal amount, String description);

    long countTotalWallets();

    BigDecimal countTotalBalance();

    Transaction transfer(TransferRequest transferRequest);

    Transaction topUpBalance(UUID id);

    void switchStatus(UUID id);

    void promoteWallet(UUID id);

    void unlockNewWallet(User user);

    Map<UUID, List<Transaction>> getLastFourTransactions(List<Wallet> wallets);
}
