package app.web.controller;

import app.exception.RetryFailedNotificationsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RetryFailedNotificationsException.class)
    public String handleRetryFailedNotificationsException(RetryFailedNotificationsException e,
                                                          RedirectAttributes attributes) {
        attributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/notifications";
    }

}
