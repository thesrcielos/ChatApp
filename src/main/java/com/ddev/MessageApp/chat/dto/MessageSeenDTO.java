package com.ddev.MessageApp.chat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageSeenDTO {
    @NotNull(message = "Chat ID cannot be null")
    @Positive(message = "Chat ID must be a positive number")
    private Integer chatId;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    private Integer userId;

    @NotNull(message = "Message ID cannot be null")
    private UUID messageId;
}
