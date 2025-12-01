package app.notification.service;

import app.notification.client.dto.NotificationPreferenceResponse;
import app.notification.client.dto.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void upsertPreference(UUID userId, boolean isNotificationEnabled, String contactInfo);

    NotificationPreferenceResponse  getPreferenceByUserId(UUID userId);

    List<NotificationResponse> getLastNotificationsForUser(UUID userId);
}
