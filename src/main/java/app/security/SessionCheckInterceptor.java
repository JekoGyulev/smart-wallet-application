package app.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class SessionCheckInterceptor implements HandlerInterceptor {

    public static final Set<String> UNAUTHENTICATED_ENDPOINTS = Set.of("/login", "/register", "/");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Returns the part of this request's URL that calls the servlet.
        if (UNAUTHENTICATED_ENDPOINTS.contains(request.getServletPath())) {
            return true;
        }

        // if create is false, and the request has no valid HttpSession, this method returns null
        // The idea is to understand if there is or there isn't a session, otherwise it makes a new session if it is create true
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }


        return true;
    }
}
