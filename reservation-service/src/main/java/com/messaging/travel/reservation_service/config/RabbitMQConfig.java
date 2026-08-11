package com.messaging.travel.reservation_service.config;

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

    public static final String RESERVATION_QUEUE = "reservation.queue";

    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";

    public static final String BOOKING_RESERVED_ROUTING_KEY = "booking.reserved";

    public static final String BOOKING_FAILED_ROUTING_KEY = "booking.failed";

    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange(TRAVEL_EXCHANGE);
    }

    @Bean
    public Queue reservationQueue() {
        return QueueBuilder
                .durable(RESERVATION_QUEUE)
                .build();
    }

    @Bean
    public Binding bookingCreatedBinding() {
        return BindingBuilder
                .bind(reservationQueue())
                .to(travelExchange())
                .with(BOOKING_CREATED_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
