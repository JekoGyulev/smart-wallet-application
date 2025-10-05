package app.web.controller;

import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    private final UserService userService;

    @Autowired
    public TransferController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ModelAndView getTransferPage() {

        User user = userService.getDefaultUser();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transfer");
        modelAndView.addObject("transferRequest", new TransferRequest());
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
