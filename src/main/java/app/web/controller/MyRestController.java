package app.web.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/demo")
public class MyRestController {

    @GetMapping
    public String demo(HttpServletRequest request, HttpServletResponse response) {
//        return "Hello " + request.getHeader("author") + " , you are " + request.getHeader("age") +
//        " from " + request.getHeader("city");
//
//        String greetingUser = "Hello " + request.getHeader("author") + " , you are " + request.getHeader("age") +
//        " from " + request.getHeader("city");;
//
//        response.setHeader("Greetings", greetingUser);

        //response.setHeader("Set-Cookie", "userId=123");

        return "Done";
    }

    @GetMapping("/info")
    public String getInfo(HttpServletRequest request, HttpServletResponse response) {

        // Cookies follows format "K=V"
        //response.setHeader("Set-Cookie", "userId=123;age=24;city=Sofia");
        Cookie cookie = new Cookie("userId", UUID.randomUUID().toString());
        cookie.setMaxAge(25);
        response.addCookie(cookie);

        return "Done";
    }




}
