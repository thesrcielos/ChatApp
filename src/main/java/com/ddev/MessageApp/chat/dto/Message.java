package com.ddev.MessageApp.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private String content;
    private Integer conversationId;
    private Integer contactId;
    LocalDateTime sentAt;
}
