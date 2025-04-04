package com.ddev.MessageApp.chat.dto;

import com.ddev.MessageApp.chat.model.FileType;
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
    private LocalDateTime sentAt;
    private FileType fileType;
    private String fileUrl;
}
