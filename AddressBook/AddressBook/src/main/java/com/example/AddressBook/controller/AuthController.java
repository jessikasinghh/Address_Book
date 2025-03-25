package com.example.AddressBook.controller;

import com.example.AddressBook.dto.UserDTO;
import com.example.AddressBook.service.AuthService;
import com.example.AddressBook.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "APIs for User Authentication")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        log.info("Received registration request for: {}", userDTO.getUsername());

        if (userDTO.getUsername() == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
            log.warn("Invalid registration details provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "All fields are required"));
        }

        String response = authService.registerUser(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword());

        log.info("User registered successfully: {}", userDTO.getUsername());
        return ResponseEntity.ok(Map.of("message", response));
    }


    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Authenticate a user and return a JWT token.")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        log.info("Login attempt for user: {}", userDTO.getUsername());

        if (userDTO.getUsername() == null || userDTO.getPassword() == null) {
            log.warn("Invalid login details provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Username and password are required"));
        }

        String token = authService.authenticate(userDTO.getUsername(), userDTO.getPassword());

        if (token == null) {
            log.warn("Login failed for user: {}", userDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        }

        log.info("Login successful for user: {}", userDTO.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password", description = "Sends a password reset link to the user's email.")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            log.warn("Forgot password request with empty email");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email is required"));
        }

        log.info("Received forgot password request for email: {}", email);
        String response = authService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Resets the user's password using a token.")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null || newPassword.isEmpty()) {
            log.warn("Reset password request with missing fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Token and new password are required"));
        }

        log.info("Processing password reset request for token: {}", token);
        String response = authService.resetPassword(token, passwordEncoder.encode(newPassword));
        return ResponseEntity.ok(Map.of("message", response));
    }
}
