package com.ddev.MessageApp.chat.dto;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteMessageDTO {
    @NotNull(message = "Message ID cannot be null")
    private UUID id;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    private Integer userId;
}
