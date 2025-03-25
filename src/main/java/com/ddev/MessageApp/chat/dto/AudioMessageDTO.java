package com.ddev.MessageApp.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AudioMessageDTO {
    private Integer conversationId;
    private Integer contactId;
    private LocalDateTime sentAt;
    private String fileType;
    private String content;
}
