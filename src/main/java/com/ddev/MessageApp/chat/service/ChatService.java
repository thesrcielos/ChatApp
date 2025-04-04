package com.ddev.MessageApp.chat.service;

import com.ddev.MessageApp.chat.dto.Message;
import com.ddev.MessageApp.chat.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ChatService {
    void deleteMessage(UUID id);
    MessageEditResponse editMessage(EditMessageDTO editMessageDTO);
    PaginatedListObject<MessageResponse> getChatMessages(Integer id, int page, int size);
    PaginatedListObject<GroupMessage> getGroupMessages(Integer id, int page, int size);
    PaginatedListObject<ChatDTO> getUserChats(Integer id, int page, int size);
    PaginatedListObject<ChatDTO> getUserContactsByPattern(Integer id, String pattern, int page, int size);
    MessageResponse saveMessage(Message message);
    ChatDTO createGroup(GroupRequest groupRequest);
}
