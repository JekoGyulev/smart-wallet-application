package app.wallet.model;

import app.user.model.User;
import app.wallet.enums.WalletStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String nickname;
    @ManyToOne
    private User owner;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status;
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(nullable = false)
    private Currency currency;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn;
    @Column(name = "is_primary")
    private boolean isPrimary;

    public Wallet() {}

    public Wallet(String nickname, User owner, WalletStatus status, BigDecimal balance, Currency currency, LocalDateTime createdOn, LocalDateTime updatedOn, boolean isPrimary) {
        this.nickname = nickname;
        this.owner = owner;
        this.status = status;
        this.balance = balance;
        this.currency = currency;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.isPrimary = isPrimary;
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

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}
