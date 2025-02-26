package com.ddev.MessageApp.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GroupMessage {
    private UUID id;
    private String message;
    private LocalDateTime sentAt;
    private Integer userId;
    private String userName;
}
