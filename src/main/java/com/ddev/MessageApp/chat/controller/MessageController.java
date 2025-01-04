package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/send")
    @SendTo("/topic/conversation")
    public Message getMessage(Message message){
        return new Message("message was " + message.getBody());
    }
}
