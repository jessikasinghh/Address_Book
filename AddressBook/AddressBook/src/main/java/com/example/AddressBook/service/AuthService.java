package com.example.AddressBook.service;

import com.example.AddressBook.model.User;
import com.example.AddressBook.repository.UserRepository;
import com.example.AddressBook.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final ConcurrentHashMap<String, String> resetTokens = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(String username, String email, String password) {
        if (username == null || email == null || password == null) {
            throw new RuntimeException("Username, email, and password are required!");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole("ROLE_USER");
        System.out.println("Encoded Password (Before Saving): " + encodedPassword);

        userRepository.save(user);
        emailService.sendWelcomeEmail(email);
        return "User registered successfully!";
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    public String authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return jwtUtil.generateToken(username);
    }

    public String forgotPassword(String email) {
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required!");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered!"));

        String resetToken = UUID.randomUUID().toString();
        resetTokens.put(resetToken, user.getEmail());
        emailService.sendPasswordResetEmail(email, resetToken);
        return "Password reset link sent to your email!";
    }

    public String resetPassword(String token, String newPassword) {
        if (token == null || newPassword == null || newPassword.isEmpty()) {
            throw new RuntimeException("Token and new password are required!");
        }

        String email = resetTokens.remove(token);
        if (email == null) {
            throw new RuntimeException("Invalid or expired reset token!");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password updated successfully!";
    }
}
