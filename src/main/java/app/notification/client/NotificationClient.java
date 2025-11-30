package app.notification.client;

import app.notification.client.dto.UpsertPreferenceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-svc", url = "http://localhost:8081/api/v1")
public interface NotificationClient {

    @PostMapping("/notification-preferences")
    ResponseEntity<Void> upsertPreference(@RequestBody UpsertPreferenceRequest upsertPreferenceRequest);

}