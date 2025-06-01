package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.dto.*;
import com.ddev.MessageApp.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;
    @MessageMapping("/send")
    public Message getMessage(@Payload Message message, Principal principal){
        chatService.saveMessage(message);
        return message;
    }

    @MessageMapping("/seen")
    public void markLastSeenMessage(MessageSeenDTO messageSeenDTO) {
        chatService.markLastMessageSeen(messageSeenDTO);
    }

    @MessageMapping("/edit-message")
    public MessageEditResponse editMessage(EditMessageDTO message){
        return chatService.editMessage(message);
    }

    @MessageMapping("/delete-message")
    public void deleteMessage(DeleteMessageDTO deleteMessageDTO) {
        chatService.deleteMessage(deleteMessageDTO);
    }
}
