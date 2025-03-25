package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.dto.AudioMessageDTO;
import com.ddev.MessageApp.chat.dto.AudioMessageResponse;
import com.ddev.MessageApp.chat.dto.Message;
import com.ddev.MessageApp.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/send")
    public Message getMessage(@Payload Message message, Principal principal){
        String recipient = principal.getName();
        System.out.println("message = " + message);
        chatService.saveMessage(message);
        return message;
    }

}
