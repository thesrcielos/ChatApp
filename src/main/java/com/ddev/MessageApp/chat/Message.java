package com.ddev.MessageApp.chat;


public class Message {
    private String body;
    public String getBody(){
        return body;
    }

    public Message(){}
    public Message(String body){
        this.body = body;
    }
}
