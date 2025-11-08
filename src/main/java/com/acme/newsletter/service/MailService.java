package com.acme.newsletter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Handles sending of emails through configured SMTP service.
 * Uses Spring Boot's JavaMailSender abstraction.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    /**
     * Sends a plain text newsletter email.
     *
     * @param to      Recipient email address
     * @param subject Email subject (usually the content title)
     * @param body    Email message body (the newsletter text)
     */
    public void sendNewsletterEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("no-reply@newsletter-service.com"); // Optional: customize sender name

            mailSender.send(message);
            log.info("Email sent successfully to {}", to);

        } catch (MailException ex) {
            log.error(" Failed to send email to {}: {}", to, ex.getMessage());
            throw ex;
        }
    }
}
