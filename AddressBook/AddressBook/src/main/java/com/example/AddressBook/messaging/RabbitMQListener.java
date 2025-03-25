package com.example.AddressBook.messaging;

import com.example.AddressBook.model.AddressBook;
import com.example.AddressBook.model.User;
import com.example.AddressBook.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.example.AddressBook.config.RabbitMQConfig.*;

@Component
public class RabbitMQListener {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);
    private final EmailService emailService;

    public RabbitMQListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = CONTACT_QUEUE)
    public void handleNewContact(AddressBook contact) {
        logger.info("Received message: New Contact Added - {}", contact.getName());
    }

    @RabbitListener(queues = USER_QUEUE)
    public void handleNewUserRegistration(User user) {
        logger.info("Received message: New User Registered - {}", user.getEmail());
        emailService.sendWelcomeEmail(user.getEmail());
    }
}
