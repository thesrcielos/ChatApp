package com.ddev.MessageApp.auth.controller;

import com.ddev.MessageApp.auth.dto.CodeDTO;
import com.ddev.MessageApp.auth.service.AuthService;
import com.ddev.MessageApp.auth.dto.TokenDTO;
import com.ddev.MessageApp.user.dto.LoginDTO;
import com.ddev.MessageApp.user.dto.RegisterDTO;
import com.ddev.MessageApp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/token")
    public ResponseEntity<TokenDTO> getToken(@RequestBody CodeDTO codeDTO){
        return ResponseEntity.ok(authService.processGoogleAuthCode(codeDTO.getCode()));
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenDTO loginUser(@RequestBody LoginDTO loginDTO){
        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDTO registerUser(@RequestBody RegisterDTO registerDTO){
        return authService.createUser(registerDTO);
    }
}
