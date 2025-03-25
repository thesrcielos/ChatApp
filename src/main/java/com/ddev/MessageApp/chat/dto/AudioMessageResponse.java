package com.ddev.MessageApp.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AudioMessageResponse {
    private String fileContent;
    private UUID messageId;
    private Integer conversationId;
    private Integer contactId;
    LocalDateTime sentAt;


}
