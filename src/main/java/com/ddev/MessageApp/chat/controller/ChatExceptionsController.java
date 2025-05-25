package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.dto.ErrorDetails;
import com.ddev.MessageApp.chat.model.ChatExceptions;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ChatExceptionsController {
    @ExceptionHandler(ChatExceptions.class)
    public ResponseEntity<ErrorDetails> handleChatPrivateExceptions(ChatExceptions ex){
        ErrorDetails errorDetails = ErrorDetails
                .builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatusCode.valueOf(ex.code));
    }
}
