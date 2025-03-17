package com.ddev.MessageApp.chat;

import com.ddev.MessageApp.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> authorization = request.getHeaders().get("Authorization");

        if (authorization == null || authorization.isEmpty() || !authorization.get(0).startsWith("Bearer ")) {
            System.out.println("❌ Handshake rechazado: sin token");
            return false;
        }

        String token = authorization.get(0).substring(7);

        // Validar el token JWT
        Authentication authentication = jwtUtil.validateAndAuthenticate(token, null);
        if (authentication == null) {
            System.out.println("❌ Handshake rechazado: token inválido");
            return false;
        }

        attributes.put("user", authentication);

        System.out.println("✅ Handshake exitoso: usuario autenticado");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

    }
}
