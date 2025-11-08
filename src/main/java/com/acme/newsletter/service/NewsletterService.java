package com.acme.newsletter.service;

import com.acme.newsletter.model.Content;
import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.repository.ContentRepository;
import com.acme.newsletter.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that processes newsletter delivery — both scheduled and manual.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {

    private final ContentRepository contentRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final MailService mailService;

    /**
     * Sends all pending newsletters whose scheduled time has already passed.
     *
     * @return Map with sent and failed counts for reporting
     */
    public Map<String, Object> sendDueNewsletters() {
        int sentCount = 0;
        int failedCount = 0;

        // Step 1: Find all pending newsletters that are due
        List<Content> dueContents = contentRepo.findByStatusAndScheduledTimeBefore(
                Content.Status.PENDING, OffsetDateTime.now(ZoneOffset.UTC)
        );

        log.info("Found {} pending newsletters to send", dueContents.size());

        // Step 2: Send to all active subscribers of each topic
        for (Content content : dueContents) {
            try {
                List<Subscription> subs = subscriptionRepo.findByTopicAndActiveTrue(content.getTopic());

                for (Subscription sub : subs) {
                    mailService.sendNewsletterEmail(
                            sub.getSubscriber().getEmail(),
                            content.getTitle(),
                            content.getBody()
                    );
                    sentCount++;
                }

                // Mark content as SENT
                content.setStatus(Content.Status.SENT);
                content.setSentAt(OffsetDateTime.now());
                contentRepo.save(content);
                log.info(" Sent newsletter '{}'", content.getTitle());

            } catch (Exception e) {
                failedCount++;
                content.setStatus(Content.Status.FAILED);
                contentRepo.save(content);
                log.error("Failed to send '{}': {}", content.getTitle(), e.getMessage());
            }
        }

        // Step 3: Return a summary result
        Map<String, Object> result = new HashMap<>();
        result.put("sentCount", sentCount);
        result.put("failedCount", failedCount);

        return result;
    }
}
