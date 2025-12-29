package app.notification.service.impl;

import app.exception.RetryFailedNotificationsException;
import app.notification.client.NotificationClient;
import app.notification.client.dto.CreateNotificationRequest;
import app.notification.client.dto.NotificationPreferenceResponse;
import app.notification.client.dto.NotificationResponse;
import app.notification.client.dto.UpsertPreferenceRequest;
import app.notification.service.NotificationService;
import app.web.dto.NotificationPreferenceState;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationClient client;

    @Autowired
    public NotificationServiceImpl(NotificationClient client) {
        this.client = client;
    }


    @Override
    public void upsertPreference(UUID userId, boolean isNotificationEnabled, String contactInfo) {

        UpsertPreferenceRequest request = UpsertPreferenceRequest.builder()
                .userId(userId)
                .isNotificationEnabled(isNotificationEnabled)
                .contactInfo(contactInfo)
                .build();

        try {
            client.upsertPreference(request);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to: {}", e.getMessage());
        }

    }

    @Override
    public NotificationPreferenceResponse getPreferenceByUserId(UUID userId) {
        ResponseEntity<NotificationPreferenceResponse> preferenceByUserId = this.client.getPreferenceByUserId(userId);
        return preferenceByUserId.getBody();
    }

    @Override
    public List<NotificationResponse> getLastNotificationsForUser(UUID userId) {
        ResponseEntity<List<NotificationResponse>> notificationHistoryForUser = this.client.getNotificationHistoryForUser(userId);
        return notificationHistoryForUser.getBody() != null
                ? notificationHistoryForUser.getBody().stream().limit(5).toList()
                : Collections.emptyList();
    }

    @Override
    public void sendEmail(UUID userId, String subject, String body) {

        CreateNotificationRequest createNotificationRequest = CreateNotificationRequest
                .builder()
                .userId(userId)
                .subject(subject)
                .body(body)
                .build();

        try {
            this.client.sendNotification(createNotificationRequest);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to: {}", e.getMessage());
        }
    }

    @Override
    public void updatePreferenceState(NotificationPreferenceState state, UUID id, String email) {

        UpsertPreferenceRequest dto = UpsertPreferenceRequest.builder()
                .userId(id)
                .contactInfo(email)
                .build();


        if (state == NotificationPreferenceState.OFF) {
            dto.setNotificationEnabled(false);
        } else {
            dto.setNotificationEnabled(true);
        }


        try {
            this.client.upsertPreference(dto);
        } catch (FeignException e ) {
            log.error("[S2S Call]: Failed due to: {}", e.getMessage());
        }






    }

    @Override
    public void deleteAllEmails(UUID userId) {
        try {
            this.client.deleteNotifications(userId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to: {}", e.getMessage());
        }
    }

    @Override
    public void retryFailedNotifications(UUID userId) {
        try {
            this.client.retryFailedNotifications(userId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to: {}", e.getMessage());
            throw new RetryFailedNotificationsException("Unfortunately the operation could not succeed. Please try again later...");
        }
    }


}
