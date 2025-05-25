package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.dto.Message;
import com.ddev.MessageApp.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/send")
    public Message getMessage(@Payload Message message, Principal principal){
        chatService.saveMessage(message);
        return message;
    }

}
