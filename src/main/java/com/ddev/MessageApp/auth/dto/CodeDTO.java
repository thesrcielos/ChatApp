package com.ddev.MessageApp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeDTO {

    @NotBlank(message = "Authorization code must not be blank")
    @Size(min = 10, max = 256, message = "Authorization code must be between 10 and 256 characters long")
    private String code;
}
