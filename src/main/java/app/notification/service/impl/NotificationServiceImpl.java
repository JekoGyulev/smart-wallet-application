package app.notification.service.impl;

import app.notification.client.NotificationClient;
import app.notification.client.dto.UpsertPreferenceRequest;
import app.notification.service.NotificationService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
































}
