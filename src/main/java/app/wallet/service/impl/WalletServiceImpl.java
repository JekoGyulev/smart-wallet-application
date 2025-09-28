package app.wallet.service.impl;

import app.exception.DomainException;
import app.transaction.enums.TransactionStatus;
import app.transaction.enums.TransactionType;
import app.transaction.model.Transaction;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.enums.WalletStatus;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import app.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private static final String SMART_WALLET_LTD = "SMART WALLET LTD";
    private final WalletRepository walletRepository;

    private final TransactionService transactionService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet = initWallet(user);

        this.walletRepository.save(wallet);

        log.info("Successfully created new wallet with id [%s] and balance %.2f"
                .formatted(wallet.getId(), wallet.getBalance()));

        return wallet;
    }

    @Override
    @Transactional
    public Transaction topUp(UUID walletId, BigDecimal amount) {

        Wallet wallet = getWalletById(walletId);

        String description = "Top up %.2f".formatted(amount.doubleValue());

        if (wallet.getStatus() == WalletStatus.INACTIVE) {

            String failedReason = "Inactive wallet";

            return transactionService
                    .createNewTransaction(
                                    wallet.getOwner(),
                                    SMART_WALLET_LTD,
                                    walletId.toString(),
                                    amount,
                                    wallet.getBalance(),
                                    wallet.getCurrency(),
                                    TransactionType.DEPOSIT,
                                    TransactionStatus.FAILED,
                                    description,
                                    failedReason
                    );
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedOn(LocalDateTime.now());

        this.walletRepository.save(wallet);

        Transaction transaction = transactionService.createNewTransaction(
                                    wallet.getOwner(),
                                    SMART_WALLET_LTD,
                                    walletId.toString(),
                                    amount,
                                    wallet.getBalance(),
                                    wallet.getCurrency(),
                                    TransactionType.DEPOSIT,
                                    TransactionStatus.SUCCEEDED,
                                    description,
                                    null
        );

        return transaction;

    }

    @Override
    public Transaction charge(User user, UUID walletId, BigDecimal amount, String chargeDescription) {

        Wallet wallet = getWalletById(walletId);

        TransactionStatus status;
        String failureReason = null;


        if (wallet.getStatus() == WalletStatus.INACTIVE) {
            status = TransactionStatus.FAILED;
            failureReason = "Inactive wallet";

        } else if (wallet.getBalance().compareTo(amount) < 0) {
            status = TransactionStatus.FAILED;
            failureReason = "Insufficient funds";

        } else {
            wallet.setBalance(wallet.getBalance().subtract(amount));
            wallet.setUpdatedOn(LocalDateTime.now());
            this.walletRepository.save(wallet);
            status = TransactionStatus.SUCCEEDED;
        }


        return transactionService.createNewTransaction (
                user,
                SMART_WALLET_LTD,
                walletId.toString(),
                amount,
                wallet.getBalance(),
                wallet.getCurrency(),
                TransactionType.WITHDRAWAL,
                status,
                chargeDescription,
                failureReason
        );


    }

    @Override
    public long countTotalWallets() {
        return this.walletRepository.count();
    }

    @Override
    public BigDecimal countTotalBalance() {
        return this.walletRepository.findAll()
                .stream()
                .map(Wallet::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private Wallet getWalletById(UUID walletId) {
        return this.walletRepository
                .findById(walletId)
                .orElseThrow(() -> new DomainException("Wallet with such id [%s] does not exist"
                        .formatted(walletId)));
    }


    private Wallet initWallet(User user) {
        /*
            Initial balance: €20
            Default Status: ACTIVE
            Currency: "EUR"
         */

        return new Wallet(
                user,
                WalletStatus.ACTIVE,
                BigDecimal.valueOf(20.00),
                Currency.getInstance("EUR"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


}
