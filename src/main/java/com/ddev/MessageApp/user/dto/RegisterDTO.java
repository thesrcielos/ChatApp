package com.ddev.MessageApp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a User register, by providing the information to create a user.
 * @version 1.0
 * @since 28-10-2024
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterDTO {
    private String name;
    private String password;
    private String email;
}
