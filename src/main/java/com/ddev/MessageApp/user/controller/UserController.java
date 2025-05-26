package com.ddev.MessageApp.user.controller;

import com.ddev.MessageApp.user.dto.LoginDTO;
import com.ddev.MessageApp.user.dto.RegisterDTO;
import com.ddev.MessageApp.auth.dto.TokenDTO;
import com.ddev.MessageApp.user.dto.UserDTO;
import com.ddev.MessageApp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.control.MappingControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> userInfo(@PathVariable Integer id)
    {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
