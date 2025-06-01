package com.ddev.MessageApp.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteMessageDTO {
    private UUID id;
    private Integer userId;
}
