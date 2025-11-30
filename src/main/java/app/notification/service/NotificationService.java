package app.notification.service;

import java.util.UUID;

public interface NotificationService {

    void upsertPreference(UUID userId, boolean isNotificationEnabled, String contactInfo);
}
