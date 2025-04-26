package com.example.application.config;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Tarkistetaan rooli

        String role = authentication.getAuthorities().toString();
        if (role.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin");  // Ohjaa admin-sivulle
        } else if (role.contains("ROLE_USER")) {
            response.sendRedirect("/user");  // Ohjaa user-sivulle
        } else {
            response.sendRedirect("/home");  // Ohjaa pääsivulle
        }
    }
}
