package app.web.controller;

import app.subscription.enums.SubscriptionType;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.transaction.model.Transaction;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UpgradeRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final TransactionService transactionService;

    @Autowired
    public SubscriptionController(UserService userService, SubscriptionService subscriptionService, TransactionService transactionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.transactionService = transactionService;
    }


    @GetMapping
    public ModelAndView getSubscriptionsPage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = this.userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upgrade");
        modelAndView.addObject("user", user);
        modelAndView.addObject("upgradeRequest", new UpgradeRequest());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView upgrade(@Valid UpgradeRequest upgradeRequest,
                                 @RequestParam(name="subscriptionType") SubscriptionType subscriptionType,
                                 BindingResult bindingResult,
                                 HttpSession session) {

        User user = this.userService.getById((UUID) session.getAttribute("userId"));


        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("upgrade");
            modelAndView.addObject("user",
                    user);
            return modelAndView;
        }

        Transaction transaction = this.subscriptionService.upgrade(user, upgradeRequest, subscriptionType);

        Subscription activeSubscription = user.getSubscriptions().get(0);

        return new ModelAndView("redirect:/subscriptions/" + activeSubscription.getId() +
                "?transactionId=" + transaction.getId());
    }

    @GetMapping("/{id}")
    public ModelAndView showUpgradeSubscription(@PathVariable UUID id,
                                                @RequestParam(name="transactionId") UUID transactionId) {

        Subscription subscription = this.subscriptionService.getById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upgrade-result");
        modelAndView.addObject("subscription", subscription);
        modelAndView.addObject("transactionId", transactionId);

        return modelAndView;
    }

    @GetMapping("/history")
    public ModelAndView getSubscriptionHistoryPage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = this.userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("subscription-history");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

}
