package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.dto.*;
import com.ddev.MessageApp.chat.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/users/{id}")
    public ResponseEntity<PaginatedListObject<ChatDTO>> getUserChats(@PathVariable Integer id,
                                                                     @RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(chatService.getUserChats(id, page, size));
    }

    @DeleteMapping("/conversation/{id}/message")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMessage(@PathVariable UUID id){
        chatService.deleteMessage(id);
    }

    @PutMapping("/conversation/message")
    @ResponseStatus(HttpStatus.OK)
    public void editMessage(@RequestBody EditMessageDTO editMessageDTO){
        chatService.editMessage(editMessageDTO);
    }

    @GetMapping("/conversation/{id}/messages")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedListObject<MessageResponse> getChatMessages(@PathVariable Integer id, int page, int size){
        return chatService.getChatMessages(id,page,size);
    }

    @GetMapping("/users/{id}/contacts")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedListObject<ChatDTO> getContactsByPattern(@PathVariable Integer id, @RequestParam String pattern,
                                                             @RequestParam Integer page, @RequestParam Integer size) {
        return chatService.getUserContactsByPattern(id, pattern, page, size);
    }

    @GetMapping("/group/conversation/{id}/messages")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedListObject<GroupMessage>  getGroupMessages(@PathVariable Integer id, int page, int size){
        return chatService.getGroupMessages(id,page,size);
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse saveMessage(@RequestBody Message message){
        return chatService.saveMessage(message);
    }
}
