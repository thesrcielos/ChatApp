package com.ddev.MessageApp.user.service;

import com.ddev.MessageApp.user.dto.LoginDTO;
import com.ddev.MessageApp.user.dto.RegisterDTO;
import com.ddev.MessageApp.auth.dto.TokenDTO;
import com.ddev.MessageApp.user.dto.UserDTO;
import com.ddev.MessageApp.auth.jwt.JwtUtil;
import com.ddev.MessageApp.user.model.Role;
import com.ddev.MessageApp.user.model.UserEntity;
import com.ddev.MessageApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final JwtUtil jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public List<UserEntity> getUsers() { return userRepository.findAll(); }

    @Override
    public UserDTO getUser(Integer id) {
        return findUser(id).toDTO();
    }

    @Override
    public TokenDTO createUser(RegisterDTO registerDTO) {
        // Should check if the user already exist.
        if(!verifyEmail(registerDTO.getEmail())){
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

    @Override
    public TokenDTO login(LoginDTO loginDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        UserEntity user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(()->new UsernameNotFoundException("The user with email not found." + loginDTO.getEmail()));
        return TokenDTO.builder().token(jwtService.getToken(user.getUsername())).build();
    }

    @Override
    public void changePassword(Integer id, String password) {
        if(notValidatePassword(password)) throw new RuntimeException("invalid password");
        UserEntity user = findUser(id);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public void changeName(Integer id, String name) {
        UserEntity user = findUser(id);
        user.setName(name);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        UserEntity user = findUser(id);
        if(user.getRole().toString().equals("ADMIN")){
            throw new RuntimeException("no delete admin");
        }
        userRepository.deleteById(id);
    }
    private boolean verifyEmail(String email){
        return userRepository.findByEmail(email).isEmpty();
    }
    private UserEntity findUser(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not exists"));
    }
    private boolean notValidatePassword(String password){
           return password==null||!password.matches(".*[a-z].*")||!password.matches(".*[A-Z].*")
               ||!password.matches(".*\\d.*")|| !password.matches(".*[@$!%*?&#].*")||
                   password.length() < 8;
    }

}
