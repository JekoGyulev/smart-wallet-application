package app.subscription.model;

import app.subscription.enums.SubscriptionPeriod;
import app.subscription.enums.SubscriptionStatus;
import app.subscription.enums.SubscriptionType;
import app.user.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    private User owner;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPeriod period;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionType type;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(name = "renewal_allowed", nullable = false)
    private boolean renewalAllowed;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "completed_on", nullable = false)
    private LocalDateTime completedOn;

    public Subscription() {}

    public Subscription(User owner, SubscriptionStatus status, SubscriptionPeriod period, SubscriptionType type, BigDecimal price, boolean renewalAllowed, LocalDateTime createdOn, LocalDateTime completedOn) {
        this.owner = owner;
        this.status = status;
        this.period = period;
        this.type = type;
        this.price = price;
        this.renewalAllowed = renewalAllowed;
        this.createdOn = createdOn;
        this.completedOn = completedOn;
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

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public SubscriptionPeriod getPeriod() {
        return period;
    }

    public void setPeriod(SubscriptionPeriod period) {
        this.period = period;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setType(SubscriptionType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isRenewalAllowed() {
        return renewalAllowed;
    }

    public void setRenewalAllowed(boolean renewalAllowed) {
        this.renewalAllowed = renewalAllowed;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(LocalDateTime completedOn) {
        this.completedOn = completedOn;
    }
}
