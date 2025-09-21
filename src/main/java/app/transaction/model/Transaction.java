package app.transaction.model;

import app.transaction.enums.TransactionStatus;
import app.transaction.enums.TransactionType;
import app.user.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    private User owner;
    // Identifier of the wallet from which we send the money
    @Column(nullable = false)
    private String sender;
    // Identifier of the wallet to which we give the money
    @Column(nullable = false)
    private String receiver;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "balance_left", nullable = false)
    private BigDecimal balanceLeft;
    @Column(nullable = false)
    private Currency currency;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    @Column(nullable = false)
    private String description;
    @Column(name = "failure_reason")
    private String failureReason;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    public Transaction() {}

    public Transaction(User owner, String sender, String receiver, BigDecimal amount, BigDecimal balanceLeft, Currency currency, TransactionType type, TransactionStatus status, String description, String failureReason, LocalDateTime createdOn) {
        this.owner = owner;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.balanceLeft = balanceLeft;
        this.currency = currency;
        this.type = type;
        this.status = status;
        this.description = description;
        this.failureReason = failureReason;
        this.createdOn = createdOn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceLeft() {
        return balanceLeft;
    }

    public void setBalanceLeft(BigDecimal balanceLeft) {
        this.balanceLeft = balanceLeft;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
