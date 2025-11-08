package com.acme.newsletter.schedular;

import com.acme.newsletter.model.Content;
import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.repository.ContentRepository;
import com.acme.newsletter.repository.SubscriptionRepository;
import com.acme.newsletter.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsletterScheduler {

    private final ContentRepository contentRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final MailService mailService;
    private final ThreadPoolTaskExecutor taskExecutor; // Thread pool for async email sending

    private static final int PAGE_SIZE = 1000; // Batch size

    @Scheduled(fixedRate = 60000)
    public void processPendingNewsletters() {
        OffsetDateTime now = OffsetDateTime.now();
        log.info("Starting processPendingNewsletters at {}", now);

        List<Content> pendingContents = contentRepo.findByStatusAndScheduledTimeBefore(Content.Status.PENDING, now);
        log.info("Found {} pending content(s)", pendingContents.size());

        for (Content content : pendingContents) {
            log.info("Processing Content ID: {}, Title: {}", content.getId(), content.getTitle());

            int page = 0;
            while (true) {
                var subscribersPage = subscriptionRepo.findByTopicAndActiveTrue(content.getTopic(), PageRequest.of(page, PAGE_SIZE));
                if (subscribersPage.isEmpty()) break;

                // Send emails asynchronously
                for (Subscription sub : subscribersPage.getContent()) {
                    taskExecutor.submit(() -> {
                        try {
                            mailService.sendNewsletterEmail(sub.getSubscriber().getEmail(), content.getTitle(), content.getBody());
                        } catch (Exception e) {
                            log.error("Failed to send email to {}: {}", sub.getSubscriber().getEmail(), e.getMessage());
                        }
                    });
                }
                page++;
            }

            // Mark content as SENT (or FAILED if desired, but async may fail individually)
            content.setStatus(Content.Status.SENT);
            content.setSentAt(OffsetDateTime.now());
            contentRepo.save(content);
            log.info("Content ID: {} marked as SENT", content.getId());
        }
    }
}
