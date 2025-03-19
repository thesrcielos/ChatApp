package com.ddev.MessageApp.user.service;

import com.ddev.MessageApp.user.dto.LoginDTO;
import com.ddev.MessageApp.user.dto.RegisterDTO;
import com.ddev.MessageApp.auth.dto.TokenDTO;
import com.ddev.MessageApp.user.dto.UserDTO;

public interface UserService {
    TokenDTO createUser(RegisterDTO registerDTO);
    UserDTO getUser(Integer id);
    TokenDTO login(LoginDTO loginDTO);
    void changePassword(Integer id, String password);
    void changeName(Integer id, String name);
    void deleteUser(Integer userId);
    Integer getUserId();
}
