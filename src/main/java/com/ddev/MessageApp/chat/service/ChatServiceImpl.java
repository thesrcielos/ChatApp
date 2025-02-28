package com.ddev.MessageApp.chat.service;

import com.ddev.MessageApp.chat.dto.Message;
import com.ddev.MessageApp.chat.dto.*;
import com.ddev.MessageApp.chat.model.ChatEntity;
import com.ddev.MessageApp.chat.model.ConversationType;
import com.ddev.MessageApp.chat.model.Conversations;
import com.ddev.MessageApp.chat.model.Messages;
import com.ddev.MessageApp.chat.repository.ChatRepository;
import com.ddev.MessageApp.chat.repository.ConversationRepository;
import com.ddev.MessageApp.chat.repository.MessageRepository;
import com.ddev.MessageApp.user.model.ContactEntity;
import com.ddev.MessageApp.user.model.UserEntity;
import com.ddev.MessageApp.user.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ContactRepository contactRepository;
    private final ChatRepository chatRepository;

    @Override
    public void deleteMessage(UUID id) {
        if(!messageRepository.existsById(id)) {
            throw new RuntimeException("Message with id " + id + " not exists");
        }
        messageRepository.deleteById(id);
    }

    @Override
    public MessageEditResponse editMessage(EditMessageDTO editMessageDTO) {
        UUID id = editMessageDTO.getId();
        Messages message = messageRepository.findById(editMessageDTO.getId()).orElseThrow(() -> new RuntimeException("Message mot found"));
        message.setMessage(editMessageDTO.getMessage());
        messageRepository.save(message);
        return new MessageEditResponse( id, message.getMessage(), message.getSentAt());
    }

    @Override
    public PaginatedListObject<ChatMessage> getChatMessages(Integer id, int page, int size) {
        return getConversationMessages(id, page, size, this::messageToChatMessage);
    }

    @Override
    public PaginatedListObject<GroupMessage> getGroupMessages(Integer id, int page, int size) {
        return getConversationMessages(id, page, size, this::messageToGroupMessage);
    }

    @Transactional
    @Override
    public MessageResponse saveMessage(Message message) {
        Conversations conversation;
        UserEntity user;
        Integer conversationId = message.getConversationId();
        if(conversationId == null) {
            conversation = createConversation();
            user = createChats(conversation, message.getContactId());
        }else{
            conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new RuntimeException("Conversation not found"));
            user = getUserFromContact(message.getContactId());
        }
        Messages messages = Messages.builder()
                .message(message.getContent())
                .conversations(conversation)
                .user(user)
                .sentAt(message.getSentAt())
                .build();
        messageRepository.save(messages);
        return new MessageResponse(messages.getMessage(), conversation.getId(), messages.getId(), messages.getSentAt() );
    }

    private UserEntity getUserFromContact(Integer id) {
        return contactRepository.findUserFromContact(id).orElseThrow(() -> new RuntimeException("Contact not exists."));

    }
    @Transactional
    public Conversations createConversation() {
        Conversations conversations = new Conversations(null, LocalDate.now(), ConversationType.CHAT);
        conversationRepository.save(conversations);

        return conversations;
    }

    @Transactional
    public UserEntity createChats(Conversations conversation, Integer contactId) {
        ContactEntity contact = contactRepository.findById(contactId).orElseThrow(() -> new RuntimeException("Contact not exists."));
        ChatEntity chat1 = new ChatEntity(null,conversation, contact.getUser());
        ChatEntity chat2 = new ChatEntity(null,conversation, contact.getContact());
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        return contact.getUser();
    }


    private ChatMessage messageToChatMessage(Messages message) {
        return new ChatMessage(message.getId(), message.getMessage(), message.getSentAt());
    }

    private GroupMessage messageToGroupMessage(Messages message) {
        UserEntity user = message.getUser();
        return new GroupMessage(message.getId(), message.getMessage(), message.getSentAt(),
                user.getId(), user.getName());
    }

    private <T> PaginatedListObject<T> getConversationMessages(Integer id, int page, int size, Function<Messages, T> mapFunction) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Messages> messagesPage = messageRepository.findByConversationsId(id, pageable);
        List<T> messages = messagesPage.getContent().stream()
                .map(mapFunction)
                .toList();
        return new PaginatedListObject<>(messages, messagesPage.getNumber(),
                messagesPage.getTotalPages(), messagesPage.getTotalElements());
    }
}
