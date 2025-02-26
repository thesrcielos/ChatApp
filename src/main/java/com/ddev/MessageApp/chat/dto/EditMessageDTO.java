package com.ddev.MessageApp.chat.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class EditMessageDTO {
    private UUID id;
    private String message;
}
