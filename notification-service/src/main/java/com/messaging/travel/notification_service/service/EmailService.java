package com.messaging.travel.notification_service.service;

import com.messaging.travel.notification_service.config.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public void send(String to, String subject, String body) {
        if (!mailProperties.enabled()) {
            log.info("Email sending disabled. to={}, subject={}, body=\n{}", to, subject, body);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.from());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);

        log.info("Email sent successfully. to={}, subject={}", to, subject);
    }
}
