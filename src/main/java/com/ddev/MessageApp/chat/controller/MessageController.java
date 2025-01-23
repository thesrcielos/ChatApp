package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.Message;
import com.ddev.MessageApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final UserRepository userRepository;
    @MessageMapping("/send")
    @SendTo("/topic/conversation")
    public Message getMessage(Message message){
        return new Message("message was " + message.getBody(), 1, 1);
    }
}
