package com.ddev.MessageApp.chat.service;

import com.ddev.MessageApp.chat.dto.Message;
import com.ddev.MessageApp.chat.dto.*;
import com.ddev.MessageApp.chat.model.*;
import com.ddev.MessageApp.chat.repository.ChatRepository;
import com.ddev.MessageApp.chat.repository.ConversationRepository;
import com.ddev.MessageApp.chat.repository.MessageRepository;
import com.ddev.MessageApp.user.dto.ContactResponse;
import com.ddev.MessageApp.user.model.ContactEntity;
import com.ddev.MessageApp.user.model.UserEntity;
import com.ddev.MessageApp.user.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
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
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void deleteMessage(UUID id) {
        if(!messageRepository.existsById(id)) {
            throw new ChatExceptions(ChatExceptions.MESSAGE_NOT_FOUND, 404);
        }
        messageRepository.deleteById(id);
    }

    @Override
    public MessageEditResponse editMessage(EditMessageDTO editMessageDTO) {
        UUID id = editMessageDTO.getId();
        Messages message = messageRepository.findById(editMessageDTO.getId()).orElseThrow(() -> new ChatExceptions(ChatExceptions.MESSAGE_NOT_FOUND, 404));
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

    @Override
    public PaginatedListObject<ChatDTO> getUserChats(Integer id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatEntity> result = chatRepository.findByUserId(id, pageable);
        List<ChatDTO> chats = result.get()
                .map(this::chatEntityToDTO)
                .toList();
        return new PaginatedListObject<>(chats, page, result.getTotalPages(), result.getTotalElements());
    }

    private ChatDTO chatEntityToDTO(ChatEntity chat) {
        Integer id = chatRepository.findUserIdByConversationAndNotUser(chat.getConversation().getId(), chat.getUser().getId())
                .orElseThrow(() -> new UserExceptions("The other user of the chat was not found", 400));
        ContactEntity contact = contactRepository.findByUserIdAndContactId(chat.getUser().getId(), id)
                .orElseThrow(() -> new UserExceptions("Contact not found", 400));
        UserEntity user = contact.getContact();
        ContactResponse contactResponse = new ContactResponse(contact.getId(), id, user.getName(), user.getEmail(), contact.getCreatedAt());
        return new ChatDTO(chat.getConversation().getId(), contactResponse);
    }

    @Transactional
    @Override
    public MessageResponse saveMessage(Message message) {
        Conversations conversation;
        UserEntity user;
        Integer conversationId = message.getConversationId();
        ContactEntity contact = getContact(message.getContactId());
        if(conversationId == null) {
            conversation = createConversation();
            user = createChats(conversation, contact);
        }else{
            conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new ChatExceptions(ChatExceptions.CONVERSATION_NOT_FOUND, 404));
            user = contact.getUser();
        }
        Messages messages = Messages.builder()
                .message(message.getContent())
                .conversations(conversation)
                .user(user)
                .sentAt(message.getSentAt())
                .build();
        messageRepository.save(messages);
        MessageResponse response = new MessageResponse(messages.getMessage(), conversation.getId(), messages.getId(), contact.getUser().getId(),messages.getSentAt() );
        messagingTemplate.convertAndSendToUser(contact.getContact().getEmail(), "/topic/conversation", response);
        return response;
    }

    private ContactEntity getContact(Integer id) {
        return contactRepository.findById(id).orElseThrow(() -> new UserExceptions(UserExceptions.CONTACT_NOT_EXIST, 404));
    }

    @Transactional
    public Conversations createConversation() {
        Conversations conversations = new Conversations(null, LocalDate.now(), ConversationType.CHAT);
        conversationRepository.save(conversations);

        return conversations;
    }

    @Transactional
    public UserEntity createChats(Conversations conversation, ContactEntity contact) {
        ChatPK chatPK = new ChatPK(conversation.getId(), contact.getUser().getId());
        ChatPK chatPK2 = new ChatPK(conversation.getId(), contact.getContact().getId());
        ChatEntity chat1 = new ChatEntity(chatPK,conversation, contact.getUser());
        ChatEntity chat2 = new ChatEntity(chatPK2,conversation, contact.getContact());
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        return contact.getUser();
    }

    @Override
    public PaginatedListObject<ChatDTO> getUserContactsByPattern(Integer id, String pattern, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactEntity> coincidences = contactRepository.searchContacts(id,pattern, pageable);
        List<ChatDTO> users = coincidences.get()
                .map(this::contactEntityToChatDto)
                .toList();
        return new PaginatedListObject<>(users,
                coincidences.getNumber(), coincidences.getTotalPages(), coincidences.getTotalElements());
    }

    private ChatDTO contactEntityToChatDto(ContactEntity entity) {
        ContactResponse response = new ContactResponse(entity.getId(), entity.getContact().getId(),
                entity.getContact().getName(), entity.getContact().getEmail(), entity.getCreatedAt());
        Integer id = chatRepository.findConversationIdByUsers(entity.getContact().getId(), entity.getUser().getId())
                .orElse(null);
        return new ChatDTO(id, response);
    }

    private ChatMessage messageToChatMessage(Messages message) {
        return new ChatMessage(message.getId(), message.getUser().getId(),message.getMessage(), message.getSentAt());
    }

    private GroupMessage messageToGroupMessage(Messages message) {
        UserEntity user = message.getUser();
        return new GroupMessage(message.getId(), message.getMessage(), message.getSentAt(),
                user.getId(), user.getName());
    }

    private <T> PaginatedListObject<T> getConversationMessages(Integer id, int page, int size, Function<Messages, T> mapFunction) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Messages> messagesPage = messageRepository.findByConversationsIdOrderBySentAtAsc(id, pageable);
        List<T> messages = messagesPage.getContent().stream()
                .sorted(Comparator.comparing(Messages::getSentAt))
                .map(mapFunction)
                .toList();
        return new PaginatedListObject<>(messages, messagesPage.getNumber(),
                messagesPage.getTotalPages(), messagesPage.getTotalElements());
    }

}
