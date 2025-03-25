package com.example.AddressBook.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.AddressBook.model.AddressBook;
import com.example.AddressBook.repository.AddressBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AddressBookServiceTest {

    @Mock
    private AddressBookRepository addressBookRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AddressBookService addressBookService;

    private AddressBook contact1, contact2;

    @BeforeEach
    void setUp() {
        contact1 = new AddressBook(1L, "John Doe", "1234567890");
        contact2 = new AddressBook(2L, "Jane Doe", "0987654321");
    }

    // ✅ Test: Get all contacts
    @Test
    void testGetAllContacts() {
        when(addressBookRepository.findAll()).thenReturn(Arrays.asList(contact1, contact2));

        List<AddressBook> result = addressBookService.getAllContacts();

        assertEquals(2, result.size());
        verify(addressBookRepository, times(1)).findAll(); // Verify DB call
    }

    // ✅ Test: Get contact by ID
    @Test
    void testGetContactById() {
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(contact1));

        AddressBook result = addressBookService.getContactById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(addressBookRepository, times(1)).findById(1L);
    }


    // ✅ Test: Add contact (Cache Eviction & Event Published)
    @Test
    void testAddContact() {
        when(addressBookRepository.save(any(AddressBook.class))).thenReturn(contact1);

        AddressBook savedContact = addressBookService.addContact(contact1);

        assertNotNull(savedContact);
        assertEquals("John Doe", savedContact.getName());

        verify(addressBookRepository, times(1)).save(contact1);
        verify(rabbitTemplate, times(1))
                .convertAndSend("addressbook.exchange", "contact.events", "Contact Added: John Doe");
    }

    // ✅ Test: Update contact (Cache Update & Event Published)
    @Test
    void testUpdateContact() {
        when(addressBookRepository.findById(1L)).thenReturn(Optional.of(contact1));
        when(addressBookRepository.save(any(AddressBook.class))).thenReturn(contact1);

        AddressBook updatedContact = new AddressBook(1L, "John Smith", "1234567890");
        AddressBook result = addressBookService.updateContact(1L, updatedContact);

        assertEquals("John Smith", result.getName());

        verify(addressBookRepository, times(1)).findById(1L);
        verify(addressBookRepository, times(1)).save(any(AddressBook.class));
        verify(rabbitTemplate, times(1))
                .convertAndSend("addressbook.exchange", "contact.events", "Contact Updated: John Smith");
    }

    // ✅ Test: Delete contact (Cache Eviction & Event Published)
    @Test
    void testDeleteContact() {
        when(addressBookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(addressBookRepository).deleteById(1L);

        addressBookService.deleteContact(1L);

        verify(addressBookRepository, times(1)).existsById(1L);
        verify(addressBookRepository, times(1)).deleteById(1L);
        verify(rabbitTemplate, times(1))
                .convertAndSend("addressbook.exchange", "contact.events", "Contact Deleted: 1");
    }


    //throws test cases added differently
    // ✅ Test: Get contact by ID - Not Found
    @Test
    void testGetContactById_NotFound() {
        when(addressBookRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> addressBookService.getContactById(3L));

        verify(addressBookRepository, times(1)).findById(3L);
    }


    // ✅ Test: Update contact - Not Found
    @Test
    void testUpdateContact_NotFound() {
        when(addressBookRepository.findById(3L)).thenReturn(Optional.empty());

        AddressBook updatedContact = new AddressBook(3L, "New Name", "9999999999");

        assertThrows(RuntimeException.class, () -> addressBookService.updateContact(3L, updatedContact));

        verify(addressBookRepository, times(1)).findById(3L);
    }

    // ✅ Test: Delete contact - Not Found
    @Test
    void testDeleteContact_NotFound() {
        when(addressBookRepository.existsById(3L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> addressBookService.deleteContact(3L));

        verify(addressBookRepository, times(1)).existsById(3L);
        verify(addressBookRepository, never()).deleteById(anyLong()); // Ensure deleteById is never called
    }



}
