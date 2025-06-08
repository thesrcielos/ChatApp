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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import static com.ddev.MessageApp.chat.util.PaginationValidator.validatePagination;

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
    public void deleteMessage(DeleteMessageDTO deleteMessageDTO) {
        UUID id = deleteMessageDTO.getId();
        if(!messageRepository.existsById(deleteMessageDTO.getId())) {
            throw new ChatExceptions(ChatExceptions.MESSAGE_NOT_FOUND, 404);
        }
        Messages messages = messageRepository.findById(id).orElse(null);
        if(messages.getUser().getId() == deleteMessageDTO.getUserId()){
            throw new ChatExceptions("User can just delete their own messages", 403);
        }
        messageRepository.deleteById(id);

        Integer conversationId = messages.getConversations().getId();
        List<String> userEmails = chatRepository.getUserEmailListFromChat(conversationId,
                messages.getUser().getEmail());
        MessageModification messageModification = new MessageModification(null, conversationId, id, MessageModificationType.DELETE);
        userEmails.forEach(
                (email)->messagingTemplate.convertAndSendToUser(email, "/topic/message-modification", messageModification)
        );
    }

    @Override
    public List<UserDTO> getUsersInformation(Integer conversationId) {
        return chatRepository.getUsersChatInfo(conversationId)
                .stream().map((user) ->
                    new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPicture())
                ).toList();
    }

    @Override
    public MessageEditResponse editMessage(EditMessageDTO editMessageDTO) {
        UUID id = editMessageDTO.getId();
        Messages message = messageRepository.findById(editMessageDTO.getId()).orElseThrow(() -> new ChatExceptions(ChatExceptions.MESSAGE_NOT_FOUND, 404));
        if (message.getUser().getId() == editMessageDTO.getUserId()){
            throw new ChatExceptions("User cant edit a message that it's not theirs", 403);
        }
        message.setMessage(editMessageDTO.getMessage());
        messageRepository.save(message);

        Integer conversationId = message.getConversations().getId();
        List<String> userEmails = chatRepository.getUserEmailListFromChat(conversationId,
                message.getUser().getEmail());
        MessageModification messageModification = new MessageModification(message.getMessage(), conversationId, id, MessageModificationType.EDIT);
        userEmails.forEach((email)-> messagingTemplate.convertAndSendToUser(email, "/topic/message-modification", messageModification));

        return new MessageEditResponse( id, message.getMessage(), message.getSentAt());
    }

    @Override
    public PaginatedListObject<MessageResponse> getChatMessages(Integer id, int page, int size) {
        validatePagination(page, size);
        return getConversationMessages(id, page, size, this::messageToResponse);
    }

    @Override
    public PaginatedListObject<MessageResponse> getMessagesBefore(Integer conversationId, LocalDateTime date, Integer size) {
        validatePagination(0, size);
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        Page<Messages> page = messageRepository.findByConversationsIdAndSentAtLessThan(conversationId, date, pageable);
        List<MessageResponse> values = page.get()
                .map(this::messageToResponse)
                .sorted(Comparator.comparing(MessageResponse::getSentAt))
                .toList();
        return new PaginatedListObject<>(values, 0, page.getTotalPages(), page.getTotalElements());
    }

    @Override
    public PaginatedListObject<ChatDTO> getUserChats(Integer id, int page, int size) {
        validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatEntity> result = chatRepository.findByUserId(id, pageable);
        List<ChatDTO> chats = result.get()
                .sorted(Comparator.comparing((ChatEntity c) -> c.getConversation().getLastActivity()).reversed())
                .map(this::chatEntityToDTO)
                .toList();
        return new PaginatedListObject<>(chats, page, result.getTotalPages(), result.getTotalElements());
    }

    private ChatDTO chatEntityToDTO(ChatEntity chat) {
        Conversations conversations = chat.getConversation();
        LocalDateTime date = chat.getLastMessageSeen() != null ? chat.getLastMessageSeen().getSentAt() : LocalDateTime.now();
        Integer unseenMessages = messageRepository.countUnseenMessages(date, conversations.getId());
        if (conversations.getType().equals(ConversationType.GROUP)) {
            return new ChatDTO(conversations.getId(), null, true,
                    conversationToGroupDto(conversations), unseenMessages);
        }

        return new ChatDTO(chat.getConversation().getId(), chatToContactResponse(chat),
                false, null, unseenMessages);
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
        Conversations conversation = getOrCreateConversation(message);
        UserEntity user = getUserForConversation(conversation, message);

        Messages messages = Messages.builder()
                .message(message.getMessage())
                .conversations(conversation)
                .user(user)
                .type(message.getFileType())
                .url(message.getFileUrl())
                .sentAt(message.getSentAt())
                .build();

        messageRepository.save(messages);
        updateLastActivity(conversation, message.getSentAt());
        updateLastMessageSeen(conversation, messages, user);

        MessageResponse response = buildMessageResponse(message, messages, conversation, user);
        sendMessagesByWS(conversation, response, user.getEmail());
        return response;
    }

    private void updateLastMessageSeen(Conversations conversations, Messages messages, UserEntity user) {
        ChatPK chatPK = new ChatPK(conversations.getId(), user.getId());
        ChatEntity chat = chatRepository.findById(chatPK).orElseThrow(()-> new ChatExceptions("Chat not found", 404));
        chat.setLastMessageSeen(messages);
    }

    private void updateLastActivity(Conversations conversation, LocalDateTime sentAt) {
        conversation.setLastActivity(sentAt);
        conversationRepository.save(conversation);
    }

    private MessageResponse buildMessageResponse(Message message, Messages messages,
                                                 Conversations conversation, UserEntity user) {
        return new MessageResponse(
                messages.getMessage(),
                conversation.getId(),
                messages.getId(),
                user.getId(),
                message.getFileType(),
                message.getFileUrl(),
                messages.getSentAt()
        );
    }

    private Conversations getOrCreateConversation(Message message) {
        if (message.getConversationId() == null || message.getConversationId() < 0) {
            Conversations conversation = createConversation();
            createChats(conversation, message.getContactId());
            return conversation;
        }
        return conversationRepository.findById(message.getConversationId())
                .orElseThrow(() -> new ChatExceptions(ChatExceptions.CONVERSATION_NOT_FOUND, 404));
    }

    private UserEntity getUserForConversation(Conversations conversation, Message message) {
        return findUser(conversation.getType(), message.getContactId());
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
        Conversations conversations = createGroupConversation(groupRequest.getUserId(), groupRequest.getName());
        List<String> memberEmails = createGroupChats(groupRequest.getGroupUsers(), conversations);
        GroupDTO groupDTO = new GroupDTO(groupRequest.getName(), groupRequest.getGroupUsers());
        ChatDTO response = new ChatDTO(conversations.getId(), null, true, groupDTO, 0);
        memberEmails.forEach((email)-> messagingTemplate.convertAndSendToUser(email, "/topic/chat", response));
        return response;
    }

    private Conversations createGroupConversation(Integer userId, String name) {
        UserEntity user = findUser(userId);
        Conversations conversations = new Conversations(null, LocalDate.now(),user, name, ConversationType.GROUP, LocalDateTime.now());
        conversationRepository.save(conversations);

        return conversations;
    }

    private List<String> createGroupChats(List<Integer> groupUsers, Conversations conversation) {
        Integer conversationId = conversation.getId();
        UserEntity user = conversation.getCreatedBy();
        ChatPK chatPK = new ChatPK(conversationId, user.getId());
        ChatEntity chat = new ChatEntity(chatPK, conversation, user, null);
        chatRepository.save(chat);
        List<String> emails = new ArrayList<>();
        for(Integer userId : groupUsers) {
            UserEntity userEntity = findUser(userId);
            emails.add(userEntity.getEmail());
            ChatPK pk = new ChatPK(conversationId, userId);
            ChatEntity chatEntity = new ChatEntity(pk, conversation, userEntity, null);
            chatRepository.save(chatEntity);
        }
        return emails;
    }

    private UserEntity findUser(Integer id) {
        return userRepository.findById(id).orElseThrow(()-> new UserExceptions("User not found", 404));
    }
    private ContactEntity getContact(Integer id) {
        return contactRepository.findById(id).orElseThrow(() -> new UserExceptions(UserExceptions.CONTACT_NOT_EXIST, 404));
    }

    @Transactional
    public Conversations createConversation() {
        Conversations conversations = new Conversations(null, LocalDate.now(),null,null, ConversationType.CHAT, LocalDateTime.now());
        conversationRepository.save(conversations);

        return conversations;
    }

    @Transactional
    public void createChats(Conversations conversation, Integer userId) {
        ContactEntity contact = getContact(userId);
        ChatPK chatPK = new ChatPK(conversation.getId(), contact.getUser().getId());
        ChatPK chatPK2 = new ChatPK(conversation.getId(), contact.getContact().getId());
        ChatEntity chat1 = new ChatEntity(chatPK,conversation, contact.getUser(), null);
        ChatEntity chat2 = new ChatEntity(chatPK2,conversation, contact.getContact(), null);
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        sendNewChatsToUsers(chat1, chat2);
    }

    private void sendNewChatsToUsers(ChatEntity chat1, ChatEntity chat2){
        messagingTemplate.convertAndSendToUser(chat1.getUser().getEmail(), "/topic/chat", chatEntityToDTO(chat1));
        messagingTemplate.convertAndSendToUser(chat2.getUser().getEmail(), "/topic/chat", chatEntityToDTO(chat2));
    }
    @Override
    public PaginatedListObject<ChatDTO> getUserContactsByPattern(Integer id, String pattern, int page, int size) {
        validatePagination(page, size);
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

    @Override
    public void markLastMessageSeen(MessageSeenDTO messageSeenDTO) {
        ChatPK pk = new ChatPK(messageSeenDTO.getChatId(), messageSeenDTO.getUserId());
        ChatEntity chat = chatRepository.findById(pk)
                .orElseThrow(()-> new ChatExceptions("Chat not found", 404));
        Messages message = messageRepository.findById(messageSeenDTO.getMessageId())
                .orElseThrow(()-> new ChatExceptions("Message not found", 404));
        checkLastMessageSeen(chat.getLastMessageSeen(), message);
        chat.setLastMessageSeen(message);
        chatRepository.save(chat);
    }

    private void checkLastMessageSeen(Messages lastMessage, Messages newLastMessage) {
        if(lastMessage != null && !lastMessage.getSentAt().isBefore(newLastMessage.getSentAt())){
            throw new ChatExceptions("New Last message must be sent after the last one", 400);
        }
    }
    private ChatDTO contactEntityToChatDto(ContactEntity entity) {
        ContactResponse response = new ContactResponse(entity.getId(), entity.getContact().getId(),
                entity.getContact().getName(), entity.getContact().getEmail(), entity.getCreatedAt());
        Integer id = chatRepository.findConversationIdByUsers(entity.getContact().getId(), entity.getUser().getId())
                .orElse(entity.getId()*(-1));
        return new ChatDTO(id, response, false, null, 0);
    }

    private MessageResponse messageToResponse(Messages message) {
        return new MessageResponse(message.getMessage(), message.getConversations().getId(),
                message.getId(), message.getUser().getId(), message.getType(),
                message.getUrl(), message.getSentAt());
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