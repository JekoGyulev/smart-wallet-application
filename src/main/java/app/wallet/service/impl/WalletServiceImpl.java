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
import app.web.dto.TransferRequest;
import jakarta.validation.Valid;
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

    private static final String TRANSFER_DESCRIPTION_FORMAT = "Transfer %s <> %s (%.2f)";
    private static final String TOP_UP_DESCRIPTION_FORMAT = "Top-up %.2f";


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
    public Transaction deposit(UUID walletId, BigDecimal amount, String description) {

        Wallet wallet = getWalletById(walletId);

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
    @Transactional
    public Transaction withdrawal(User user, UUID walletId, BigDecimal amount, String description) {

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
                description,
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

    @Override
    @Transactional
    public Transaction transfer(@Valid TransferRequest transferRequest) {

        Wallet senderWallet = this.getWalletById(transferRequest.getWalletId());

        Wallet receiverWallet = this.walletRepository.findWalletByOwnerUsername(transferRequest.getRecipientUsername())
                .stream()
                .filter(this::isActiveWallet)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("[%s] doesn't have active wallets"
                        .formatted(transferRequest.getRecipientUsername())));


        String transferDescription = TRANSFER_DESCRIPTION_FORMAT
                .formatted( senderWallet.getOwner().getUsername(),
                            receiverWallet.getOwner().getUsername(),
                            transferRequest.getAmount()
                );





        Transaction withdrawalTransaction = withdrawal(senderWallet.getOwner(), senderWallet.getId(),
                transferRequest.getAmount(), transferDescription);

        Transaction depositTransaction = deposit(receiverWallet.getId(), transferRequest.getAmount(),
                transferDescription);



        return withdrawalTransaction;
    }

    private boolean isActiveWallet(Wallet wallet) {
        return wallet.getStatus() == WalletStatus.ACTIVE;
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
