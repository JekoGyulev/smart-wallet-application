package app.web.controller;

import app.notification.client.dto.NotificationPreferenceResponse;
import app.notification.client.dto.NotificationResponse;
import app.notification.service.NotificationService;
import app.security.UserData;
import app.utility.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ModelAndView getNotificationsPage(@AuthenticationPrincipal UserData userData) {

        NotificationPreferenceResponse response = this.notificationService.getPreferenceByUserId(userData.getId());
        List<NotificationResponse> userEmails = this.notificationService.getLastNotificationsForUser(userData.getId());


        ModelAndView modelAndView = new ModelAndView("notifications");

        modelAndView.addObject("userNotificationPreference", response);
        modelAndView.addObject("lastEmails", userEmails);
        modelAndView.addObject("succeededEmails", EmailUtils.countSucceededEmails(userEmails));
        modelAndView.addObject("failedEmails", EmailUtils.countFailedEmails(userEmails));


        return modelAndView;
    }
}
