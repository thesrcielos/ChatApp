package com.ddev.MessageApp.user.controller;

import com.ddev.MessageApp.user.dto.LoginDTO;
import com.ddev.MessageApp.user.dto.RegisterDTO;
import com.ddev.MessageApp.user.dto.TokenDTO;
import com.ddev.MessageApp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @GetMapping("/getInfo")
    public Map<String, Object> userInfo(OAuth2AuthenticationToken authentication) {
        return authentication.getPrincipal().getAttributes();
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenDTO loginUser(@RequestBody LoginDTO loginDTO){
        return userService.login(loginDTO);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDTO registerUser(@RequestBody RegisterDTO registerDTO){
        return userService.createUser(registerDTO);
    }
}
