package app.wallet.service.impl;

import app.event.SuccessfulChargeEvent;
import app.exception.DomainException;
import app.transaction.enums.TransactionStatus;
import app.transaction.enums.TransactionType;
import app.transaction.model.Transaction;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.utility.WalletUtils;
import app.wallet.enums.WalletStatus;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import app.wallet.service.WalletService;
import app.web.dto.TransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private static final String SMART_WALLET_LTD = "SMART WALLET LTD";
    private static final String TRANSFER_DESCRIPTION_FORMAT = "Transfer %s <> %s (%.2f)";
    private static final String TOP_UP_DESCRIPTION_FORMAT = "Top-up %.2f";
    private static final String FIRST_WALLET_NICKNAME = "Vault Zero";
    private static final String SECOND_WALLET_NICKNAME = "Nova Flow";
    private static final String THIRD_WALLET_NICKNAME = "Pulse Pay";

    private final WalletRepository walletRepository;

    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;


    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, TransactionService transactionService, ApplicationEventPublisher eventPublisher) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
        this.eventPublisher = eventPublisher;
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

            // Event
            SuccessfulChargeEvent event = SuccessfulChargeEvent.builder()
                            .userId(user.getId())
                                    .walletId(wallet.getId())
                                            .amount(amount)
                                                    .createdOn(LocalDateTime.now())
                                                            .build();

            this.eventPublisher.publishEvent(event);


        }


        return transactionService.createNewTransaction (
                user,
                walletId.toString(),
                SMART_WALLET_LTD,
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
    public Transaction transfer(TransferRequest transferRequest) {

        Wallet senderWallet = this.getWalletById(transferRequest.getWalletId());

        Wallet receiverWallet = this.walletRepository.findWalletByOwnerUsername(transferRequest.getRecipientUsername())
                .stream()
                .filter(Wallet::isPrimary)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("[%s] doesn't have active wallets"
                        .formatted(transferRequest.getRecipientUsername())));


        String transferDescription = TRANSFER_DESCRIPTION_FORMAT
                .formatted(senderWallet.getOwner().getUsername(),
                        receiverWallet.getOwner().getUsername(),
                        transferRequest.getAmount()
                );


        Transaction withdrawalTransaction = withdrawal(senderWallet.getOwner(), senderWallet.getId(),
                transferRequest.getAmount(), transferDescription);

        if (withdrawalTransaction.getStatus() == TransactionStatus.SUCCEEDED) {
            deposit(receiverWallet.getId(), transferRequest.getAmount(), transferDescription);
        }

        return withdrawalTransaction;
    }

    @Override
    @Transactional
    public Transaction topUpBalance(UUID walletId) {
        BigDecimal amount = new BigDecimal("20.00");

        return deposit(walletId, amount, TOP_UP_DESCRIPTION_FORMAT.formatted(amount));
    }

    @Override
    public void switchStatus(UUID walletId) {
        Wallet wallet = getWalletById(walletId);

        if (wallet.getStatus() == WalletStatus.ACTIVE) {
            if (wallet.isPrimary()) {
                throw new RuntimeException("Primary wallets cannot be inactive");
            }
            wallet.setStatus(WalletStatus.INACTIVE);
        } else {
            wallet.setStatus(WalletStatus.ACTIVE);
        }

        this.walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void promoteWallet(UUID walletId) {
        Wallet wallet = getWalletById(walletId);

        if (wallet.isPrimary()) {
            throw new RuntimeException("This wallet is already primary");
        }

        User owner = wallet.getOwner();

        Optional<Wallet> optionalCurrentPrimaryWallet = this.walletRepository.findByOwner_IdAndPrimary(owner.getId(), true);

        if (optionalCurrentPrimaryWallet.isPresent()) {

            Wallet currentPrimaryWallet = optionalCurrentPrimaryWallet.get();
            currentPrimaryWallet.setPrimary(false);
            currentPrimaryWallet.setUpdatedOn(LocalDateTime.now());
            this.walletRepository.save(currentPrimaryWallet);

        }

        wallet.setPrimary(true);
        wallet.setUpdatedOn(LocalDateTime.now());
        this.walletRepository.save(wallet);
    }

    @Override
    public void unlockNewWallet(User user) {

        boolean isEligibleToUnlock = WalletUtils.isEligibleToUnlockNewWallet(user);

        if (!isEligibleToUnlock) {
            throw new RuntimeException("This user reached the max number of allowed wallets");
        }

        Wallet newWallet = new Wallet(
                user.getWallets().size() == 1 ? SECOND_WALLET_NICKNAME : THIRD_WALLET_NICKNAME,
                user,
                WalletStatus.ACTIVE,
                BigDecimal.valueOf(0),
                Currency.getInstance("EUR"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false
        );


        this.walletRepository.save(newWallet);
    }

    @Override
    public Map<UUID, List<Transaction>> getLastFourTransactions(List<Wallet> wallets) {

        Map<UUID, List<Transaction>> transactionsByWalletId = new HashMap<>();

        for (Wallet wallet : wallets) {

            List<Transaction> lastFourTransactions = this.transactionService.getLastFourTransactions(wallet);

            transactionsByWalletId.put(wallet.getId(), lastFourTransactions);
        }

        return transactionsByWalletId;
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
                FIRST_WALLET_NICKNAME,
                user,
                WalletStatus.ACTIVE,
                BigDecimal.valueOf(20.00),
                Currency.getInstance("EUR"),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );
    }


}
