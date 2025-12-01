package app.notification.client;

import app.notification.client.dto.CreateNotificationRequest;
import app.notification.client.dto.NotificationPreferenceResponse;
import app.notification.client.dto.NotificationResponse;
import app.notification.client.dto.UpsertPreferenceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "notification-svc", url = "http://localhost:8081/api/v1")
public interface NotificationClient {

    @PostMapping("/notification-preferences")
    ResponseEntity<Void> upsertPreference(@RequestBody UpsertPreferenceRequest upsertPreferenceRequest);

    @GetMapping("/notification-preferences")
    ResponseEntity<NotificationPreferenceResponse> getPreferenceByUserId(@RequestParam("userId") UUID userId);

    @GetMapping("/notifications")
    ResponseEntity<List<NotificationResponse>> getNotificationHistoryForUser(@RequestParam("user_id") UUID userId);

    @PostMapping("/notifications")
    ResponseEntity<Void> sendNotification(@RequestBody CreateNotificationRequest createNotificationRequest);











}