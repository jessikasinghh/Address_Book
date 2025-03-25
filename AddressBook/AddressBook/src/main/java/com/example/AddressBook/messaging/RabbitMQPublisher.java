package com.example.AddressBook.messaging;

import com.example.AddressBook.model.AddressBook;
import com.example.AddressBook.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.example.AddressBook.config.RabbitMQConfig.*;

@Service
public class RabbitMQPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishNewContact(AddressBook contact) {
        logger.info("Publishing event to RabbitMQ: New Contact Added - {}", contact.getName());
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, CONTACT_ROUTING_KEY, contact);
    }

    public void publishUserRegistration(User user) {
        logger.info("Publishing event to RabbitMQ: New User Registered - {}", user.getEmail());
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, USER_ROUTING_KEY, user);
    }
}
