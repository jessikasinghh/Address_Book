package com.example.AddressBook.controller;

import com.example.AddressBook.dto.AddressBookDTO;
import com.example.AddressBook.model.AddressBook;
import com.example.AddressBook.service.AddressBookService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
@PreAuthorize("hasAuthority('ROLE_USER')")  // Ensure only authenticated users can access
public class AddressBookController {

    private final AddressBookService addressBookService;

    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    // ✅ Get all contacts (Cached)
    @GetMapping
    public List<AddressBook> getAllContacts() {
        return addressBookService.getAllContacts();
    }

    // ✅ Get a single contact by ID (Cached)
    @GetMapping("/{id}")
    public AddressBook getContactById(@PathVariable Long id) {
        return addressBookService.getContactById(id);
    }

    // ✅ Add a new contact (Cache Evicted)


    @PostMapping
    public ResponseEntity<AddressBook> addContact(@RequestBody AddressBookDTO contactDTO) {
        AddressBook contact = new AddressBook();
        contact.setName(contactDTO.getName());
        contact.setPhone(contactDTO.getPhone());
        contact.setEmail(contactDTO.getEmail());  // ✅ Ensure Email is Set

        AddressBook savedContact = addressBookService.addContact(contact);
        return ResponseEntity.ok(savedContact);
    }




    // ✅ Update a contact (Cache Evicted)
    @PutMapping("/{id}")
    public AddressBook updateContact(@PathVariable Long id, @RequestBody AddressBook contact) {
        return addressBookService.updateContact(id, contact);
    }

    // ✅ Delete a contact (Cache Evicted)
    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable Long id) {
        addressBookService.deleteContact(id);
    }
}
