package app.transaction.service;

import app.transaction.enums.TransactionStatus;
import app.transaction.enums.TransactionType;
import app.transaction.model.Transaction;
import app.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    Transaction createNewTransaction(User owner, String sender, String receiver, BigDecimal amount, BigDecimal balanceLeft, Currency currency, TransactionType type, TransactionStatus status, String description, String failedReason);

    List<Transaction> getAllTransactionsByUserId(UUID owner);

    long countTotalTransactions();

    BigDecimal countTotalAmount();

    long countTotalDepositTransactions();
    long countTotalWithdrawalTransactions();
    long countTotalSucceededTransactions();
    long countTotalFailedTransactions();

    Transaction getById(UUID id);
}
