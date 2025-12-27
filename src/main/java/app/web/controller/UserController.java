package app.web.controller;

import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.DtoMapper;
import app.web.dto.ProfileEditRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getUsersPage() {
        List<User> users = this.userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }


    @GetMapping("/{id}/profile")
    public ModelAndView getUserProfile(@PathVariable UUID id) {

        User user = this.userService.getById(id);

        ProfileEditRequest profileEditRequest = DtoMapper.fromUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-menu");
        modelAndView.addObject("user", user);
        modelAndView.addObject("profileEditRequest", profileEditRequest);

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(@PathVariable UUID id,
                                    @Valid ProfileEditRequest profileEditRequest,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("profile-menu");
            modelAndView.addObject("user", this.userService.getById(id));
            return modelAndView;
        }

        this.userService.updateProfile(id, profileEditRequest);

        return new ModelAndView("redirect:/home");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchStatus(@PathVariable UUID id) {
        this.userService.switchStatus(id);
        return "redirect:/users";
    }


    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchRole(@PathVariable UUID id) {
        this.userService.switchRole(id);
        return "redirect:/users";
    }

}
