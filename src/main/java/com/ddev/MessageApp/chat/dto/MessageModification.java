package com.ddev.MessageApp.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class MessageModification {
    private String message;
    private Integer conversationId;
    private UUID messageId;
    private MessageModificationType type;
}
