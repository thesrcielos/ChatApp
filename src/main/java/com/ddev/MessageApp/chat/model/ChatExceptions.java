package com.ddev.MessageApp.chat.model;

public class ChatExceptions  extends RuntimeException {
        public static final String MESSAGE_NOT_FOUND="THE MESSAGE WAS NOT FOUND";
        public static final String CONVERSATION_NOT_FOUND ="THE CONVERSATION WAS NOT FOUND";
        public static final String CONTACT_NOT_EXIST ="THE CONTACT WAS NOT FOUND";
        public int code;
        public ChatExceptions(String message, int code) {
            super(message);
            this.code = code;
        }
}


