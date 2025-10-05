package app.web.controller;

import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.user.property.UserProperties;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final UserService userService;
    private final UserProperties userProperties;

    @Autowired
    public SubscriptionController(UserService userService, UserProperties userProperties) {
        this.userService = userService;
        this.userProperties = userProperties;
    }


    @GetMapping
    public ModelAndView getSubscriptionsPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upgrade");
        return modelAndView;
    }

    @GetMapping("/history")
    public ModelAndView getSubscriptionHistoryPage() {
        // This will be changed in the future (no hardcoding)
        User user = this.userService.getByUsername(this.userProperties.getDefaultUser().getUsername());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("subscription-history");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

}
