package com.ddev.MessageApp.chat.dto;

import com.ddev.MessageApp.user.dto.ContactResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatDTO {
    private Integer id;
    private ContactResponse contact;
    private boolean isGroup;
    private GroupDTO groupResponse;
}
