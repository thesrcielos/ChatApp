package com.ddev.MessageApp.chat.dto;

import com.ddev.MessageApp.chat.model.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;
    private Integer conversationId;
    private UUID messageId;
    private Integer userId;
    private FileType fileType;
    private String fileUrl;
    private LocalDateTime sentAt;
}
