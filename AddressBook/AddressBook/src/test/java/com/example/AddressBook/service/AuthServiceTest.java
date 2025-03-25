package com.example.AddressBook.service;

import static org.mockito.Mockito.*;

import com.example.AddressBook.model.User;
import com.example.AddressBook.repository.UserRepository;
import com.example.AddressBook.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;  // ✅ Mock EmailService

    @InjectMocks
    private AuthService authService;  // ✅ Automatically inject mocks

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // ✅ Initialize Mocks
    }

    @Test
    void testRegisterUser_Success() {
        // ✅ Arrange
        when(userRepository.findByUsername("jagrati")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jagrati@example.com")).thenReturn(Optional.empty());

        User mockUser = new User();
        mockUser.setUsername("jagrati");
        mockUser.setEmail("jagrati@example.com");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // ✅ Act
        String result = authService.registerUser("jagrati", "jagrati@example.com", "password123");

        // ✅ Assert
        assertEquals("User registered successfully!", result);
        verify(emailService, times(1)).sendWelcomeEmail("jagrati@example.com");  // ✅ Verify email is sent
    }

    @Test
    void testForgotPassword_Success() {
        // ✅ Arrange
        User mockUser = new User();
        mockUser.setEmail("jagrati@example.com");
        when(userRepository.findByEmail("jagrati@example.com")).thenReturn(Optional.of(mockUser));

        // ✅ Act
        String result = authService.forgotPassword("jagrati@example.com");

        // ✅ Assert
        assertEquals("Password reset link sent to your email!", result);
        verify(emailService, times(1)).sendPasswordResetEmail(eq("jagrati@example.com"), anyString()); // ✅ Verify email is sent
    }

    //throws test cases
    // ✅ Test: Register User - Already Exists
    @Test
    void testRegisterUser_AlreadyExists() {
        when(userRepository.findByUsername("jagrati")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.registerUser("jagrati", "jagrati@example.com", "password123"));

        verify(userRepository, times(1)).findByUsername("jagrati");
    }

    // ✅ Test: Forgot Password - Email Not Found
    @Test
    void testForgotPassword_EmailNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.forgotPassword("notfound@example.com"));

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

}
