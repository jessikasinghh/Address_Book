package com.example.AddressBook.controller;

import com.example.AddressBook.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-welcome")
    public ResponseEntity<String> sendTestEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailService.sendWelcomeEmail(email);
        return ResponseEntity.ok("Test email sent successfully!");
    }
}
