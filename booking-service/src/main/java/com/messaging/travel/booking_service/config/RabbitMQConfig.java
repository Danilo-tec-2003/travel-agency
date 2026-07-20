// Exchange:  roteador de mensagens.
// Queue: É onde a mensagem fica armazenada até algum consumidor ler.
// Binding: É a ligação entre exchange e queue.
// Routing key: É a chave usada pela exchange para decidir para qual fila mandar a mensagem.
// TopicExchange: Exchange que roteia mensagens usando nomes/padrões.
// Durable Queue: Fila que continua existindo mesmo se o RabbitMQ reiniciar.
// MessageConverter: Converte objeto Java <-> JSON.

package com.messaging.travel.booking_service.config;

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
    public static final String BOOKING_RESULT_QUEUE = "booking-result.queue";
    public static final String BOOKING_RESERVED_ROUTING_KEY = "booking.reserved";
    public static final String BOOKING_FAILED_ROUTING_KEY = "booking.failed";

    @Bean
    public TopicExchange travelExchange() {
        return new TopicExchange(TRAVEL_EXCHANGE);
    }

    @Bean
    public Queue bookingResultQueue() {
        return QueueBuilder
                .durable(BOOKING_RESULT_QUEUE)
                .build();
    }

    @Bean
    public Binding bookingReservedBinding() {
        return BindingBuilder
                .bind(bookingResultQueue())
                .to(travelExchange())
                .with(BOOKING_RESERVED_ROUTING_KEY);
    }

    @Bean
    public Binding bookingFailedBinding() {
        return BindingBuilder
                .bind(bookingResultQueue())
                .to(travelExchange())
                .with(BOOKING_FAILED_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
