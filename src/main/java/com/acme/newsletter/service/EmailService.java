package com.acme.newsletter.service;

public interface EmailService {
    void sendMail(String to, String subject, String body);
}