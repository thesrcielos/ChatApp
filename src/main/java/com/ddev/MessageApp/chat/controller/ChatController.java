package com.ddev.MessageApp.chat.controller;

import com.ddev.MessageApp.chat.dto.*;
import com.ddev.MessageApp.chat.service.ChatService;
import com.ddev.MessageApp.user.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/{id}/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getUsersChatsInfo(@PathVariable Integer id) {
        return chatService.getUsersInformation(id);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<PaginatedListObject<ChatDTO>> getUserChats(@PathVariable Integer id,
                                                                     @RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(chatService.getUserChats(id, page, size));
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

    @PostMapping("/group/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatDTO createGroup(@Valid @RequestBody GroupRequest request) {
        return chatService.createGroup(request);
    }

    @GetMapping("/conversation/{id}/messages/before")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedListObject<MessageResponse> getPreviousMessages(
            @PathVariable Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime beforeDate,
            @RequestParam(defaultValue = "15") Integer size) {
        return chatService.getMessagesBefore(id, beforeDate, size);
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse saveMessage(@Valid @RequestBody Message message){
        return chatService.saveMessage(message);
    }
}
