package com.ddev.MessageApp.chat.model;

public class UserExceptions extends RuntimeException {
    public static final String CONTACT_NOT_EXIST ="THE CONTACT WAS NOT FOUND";
    public int code;
    public UserExceptions(String message, int code) {
        super(message);
        this.code = code;
    }
}

