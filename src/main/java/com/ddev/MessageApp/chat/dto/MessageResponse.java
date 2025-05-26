package com.ddev.MessageApp.chat.dto;

import com.ddev.MessageApp.chat.model.FileType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageResponse {
    private String message;
    private Integer conversationId;
    private UUID messageId;
    private Integer userId;
    private FileType fileType;
    private String fileUrl;
    private LocalDateTime sentAt;
}
