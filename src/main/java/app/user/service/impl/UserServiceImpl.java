package app.user.service.impl;

import app.exception.DomainException;
import app.notification.service.NotificationService;
import app.security.UserData;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.user.enums.UserRole;
import app.user.model.User;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.user.service.UserService;

import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import app.web.dto.LoginRequest;
import app.web.dto.ProfileEditRequest;
import app.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final UserProperties userProperties;



    @Autowired
    public UserServiceImpl(UserRepository userRepository, WalletService walletService, SubscriptionService subscriptionService, NotificationService notificationService, PasswordEncoder passwordEncoder, UserProperties userProperties) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
        this.userProperties = userProperties;
    }


    @Override
    public User getDefaultUser() {
        return getByUsername(this.userProperties.getDefaultUser().getUsername());
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User registerUser(RegisterRequest request) {

        // Check if user with such username exists
        if (this.userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DomainException("Username '%s' already exists.".formatted(request.getUsername()));
        }

        // Create an account
        User user = initUser(request, passwordEncoder);

        // Save the user
        userRepository.save(user);

        // Create Wallet
        Wallet wallet = walletService.createNewWallet(user);

        // Assign a free subscription to the user upon registration
        Subscription defaultSubscription = subscriptionService.createDefaultSubscription(user);

        user.setWallets(List.of(wallet));
        user.setSubscriptions(List.of(defaultSubscription));

        // Log info about registration
        log.info("Successfully created new user account for username [%s] and id [%s]"
                .formatted(user.getUsername(), user.getId()));

        this.notificationService.upsertPreference(user.getId(), false, null);

        return user;
    }

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findByUsername(username).get();
    }

    @Override
    @Cacheable("users")
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User getById(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new DomainException("User with id ['%s'] not found".formatted(id)));
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID id) {
        User user = getById(id);

        user.setActive(!user.isActive());

        user.setUpdatedOn(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID id) {
        User user = getById(id);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        user.setUpdatedOn(LocalDateTime.now());

        this.userRepository.save(user);
    }

    @Override
    public long countActiveUsers() {
        return this.getAllUsers()
                .stream()
                .filter(User::isActive)
                .count();
    }

    @Override
    public long countInactiveUsers() {
        return this.getAllUsers()
                .stream()
                .filter(user -> !user.isActive())
                .count();
    }

    @Override
    public long countAdminUsers() {
        return this.getAllUsers()
                .stream()
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .count();
    }

    @Override
    public long countNonAdminUsers() {
        return this.getAllUsers()
                .stream()
                .filter(user -> user.getRole() == UserRole.USER)
                .count();
    }

    @Override
    public long countUsersWithWallets(int countWallets) {
        return this.userRepository.countByCountWallet(countWallets);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void updateProfile(User user, ProfileEditRequest profileEditRequest) {

        user.setFirstName(profileEditRequest.getFirstName());
        user.setLastName(profileEditRequest.getLastName());
        user.setEmail(profileEditRequest.getEmailAddress());
        user.setProfilePicture(profileEditRequest.getProfilePictureUrl());
        user.setUpdatedOn(LocalDateTime.now());

        this.userRepository.save(user);
    }


    private User initUser(RegisterRequest request, PasswordEncoder passwordEncoder) {
        return new User (
                            request.getUsername(),
                            passwordEncoder.encode(request.getPassword()),
                            request.getCountry()
        );
    }

    // Всеки път при логин операция, Spring Security ще извиква този метод
    // Това потребителско име може да е телефон, имейл и т.н , но трябва да се конфигурира в SecurityFilterChain
    // Цел на метода: да кажа на Spring Security кой е потребителя зад това потребителско име
    // Return type: методът очаква да върне UserDetails обект, който има данни на този потребител
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());
    }
}
