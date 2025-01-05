package com.ddev.MessageApp.user.config;

import com.ddev.MessageApp.user.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = getEmail(authentication);
        String token = jwtUtil.getToken(email);
        // Devolver el JWT al cliente
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }

    private String getEmail(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            // Extrae el correo electrónico desde los atributos
            return oAuth2User.getAttribute("email");
        }
        return null;
    }
}
