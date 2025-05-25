package com.ddev.MessageApp.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ErrorDetails {
    private String message;
    private LocalDateTime date;
}
