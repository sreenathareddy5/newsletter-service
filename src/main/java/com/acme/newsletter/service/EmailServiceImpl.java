package com.acme.newsletter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    // NOTE: In a real app, you would retrieve the 'from' address via @Value or config.
    // For this example, ensure your application.properties spring.mail.username is valid.
    private final String FROM_EMAIL = "your.sender.email@gmail.com"; 

    @Override
    public void sendMail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MailException e) {
            // Log the failure, but let the scheduler continue. 
            // The is_sent flag on Content ensures the entire batch isn't re-attempted.
            log.error("Failed to send email to {}. Error: {}", to, e.getMessage());
        }
    }
}