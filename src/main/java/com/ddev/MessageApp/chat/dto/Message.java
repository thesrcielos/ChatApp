package com.ddev.MessageApp.chat.dto;

import com.ddev.MessageApp.chat.model.FileType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    @Size(max = 1000, message = "Message size must be less or equal than 1000")
    private String message;
    private Integer conversationId;
    private Integer contactId;
    @NotNull(message = "Sent Date must not be null")
    private LocalDateTime sentAt;
    private FileType fileType;
    private String fileUrl;
}
