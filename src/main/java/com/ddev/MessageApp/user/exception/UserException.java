package com.ddev.MessageApp.user.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException{
    private int code;
    public UserException(String message, int code){
        super(message);
        this.code = code;
    }
}
