package com.ddev.MessageApp.user.service;

import com.ddev.MessageApp.chat.dto.PaginatedListObject;
import com.ddev.MessageApp.chat.model.ChatEntity;
import com.ddev.MessageApp.chat.model.ConversationType;
import com.ddev.MessageApp.chat.model.Conversations;
import com.ddev.MessageApp.chat.repository.ChatRepository;
import com.ddev.MessageApp.chat.repository.ConversationRepository;
import com.ddev.MessageApp.user.dto.ContactResponse;
import com.ddev.MessageApp.user.dto.ContactSearch;
import com.ddev.MessageApp.user.exception.UserException;
import com.ddev.MessageApp.user.dto.ContactDTO;
import com.ddev.MessageApp.user.model.ContactEntity;
import com.ddev.MessageApp.user.model.Status;
import com.ddev.MessageApp.user.model.UserEntity;
import com.ddev.MessageApp.user.repository.ContactRepository;
import com.ddev.MessageApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService{
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    @Override
    @Transactional
    public void acceptContactRequest(Integer id) {
        ContactEntity contact = findContact(id);
        contact.setStatus(Status.ACCEPTED);
        contact.setCreatedAt(LocalDateTime.now());
        ContactEntity entity = new ContactEntity(null, contact.getContact(),
                contact.getUser(), Status.ACCEPTED, LocalDateTime.now());
        contactRepository.save(contact);
        contactRepository.save(entity);
    }

    @Override
    public void blockContact(Integer id) {
        ContactEntity contact = findContact(id);
        contact.setStatus(Status.ACCEPTED);
        contactRepository.save(contact);
    }

    @Override
    public void deleteContact(Integer id) {
        contactRepository.deleteById(id);
    }

    @Override
    public PaginatedListObject<ContactResponse> getUserBlockedContacts(Integer userId, int page, int size) {
        return getUserContactsByState(userId, page, size, Status.BLOCKED);
    }

    @Override
    public PaginatedListObject<ContactResponse> getUserContacts(Integer userId, int page, int size) {
        return getUserContactsByState(userId, page, size, Status.ACCEPTED);
    }

    @Override
    public PaginatedListObject<ContactResponse> getUserContactRequests(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactEntity> response = contactRepository.findByContactIdAndStatus(userId, Status.PENDING, pageable);
        List<ContactResponse> users = response.getContent()
                .stream()
                .map(entity -> new ContactResponse(entity.getId(),
                        entity.getUser().getId(), entity.getUser().getName(),
                        entity.getUser().getEmail(), entity.getCreatedAt()))
                .toList();
        return new PaginatedListObject<>(users,
                response.getNumber(), response.getTotalPages(), response.getTotalElements());
    }

    @Override
    public PaginatedListObject<ContactSearch> getContactsByPattern(String pattern, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> coincidences = userRepository.findByEmailContainingIgnoreCase(pattern, pageable);
        List<ContactSearch> users = coincidences.get()
                .map(this::userEntityToContactSearch)
                .toList();
        return new PaginatedListObject<>(users,
                coincidences.getNumber(), coincidences.getTotalPages(), coincidences.getTotalElements());
    }

    @Override
    public void rejectContactRequest(Integer id) {
        contactRepository.deleteById(id);
    }

    @Override
    public void sendContactRequest(ContactDTO contactDTO) {
        UserEntity user = findUser(contactDTO.getUserId());
        UserEntity contact = findUser(contactDTO.getContactId());

        ContactEntity contactEntity = new ContactEntity(null, user, contact,
                Status.PENDING, null);
        contactRepository.save(contactEntity);
    }

    private PaginatedListObject<ContactResponse> getUserContactsByState(Integer userId, int page, int size, Status status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactEntity> response = contactRepository.findByUserIdAndStatus(userId, status, pageable);
        List<ContactResponse> users = response.getContent()
                .stream()
                .map(this::contactEntityToResponse)
                .toList();
        return new PaginatedListObject<>(users,
                response.getNumber(), response.getTotalPages(), response.getTotalElements());
    }

    private ContactSearch userEntityToContactSearch(UserEntity user) {
        return new ContactSearch(user.getId(), user.getEmail(), user.getName());
    }

    private ContactResponse contactEntityToResponse(ContactEntity entity) {
        return new ContactResponse(entity.getId(), entity.getContact().getId(),
                entity.getContact().getName(), entity.getContact().getEmail(), entity.getCreatedAt());
    }

    private ContactEntity findContact(Integer id) {
        return contactRepository.findById(id)
                .orElseThrow(()-> new UserException("Contact with id " + id + " not found", 404));
    }

    private UserEntity findUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new UserException("User with id " + id + " not found", 404));
    }
}
