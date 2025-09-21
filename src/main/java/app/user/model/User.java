package app.user.model;

import app.subscription.model.Subscription;
import app.user.enums.Country;
import app.user.enums.UserRole;
import app.wallet.model.Wallet;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "profile_picture")
    private String profilePicture;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Country country;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn;

    // Subscriptions
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    @OrderBy("createdOn DESC")
    private List<Subscription> subscriptions;
    // Wallets
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    @OrderBy("createdOn ASC")
    private List<Wallet> wallets;


    public User() {
        this.subscriptions = new ArrayList<>();
        this.wallets = new ArrayList<>();
    }



    public User(String username, String firstName, String lastName, String profilePicture, String email, String password, UserRole role, Country country, boolean isActive, LocalDateTime createdOn, LocalDateTime updatedOn, List<Subscription> subscriptions, List<Wallet> wallets) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.email = email;
        this.password = password;
        this.role = role;
        this.country = country;
        this.isActive = isActive;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.subscriptions = subscriptions;
        this.wallets = wallets;
    }

    // Used for registering users
    public User(String username, String password, Country country) {
        this.username = username;
        this.password = password;
        this.country = country;
        this.isActive = true;
        this.role = UserRole.USER;
        this.createdOn = LocalDateTime.now();
        this.updatedOn = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Wallet> getWallets() {
        return wallets;
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
    }
}
