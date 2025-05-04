package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionsController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex){
        System.out.println("ex.getMessage() = " + ex.getMessage());
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleHttpRequestMethodNorSupported(MissingServletRequestParameterException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDetails> handleNotRequestParameter(MissingServletRequestParameterException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentials(BadCredentialsException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(NoResourceFoundException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneralException(Exception ex) {
        ErrorDetails errorDetails = ErrorDetails
                .builder()
                .message(ex.getMessage())
                .date(LocalDateTime.now())
                .build();
        ex.printStackTrace();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}