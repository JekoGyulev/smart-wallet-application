package app.user;

import app.exception.DomainException;
import app.exception.UserNotFound;
import app.notification.service.NotificationService;
import app.subscription.service.SubscriptionService;
import app.user.enums.UserRole;
import app.user.model.User;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.user.service.impl.UserServiceImpl;
import app.wallet.service.WalletService;
import app.web.dto.ProfileEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplUTest {

    @Mock
    private  UserRepository userRepository;
    @Mock
    private  WalletService walletService;
    @Mock
    private  SubscriptionService subscriptionService;
    @Mock
    private  NotificationService notificationService;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private  UserProperties userProperties;


    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void whenEditUserDetails_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        //Given
        UUID userId = UUID.randomUUID();
        ProfileEditRequest dto = new ProfileEditRequest("Gosho", "Georgiev", "test@abv.bg", "www.picture.com");

        User user = User.builder()
                .firstName(dto.getFirstName())
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // When and then
        assertThrows(UserNotFound.class, () -> userServiceImpl.updateProfile(userId, dto));
    }


    @Test
    void whenEditUserDetails_andRepositoryReturnsUserFromDatabase_thenUpdateUserDetails_andSaveItToDatabase() {

        // Given
        UUID userId = UUID.randomUUID();
        ProfileEditRequest dto = new ProfileEditRequest("Gosho", "Georgiev", "test@abv.bg", "www.picture.com");

        User userRetrievedFromDatabase =
                User.builder()
                        .id(userId)
                                .firstName("Vik")
                                        .lastName("Aleksandrov")
                                                .profilePicture(null)
                                                        .email("vik@gmail.com")
                                                                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userRetrievedFromDatabase));

        // When
        userServiceImpl.updateProfile(userId, dto);

        // Then
        assertEquals("Gosho", userRetrievedFromDatabase.getFirstName());
        assertEquals("Georgiev", userRetrievedFromDatabase.getLastName());
        assertNotNull(userRetrievedFromDatabase.getProfilePicture());
        assertEquals("www.picture.com", userRetrievedFromDatabase.getProfilePicture());
        assertEquals("test@abv.bg",  userRetrievedFromDatabase.getEmail());

        verify(userRepository).save(userRetrievedFromDatabase);
    }


    @Test
    void whenEditUserDetails_andRepositoryReturnUserAndDtoComesWithNonEmptyEmail_thenInvokeUpsertNotificationPreferenceWithTrue() {

        // Given
        UUID userId = UUID.randomUUID();
        ProfileEditRequest dto = ProfileEditRequest.builder().emailAddress("joro@gmail.com").build();

        User userRetrievedFromDatabase =
                User.builder()
                        .id(userId)
                        .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userRetrievedFromDatabase));

        // When
        userServiceImpl.updateProfile(userId, dto);

        // Then
        verify(notificationService).upsertPreference(userId, true, dto.getEmailAddress());
    }


    @Test
    void whenEditUserDetails_andRepositoryReturnUserAndDtoComesWithEmptyEmail_thenInvokeUpsertNotificationPreferenceWithFalse() {
        // Given
        UUID userId = UUID.randomUUID();
        ProfileEditRequest dto = ProfileEditRequest.builder().build();

        User userRetrievedFromDatabase =
                User.builder()
                        .id(userId)
                        .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userRetrievedFromDatabase));

        // When
        userServiceImpl.updateProfile(userId, dto);


        // Then
        verify(notificationService).upsertPreference(userId, false, dto.getEmailAddress());
    }


    @Test
    void whenSwitchUserRole_AndUserNotFound_thenThrowException() {

        //Given
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // When and Then
        assertThrows(DomainException.class, () -> userServiceImpl.switchRole(userId));
    }

    @Test
    void whenSwitchUserRole_AndUserIsFoundAndHisRoleIsUser_thenSwitchToAdmin() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                        .role(UserRole.USER)
                                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userServiceImpl.switchRole(userId);

        // Then
        assertEquals(UserRole.ADMIN, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchUserRole_AndUserIsFoundAndHisRoleIsUser_thenSwitchToUser() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userServiceImpl.switchRole(userId);

        // Then
        assertEquals(UserRole.USER, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

}
