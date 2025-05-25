package com.ddev.MessageApp.chat.dto;

import com.ddev.MessageApp.user.dto.ContactResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ChatDTO {
    private Integer id;
    private ContactResponse contact;
    private Boolean isGroup;
    private GroupDTO group;
    private Integer unseenMessages;
}
