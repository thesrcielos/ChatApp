package com.ddev.MessageApp.auth.service;

import com.ddev.MessageApp.auth.jwt.JwtUtil;
import com.ddev.MessageApp.auth.dto.GoogleTokenResponse;
import com.ddev.MessageApp.auth.dto.GoogleUserInfo;
import com.ddev.MessageApp.auth.dto.TokenDTO;
import com.ddev.MessageApp.user.dto.LoginDTO;
import com.ddev.MessageApp.user.dto.RegisterDTO;
import com.ddev.MessageApp.user.model.Role;
import com.ddev.MessageApp.user.model.UserEntity;
import com.ddev.MessageApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public TokenDTO processGoogleAuthCode(String code) {
        // 1. Intercambiar el código por tokens de Google
        GoogleTokenResponse googleTokens = exchangeCodeForTokens(code);

        // 2. Obtener información del usuario usando el access token
        GoogleUserInfo userInfo = getUserInfo(googleTokens.getAccess_token());

        // 3. Crear o actualizar usuario en tu base de datos
        UserEntity user = findOrCreateUser(userInfo);

        // 4. Generar tu propio JWT token
        String jwtToken = jwtUtil.getToken(user.getEmail());

        return new TokenDTO(jwtToken);
    }

    private GoogleTokenResponse exchangeCodeForTokens(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    GoogleTokenResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new OAuth2AuthenticationException("Error exchanging code for tokens: " + e.getMessage());
        }
    }

    private GoogleUserInfo getUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    GoogleUserInfo.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new OAuth2AuthenticationException("Error getting user info: " + e.getMessage());
        }
    }

    private UserEntity findOrCreateUser(GoogleUserInfo userInfo) {
        return userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(userInfo.getEmail());
                    newUser.setName(userInfo.getName());
                    //newUser.setPicture(userInfo.getPicture());
                    //newUser.setProvider(AuthProvider.GOOGLE);
                    newUser.setRole(Role.USER);
                    return userRepository.save(newUser);
                });
    }

    public TokenDTO createUser(RegisterDTO registerDTO) {
        // Should check if the user already exist.
        if(userRepository.existsByEmail(registerDTO.getEmail())){
            throw new RuntimeException("Email in use");
        }
        UserEntity user = new UserEntity(
                null,
                registerDTO.getName(),
                passwordEncoder.encode(registerDTO.getPassword()),
                registerDTO.getEmail(), Role.USER);
        userRepository.save(user);
        return TokenDTO.builder().token(jwtService.getToken(user.getUsername())).build();
    }

    public TokenDTO login(LoginDTO loginDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        UserEntity user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(()->new UsernameNotFoundException("The user with email not found." + loginDTO.getEmail()));
        return TokenDTO.builder().token(jwtService.getToken(user.getUsername())).build();
    }
}
