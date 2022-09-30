package com.example.tattooartistbackend.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender emailSender;
    @Value("spring.mail.username")
    private String from;

    public void sendSimpleMessage(String to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("TattooArtist Application Notification");
        message.setText(text);
        emailSender.send(message);
    }
}
