package com.example.AddressBook.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // ✅ Welcome Email
    public void sendWelcomeEmail(String to) {
        try {
            logger.info("📨 Preparing welcome email for {}", to);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Welcome to AddressBook!");
            helper.setText("Hello,\n\nThank you for registering with AddressBook. Enjoy your experience!", true);

            logger.info("Sending email...");
            javaMailSender.send(message);
            logger.info("✅ Email successfully sent to {}", to);
        } catch (MessagingException e) {
            logger.error(" Email send failed to {} - Error: {}", to, e.getMessage(), e);
        }
    }

    // ✅ Password Reset Email
    public void sendPasswordResetEmail(String to, String token) {
        try {
            logger.info("📨 Preparing password reset email for {}", to);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Reset Your Password");
            helper.setText("Hello,\n\nClick the link below to reset your password:\n\n" +
                    "http://localhost:8080/reset-password?token=" + token +
                    "\n\nThis link will expire in 15 minutes.", true);
            logger.info(" Sending password reset email...");
            javaMailSender.send(message);
            logger.info(" Password reset email sent to {}", to);
        } catch (MessagingException e) {
            logger.error(" Password reset email failed to {} - Error: {}", to, e.getMessage(), e);
        }
    }
}
