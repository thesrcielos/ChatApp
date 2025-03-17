package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.dto.Message;
import com.ddev.MessageApp.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    @MessageMapping("/send")
    @SendTo("/topic/conversation")
    public Message getMessage(Message message){
        System.out.println("message = " + message);
        //chatService.saveMessage(message);
        return message;
    }

}
