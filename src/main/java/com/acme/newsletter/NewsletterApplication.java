package com.acme.newsletter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main entry point for the Spring Boot Newsletter Service.
 */
@SpringBootApplication
@EnableScheduling // REQUIRED: This annotation activates the scheduled task runner
public class NewsletterApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsletterApplication.class, args);
    }
}