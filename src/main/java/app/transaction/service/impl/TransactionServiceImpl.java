package app.transaction.service.impl;

import app.transaction.enums.TransactionStatus;
import app.transaction.enums.TransactionType;
import app.transaction.model.Transaction;
import app.transaction.repository.TransactionRepository;
import app.transaction.service.TransactionService;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    @Override
    public Transaction createNewTransaction(User owner, String sender, String receiver, BigDecimal amount, BigDecimal balanceLeft, Currency currency, TransactionType type, TransactionStatus status, String description, String failedReason) {
        Transaction transaction = new Transaction(owner, sender, receiver, amount, balanceLeft, currency, type, status, description, failedReason, LocalDateTime.now());

        return this.transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAllTransactionsByUserId(UUID id) {
        return this.transactionRepository.findAllByOwnerIdOrderByCreatedOnDesc(id);
    }

    @Override
    public long countTotalTransactions() {
        return this.transactionRepository.count();
    }

    @Override
    public BigDecimal countTotalAmount() {
        return this.transactionRepository.findAll()
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public long countTotalDepositTransactions() {
        return this.transactionRepository.findAll()
                .stream()
                .filter(transaction -> transaction.getType() == TransactionType.DEPOSIT)
                .count();
    }

    @Override
    public long countTotalWithdrawalTransactions() {
        return this.transactionRepository.findAll()
                .stream()
                .filter(transaction -> transaction.getType() == TransactionType.WITHDRAWAL)
                .count();
    }

    @Override
    public long countTotalSucceededTransactions() {
        return this.transactionRepository.findAll()
                .stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.SUCCEEDED)
                .count();
    }

    @Override
    public long countTotalFailedTransactions() {
        return this.transactionRepository.findAll()
                .stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.FAILED)
                .count();
    }
}
