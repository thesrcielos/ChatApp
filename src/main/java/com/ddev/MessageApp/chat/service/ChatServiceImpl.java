package com.ddev.MessageApp.chat.service;

import com.ddev.MessageApp.chat.dto.Message;
import com.ddev.MessageApp.chat.dto.*;
import com.ddev.MessageApp.chat.model.*;
import com.ddev.MessageApp.chat.repository.ChatRepository;
import com.ddev.MessageApp.chat.repository.ConversationRepository;
import com.ddev.MessageApp.chat.repository.MessageRepository;
import com.ddev.MessageApp.user.dto.ContactResponse;
import com.ddev.MessageApp.user.dto.UserDTO;
import com.ddev.MessageApp.user.model.ContactEntity;
import com.ddev.MessageApp.user.model.UserEntity;
import com.ddev.MessageApp.user.repository.ContactRepository;
import com.ddev.MessageApp.user.repository.UserRepository;
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
    private final UserRepository userRepository;
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
    public List<UserDTO> getUsersInformation(Integer conversationId) {
        return chatRepository.getUsersChatInfo(conversationId)
                .stream().map((user) ->
                    new UserDTO(user.getId(), user.getName(), user.getEmail())
                ).toList();
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
    public PaginatedListObject<MessageResponse> getChatMessages(Integer id, int page, int size) {
        return getConversationMessages(id, page, size, this::messageToResponse);
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
        Conversations conversations = chat.getConversation();
        if (conversations.getType().equals(ConversationType.GROUP)) {
            return new ChatDTO(conversations.getId(), null, true,
                    conversationToGroupDto(conversations));
        }

        return new ChatDTO(chat.getConversation().getId(), chatToContactResponse(chat),
                false, null);
    }

    private ContactResponse chatToContactResponse(ChatEntity chat) {
        Integer id = chatRepository.findUserIdByConversationAndNotUser(chat.getConversation().getId(), chat.getUser().getId())
                .orElseThrow(() -> new UserExceptions("The other user of the chat was not found", 400));
        ContactEntity contact = contactRepository.findByUserIdAndContactId(chat.getUser().getId(), id)
                .orElseThrow(() -> new UserExceptions("Contact not found", 400));
        UserEntity user = contact.getContact();
        return new ContactResponse(contact.getId(), id, user.getName(),
                user.getEmail(), contact.getCreatedAt());
    }

    private GroupDTO conversationToGroupDto(Conversations conversation) {
        List<Integer> users = chatRepository.getUserListFromChat(conversation.getId());
        return new GroupDTO(conversation.getName(), users);
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
            conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new ChatExceptions(ChatExceptions.CONVERSATION_NOT_FOUND, 404));
            user = findUser(conversation.getType(), message.getContactId());
        }
        Messages messages = Messages.builder()
                .message(message.getContent())
                .conversations(conversation)
                .user(user)
                .type(message.getFileType())
                .url(message.getFileUrl())
                .sentAt(message.getSentAt())
                .build();
        messageRepository.save(messages);
        MessageResponse response = new MessageResponse(messages.getMessage(), conversation.getId(), messages.getId(), user.getId(),
                message.getFileType(), message.getFileUrl(),messages.getSentAt() );
        sendMessagesByWS(conversation, response, user.getEmail());
        return response;
    }

    private void sendMessagesByWS(Conversations conversations, MessageResponse response, String userEmail) {
        List<String> userEmails = chatRepository.getUserEmailListFromChat(conversations.getId(), userEmail);
        userEmails.forEach((email) -> messagingTemplate.convertAndSendToUser(email, "/topic/conversation", response));
    }

    private UserEntity findUser(ConversationType type, Integer id) {
        if(type.equals(ConversationType.GROUP)){
            return findUser(id);
        }

        return contactRepository.findUserFromContact(id)
                .orElseThrow(() -> new UserExceptions("Contact not found", 404));
    }
    @Override
    @Transactional
    public ChatDTO createGroup(GroupRequest groupRequest) {
        UserEntity user = findUser(groupRequest.getUserId());
        Conversations conversations = new Conversations(null, LocalDate.now(),user, groupRequest.getName(), ConversationType.GROUP);
        conversationRepository.save(conversations);

        Integer conversationId = conversations.getId();
        ChatPK chatPK = new ChatPK(conversationId, user.getId());
        ChatEntity chat = new ChatEntity(chatPK, conversations, user);
        chatRepository.save(chat);

        for(Integer userId : groupRequest.getGroupUsers()) {
            UserEntity userEntity = findUser(userId);
            ChatPK pk = new ChatPK(conversationId, userId);
            ChatEntity chatEntity = new ChatEntity(pk, conversations, userEntity);
            chatRepository.save(chatEntity);
        }

        GroupDTO groupDTO = new GroupDTO(groupRequest.getName(), groupRequest.getGroupUsers());
        return new ChatDTO(conversationId, null, true, groupDTO);
    }

    private UserEntity findUser(Integer id) {
        return userRepository.findById(id).orElseThrow(()-> new UserExceptions("User not found", 404));
    }
    private ContactEntity getContact(Integer id) {
        return contactRepository.findById(id).orElseThrow(() -> new UserExceptions(UserExceptions.CONTACT_NOT_EXIST, 404));
    }

    @Transactional
    public Conversations createConversation() {
        Conversations conversations = new Conversations(null, LocalDate.now(),null,null, ConversationType.CHAT);
        conversationRepository.save(conversations);

        return conversations;
    }

    @Transactional
    public UserEntity createChats(Conversations conversation, Integer userId) {
        ContactEntity contact = getContact(userId);
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
        if (pattern.isEmpty()) {
            return getUserChats(id, page, size);
        }
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
        return new ChatDTO(id, response, false, null);
    }

    private MessageResponse messageToResponse(Messages message) {
        return new MessageResponse(message.getMessage(), message.getConversations().getId(),
                message.getId(), message.getUser().getId(), message.getType(),
                message.getUrl(), message.getSentAt());
    }

    private GroupMessage messageToGroupMessage(Messages message) {
        UserEntity user = message.getUser();
        return new GroupMessage(message.getId(), message.getMessage(), message.getSentAt(),
                user.getId(), user.getName());
    }

    private <T> PaginatedListObject<T> getConversationMessages(Integer id, int page, int size, Function<Messages, T> mapFunction) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Messages> messagesPage = messageRepository.findByConversationsIdOrderBySentAtDesc(id, pageable);
        List<T> messages = messagesPage.getContent().stream()
                .sorted(Comparator.comparing(Messages::getSentAt))
                .map(mapFunction)
                .toList();
        return new PaginatedListObject<>(messages, messagesPage.getNumber(),
                messagesPage.getTotalPages(), messagesPage.getTotalElements());
    }

}
