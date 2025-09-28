package app.user.service.impl;

import app.exception.DomainException;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.user.enums.UserRole;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.user.service.UserService;

import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, WalletService walletService, SubscriptionService subscriptionService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
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

        return user;
    }

    @Override
    public User loginUser(LoginRequest request) {

        Optional<User> optionalUser = this.userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            throw new DomainException("Username or password is incorrect");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new DomainException("Username or password is incorrect");
        }

        return user;
    }

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findByUsername(username).get();
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User getById(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new DomainException("User with id ['%s'] not found".formatted(id)));
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


    private User initUser(RegisterRequest request, PasswordEncoder passwordEncoder) {
        return new User (
                            request.getUsername(),
                            passwordEncoder.encode(request.getPassword()),
                            request.getCountry()
        );
    }
}
