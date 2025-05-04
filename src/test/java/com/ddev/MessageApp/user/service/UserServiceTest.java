package com.ddev.MessageApp.user.service;

import com.ddev.MessageApp.auth.dto.TokenDTO;
import com.ddev.MessageApp.auth.jwt.JwtUtil;
import com.ddev.MessageApp.user.dto.RegisterDTO;
import com.ddev.MessageApp.user.dto.UserDTO;
import com.ddev.MessageApp.user.model.Role;
import com.ddev.MessageApp.user.model.UserEntity;
import com.ddev.MessageApp.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder ;

    @Test
    public void getUserTest() {
        Integer id = 1;
        String name = "diego";
        String email = "diego@gmail.com";
        Optional<UserEntity> entity = Optional.of(new UserEntity(id, name, ",,", email, Role.USER));

        UserDTO dto = new UserDTO(id, name, email);
        when(userRepository.findById(id)).thenReturn(entity);

        UserDTO result = userService.getUser(id);

        assertEquals(dto, result);
        verify(userRepository).findById(id);
    }

    @Test
    public void getNonExistentUserTest() {
        Integer id = 1;
        Optional<UserEntity> entity = Optional.empty();

        when(userRepository.findById(id)).thenReturn(entity);

        assertThrows(RuntimeException.class, () -> userService.getUser(id),
                "User not exists");
        verify(userRepository).findById(any(Integer.class));
    }

    @Test
    public void createUser() {
        RegisterDTO registerDTO = new RegisterDTO("test-user", "test-password","test@test.com");
        String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlRlc3QiLCJpYXQiOjE1MTYyMzkwMjJ9.r2tIfSQyjfh-s0S3IXibZ5ftEeqK7_KfkXPuPBkfFm8";
        String passwordEncoded = "PasswordEncoded";

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn(passwordEncoded);
        when(jwtUtil.getToken(any(String.class))).thenReturn(exampleToken);

        UserEntity user = new UserEntity(
                1,
                registerDTO.getName(),
                registerDTO.getPassword(),
                registerDTO.getEmail(), Role.USER);
        when(userRepository.save(user)).thenReturn(user);

        TokenDTO response = userService.createUser(registerDTO);

        assertEquals(response.getToken(), exampleToken);
        verify(userRepository).save(any(UserEntity.class));
        verify(jwtUtil).getToken(any(String.class));
        verify(passwordEncoder).encode("test-password");

        verify(userRepository).save(argThat(userSaved -> userSaved.getPassword().equals(passwordEncoded)));
    }

}
