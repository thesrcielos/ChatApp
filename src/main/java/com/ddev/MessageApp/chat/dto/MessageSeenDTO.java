package com.ddev.MessageApp.chat.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageSeenDTO {
    private Integer chatId;
    private Integer userId;
    private UUID messageId;
}
