package com.ddev.MessageApp.user.controller;

import com.ddev.MessageApp.chat.dto.PaginatedListObject;
import com.ddev.MessageApp.user.dto.ContactDTO;
import com.ddev.MessageApp.user.dto.ContactResponse;
import com.ddev.MessageApp.user.dto.ContactSearch;
import com.ddev.MessageApp.user.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContactController {
    private final ContactService contactService;

    @PostMapping("/users/contacts/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public void acceptContact(@PathVariable Integer id) {
        contactService.acceptContactRequest(id);
    }

    @PostMapping("/users/contacts/{id}/block")
    @ResponseStatus(HttpStatus.OK)
    public void blockContact(@PathVariable Integer id) {
        contactService.blockContact(id);
    }

    @DeleteMapping("/users/contacts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteContact(@PathVariable Integer id) {
        contactService.deleteContact(id);
    }

    @GetMapping("/users/{id}/contacts/blocked")
    public ResponseEntity<PaginatedListObject<ContactResponse>> getUserBlockedContacts(@PathVariable Integer id, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(contactService.getUserBlockedContacts(id, page, size));
    }

    @GetMapping("/users/{id}/contacts")
    public ResponseEntity<PaginatedListObject<ContactResponse>> getUserContacts(@PathVariable Integer id, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(contactService.getUserContacts(id, page, size));
    }

    @GetMapping("/users/{id}/contacts/requested")
    public ResponseEntity<PaginatedListObject<ContactResponse>> getUserRequestedContacts(@PathVariable Integer id, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(contactService.getUserContactRequests(id, page, size));
    }

    @GetMapping("/users/{id}/contacts/requested-sent")
    public ResponseEntity<PaginatedListObject<ContactResponse>> getUserRequestedContactsSent(@PathVariable Integer id, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(contactService.getUserContactRequestsSent(id, page, size));
    }

    @DeleteMapping("/users/contacts/request/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void rejectContactRequest(@PathVariable Integer id) {
        contactService.rejectContactRequest(id);
    }

    @PostMapping("/users/contacts/request")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendContactRequest(@Valid @RequestBody ContactDTO contactDTO) {
        contactService.sendContactRequest(contactDTO);
    }

    @GetMapping("/users/coincidences")
    public ResponseEntity<PaginatedListObject<ContactSearch>> getContactCoincidences(@RequestParam String pattern,
                                                                                     @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(contactService.getContactsByPattern(pattern, page, size));
    }

    @GetMapping("/users/{id}/coincidences")
    public ResponseEntity<PaginatedListObject<ContactSearch>> getContactCoincidences(@PathVariable Integer id, @RequestParam String pattern,
                                                                                     @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(contactService.getContactsByPattern(pattern, page, size));
    }

}
