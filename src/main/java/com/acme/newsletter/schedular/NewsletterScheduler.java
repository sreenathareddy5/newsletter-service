package com.acme.newsletter.schedular;

import com.acme.newsletter.model.Content;
import com.acme.newsletter.model.Subscription;
import com.acme.newsletter.repository.ContentRepository;
import com.acme.newsletter.repository.SubscriptionRepository;
import com.acme.newsletter.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsletterScheduler {

    private final ContentRepository contentRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final MailService mailService;
    private final ThreadPoolTaskExecutor taskExecutor; // Thread pool for async email sending

    private static final int PAGE_SIZE = 1000; // Batch size

    @Scheduled(fixedRateString = "${newsletter.scheduler-interval-seconds:60}000")
    public void processPendingNewsletters() {
        OffsetDateTime now = OffsetDateTime.now();
        log.info("Starting processPendingNewsletters at {}", now);

        var pendingContents = contentRepo.findByStatusAndScheduledTimeBefore(Content.Status.PENDING, now);
        log.info("Found {} pending content(s)", pendingContents.size());

        for (Content content : pendingContents) {
            log.info("Processing Content ID: {}, Title: {}", content.getId(), content.getTitle());

            int page = 0;
            CountDownLatch latch = new CountDownLatch(1); // Optional: track async completion

            while (true) {
                Page<Subscription> subscribersPage = subscriptionRepo
                        .findByTopicAndActiveTrue(content.getTopic(), PageRequest.of(page, PAGE_SIZE));

                if (subscribersPage.isEmpty()) break;

                for (Subscription sub : subscribersPage.getContent()) {
                    taskExecutor.submit(() -> {
                        try {
                            String emailBody = """
                                    Hi %s,

                                    Read the full content here: %s
                                    """.formatted(
                                    sub.getSubscriber().getEmail(),
                                    "http://localhost:8080/api/content/" + content.getId()
                            );


                            mailService.sendNewsletterEmail(
                                    sub.getSubscriber().getEmail(),
                                    content.getTitle(),
                                    emailBody
                            );
                        } catch (Exception e) {
                            log.error("Failed to send email to {}: {}", sub.getSubscriber().getEmail(), e.getMessage(), e);
                        }
                    });
                }

                page++;
            }

            // Mark content as SENT after scheduling all tasks (not after actual sending)
            content.setStatus(Content.Status.SENT);
            content.setSentAt(OffsetDateTime.now());
            contentRepo.save(content);
            log.info("Content ID: {} marked as SENT", content.getId());
        }

        log.info("processPendingNewsletters finished at {}", OffsetDateTime.now());
    }
}
