package com.ddev.MessageApp.user.service;

import com.ddev.MessageApp.chat.dto.PaginatedListObject;
import com.ddev.MessageApp.user.dto.ContactDTO;
import com.ddev.MessageApp.user.dto.ContactResponse;
import com.ddev.MessageApp.user.dto.ContactSearch;

public interface ContactService {
    void acceptContactRequest(Integer id);
    void blockContact(Integer id);
    void deleteContact(Integer id);
    PaginatedListObject<ContactResponse> getUserBlockedContacts(Integer userId, int page, int size);
    PaginatedListObject<ContactResponse> getUserContacts(Integer userId, int page, int size);
    PaginatedListObject<ContactResponse> getUserContactRequests(Integer userId, int page, int size);
    PaginatedListObject<ContactSearch> getContactsByPattern(String pattern, int page, int size);
    void rejectContactRequest(Integer id);
    void sendContactRequest(ContactDTO contactDTO);
}
