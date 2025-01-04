package com.ddev.MessageApp.user.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${SECRET_KEY}")
    private String secretKey;

    public String generateJwtToken(Authentication authentication) {
        long expirationTime = 3600000; // 1 hora en milisegundos

        return Jwts.builder()
                .setSubject(authentication.getName()) // El nombre del usuario
                .claim("authorities", authentication.getAuthorities()) // Roles/autoridades
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
