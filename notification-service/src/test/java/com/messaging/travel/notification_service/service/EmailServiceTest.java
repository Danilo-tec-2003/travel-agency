package com.messaging.travel.notification_service.service;

import com.messaging.travel.notification_service.config.MailProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void shouldNotSendEmailWhenMailIsDisabled() {
        MailProperties mailProperties = new MailProperties(false, "no-reply@travel-agency.local");
        EmailService emailService = new EmailService(javaMailSender, mailProperties);

        emailService.send("customer@email.com", "Travel booking confirmed", "Email body");

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldSendEmailWhenMailIsEnabled() {
        MailProperties mailProperties = new MailProperties(true, "no-reply@travel-agency.local");
        EmailService emailService = new EmailService(javaMailSender, mailProperties);

        emailService.send("customer@email.com", "Travel booking confirmed", "Email body");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getFrom()).isEqualTo("no-reply@travel-agency.local");
        assertThat(message.getTo()).containsExactly("customer@email.com");
        assertThat(message.getSubject()).isEqualTo("Travel booking confirmed");
        assertThat(message.getText()).isEqualTo("Email body");
    }
}
