package com.messaging.travel.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TRAVEL_EXCHANGE = "travel.exchange";

    public static final String NOTIFICATION_QUEUE = "notification.queue";

    public static final String BOOKING_CONFIRMED_ROUTING_KEY = "booking.confirmed";
    public static final String BOOKING_CANCELLED_ROUTING_KEY = "booking.cancelled";

    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange(TRAVEL_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder
                .durable(NOTIFICATION_QUEUE)
                .build();
    }

    @Bean
    public Binding bookingConfirmedBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(travelExchange())
                .with(BOOKING_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding bookingCancelledBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(travelExchange())
                .with(BOOKING_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
