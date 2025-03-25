package com.example.AddressBook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender; // ✅ Mock JavaMailSender

    @InjectMocks
    private EmailService emailService; // ✅ Inject mocks

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendWelcomeEmail_Success() {
        // ✅ Arrange
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

        // ✅ Act
        emailService.sendWelcomeEmail("jagrati@example.com");

        // ✅ Assert
        verify(javaMailSender, times(1)).send(mockMessage); // ✅ Ensure email is sent
    }
    //throws test
    // ✅ Test: Send Welcome Email - Failure
    @Test
    void testSendWelcomeEmail_Failure() {
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new RuntimeException("Mail server error")).when(javaMailSender).send(any(MimeMessage.class));

        // ✅ Now expecting exception since it's not caught inside sendWelcomeEmail()
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> emailService.sendWelcomeEmail("jagrati@example.com"));

        assertEquals("Mail server error", thrown.getMessage()); // ✅ Ensure correct exception message

        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mockMessage);
    }



}
