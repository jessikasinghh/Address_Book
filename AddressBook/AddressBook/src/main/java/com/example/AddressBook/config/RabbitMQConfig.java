package com.example.AddressBook.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "addressbook.exchange";
    public static final String CONTACT_ROUTING_KEY = "addressbook.contact.routingkey";
    public static final String USER_ROUTING_KEY = "addressbook.user.routingkey";
    public static final String CONTACT_QUEUE = "contact.queue";
    public static final String USER_QUEUE = "user.queue";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue contactQueue() {
        return new Queue(CONTACT_QUEUE, false);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, false);
    }

    @Bean
    public Binding contactBinding(Queue contactQueue, DirectExchange exchange) {
        return BindingBuilder.bind(contactQueue).to(exchange).with(CONTACT_ROUTING_KEY);
    }

    @Bean
    public Binding userBinding(Queue userQueue, DirectExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with(USER_ROUTING_KEY);
    }
}
