#  Newsletter Service – Complete Backend API

A Spring Boot backend service to **create, manage, and send newsletters** by topic and schedule.  
The system includes APIs for **topics, subscribers, subscriptions, content scheduling, and delivery**.  
It supports both **automated email sending (scheduler)** and **manual triggers**.

This project fulfills the **Newsletter Service assignment** by demonstrating design, implementation, scheduling, and system design skills.

---

##  Features

 Create and manage newsletter **topics**  
 Register and manage **subscribers**  
 Map subscribers to topics (**subscriptions**)  
 Add and schedule **newsletter content**  
 Send newsletters automatically or manually  
 Fully documented REST APIs with Swagger  
 Deployable to **Heroku** with **PostgreSQL** and **SMTP mail**

---

##  Tech Stack

| Component | Technology |
|------------|-------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Database** | PostgreSQL (Heroku Postgres) |
| **Build Tool** | Maven Wrapper (`mvnw`) |
| **Deployment** | Heroku |
| **Mail Service** | Spring Mail (Jakarta Mail) |
| **Scheduler** | Spring `@Scheduled` |
| **Docs** | OpenAPI / Swagger 3 |

---

## ️ Configuration

### Mail Setup

Add the following to your `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.transport.protocol=smtp
```
---

### Database Management
    src/main/resources/db/changelog
    All database schema creation and version control are managed through Liquibase.
    Liquibase automatically applies changelog files (db.changelog-master.yaml) during application startup, ensuring consistent schema evolution across environments.
    No manual table creation or spring.jpa.hibernate.ddl-auto is required.

###  Running the Application
      ./mvnw spring-boot:run \
     --Dspring-boot.run.arguments="--spring.mail.username=your_email@gmail.com \
    --spring.mail.password=your_app_password \
    --spring.datasource.username=db_user \
    --spring.datasource.password=db_pass"

### Deploying to Heroku

    ./mvnw clean package -DskipTests
    heroku login
    heroku create newsletter-service-demo
    heroku addons:create heroku-postgresql:hobby-dev --app newsletter-service-demo
    heroku config:set SPRING_MAIL_USERNAME=your_email@gmail.com SPRING_MAIL_PASSWORD=your_app_password --app newsletter-service-demo
    heroku deploy:jar target/newsletter-service-0.0.1-SNAPSHOT.jar --app newsletter-service-demo
    heroku open --app newsletter-service-demo

### API Documentation

    http://localhost:8080/swagger-ui.html

    https://newsletter-service-demo.herokuapp.com/swagger-ui.html

### Topics (/api/topics)
    
    | Method   | Endpoint           | Description        |
    | -------- | ------------------ | ------------------ |
    | `POST`   | `/api/topics`      | Create a new topic |
    | `GET`    | `/api/topics`      | List all topics    |
    | `GET`    | `/api/topics/{id}` | Get topic by ID    |
    | `DELETE` | `/api/topics/{id}` | Delete a topic     |

### Subscribers (/api/subscribers)
    
     | Method   | Endpoint                           | Description               |
     | -------- | ---------------------------------- | ------------------------- |
     | `POST`   | `/api/subscribers`                 | Register a new subscriber |
     | `GET`    | `/api/subscribers`                 | List all subscribers      |
     | `GET`    | `/api/subscribers/topic/{topicId}` | List subscribers by topic |
     | `DELETE` | `/api/subscribers/{id}`            | Unsubscribe a user        |

### Subscriptions (/api/subscriptions)

     | Method   | Endpoint                                      | Description                 |
     | -------- | --------------------------------------------- | --------------------------- |
     | `GET`    | `/api/subscriptions`                          | List all subscriptions      |
     | `POST`   | `/api/subscriptions/{subscriberId}/{topicId}` | Subscribe a user to a topic |
     | `DELETE` | `/api/subscriptions/{subscriberId}/{topicId}` | Unsubscribe a user          |

### Content (/api/content)

     | Method   | Endpoint            | Description                   |
     | -------- | ------------------- | ----------------------------- |
     | `POST`   | `/api/content`      | Create new newsletter content |
     | `GET`    | `/api/content`      | List all content              |
     | `GET`    | `/api/content/{id}` | Get content by ID             |
     | `DELETE` | `/api/content/{id}` | Delete content                |

### Newsletter Sending (/api/newsletter)

     | Method | Endpoint                 | Description                                 |
     | ------ | ------------------------ | ------------------------------------------- |
     | `POST` | `/api/newsletter/send`   | Manually trigger sending of due newsletters |
     | `GET`  | `/api/newsletter/status` | Check scheduler health/status               |


###  System Architecture

     Topic ───▶ Content ───▶ Scheduler ───▶ MailService
     │           │             │
     │           ▼             │
     Subscriber ───▶ Subscription ─┘

     Topic → defines newsletter category

    1) Subscriber → registers with an email

    2)Subscription → links subscriber ↔ topic

    3)Content → defines newsletter text and send time

    4)NewsletterService → handles email dispatch (manual & scheduled)


## Current Architecture 

    Scheduler-Based Trigger:
    A Spring @Scheduled task runs periodically (default every 60 seconds) to identify newsletters with a PENDING status and scheduled time ≤ current time.

    Paginated Subscription Fetching:
    Subscribers are loaded in batches (PageRequest) of 1000 to avoid memory overflow.

    Asynchronous Email Sending:
    Emails are sent concurrently via a configured ThreadPoolTaskExecutor.
    Each subscriber email is processed as a separate task.

    Content Lifecycle Management:
    Once all email jobs are queued, the content is marked as SENT to prevent duplicate deliveries.

## Scalability Limitations

    | Area                           | Limitation                                    | Impact                                           |
    | ------------------------------ | --------------------------------------------- | ------------------------------------------------ |
    | **Thread Pool**                | Limited concurrent tasks                      | Task backlog and increased latency               |
    | **Single Instance Processing** | Only one scheduler node runs                  | Cannot utilize horizontal scaling                |
    | **Database Load**              | Multiple page queries per topic               | High read/write load for large subscriber counts |
    | **Email Rate Limits**          | SMTP servers (like Gmail) restrict throughput | High risk of throttling or failure               |
    | **Failure Handling**           | Failed email sends are not retried            | Partial delivery, inconsistent states            |

## Proposed Scalable Solution
    To handle 1M+ subscribers, we evolve the design to a distributed event-driven architecture powered by Kafka (or RabbitMQ/SQS).

    | Component                | Responsibility                                                                              |
    | ------------------------ | ------------------------------------------------------------------------------------------- |
    | **Newsletter Scheduler** | Scans for pending newsletters and publishes jobs (`NewsletterJob`) to Kafka.                |
    | **Kafka Topic**          | Acts as a distributed work queue (`newsletter-topic`) with high throughput and persistence. |
    | **Mail Worker(s)**       | Independent microservices consuming from Kafka and sending emails concurrently.             |
    | **Email Log DB**         | Stores delivery status for audit, retries, and analytics.                                   |


     ┌───────────────────────────┐
     │  NewsletterScheduler      │
     │  (Spring @Scheduled Job)  │
     └─────────────┬─────────────┘
     │ Publishes Email Jobs
                   ▼
     ┌────────────────────┐
     │   Kafka Topic      │
     │ (newsletter-topic) │
     └────────┬───────────┘
     │ Consumed by multiple workers
              ▼
    ┌────────────────────┐
    │   MailWorker(s)    │
    │  (Async Senders)   │
    └────────┬───────────┘
    │ Logs results
             ▼
    ┌────────────────────┐
    │   Email Log Table  │
    │ (sent / failed)    │
    └────────────────────┘


## Scaling Characteristics

    | Dimension            | Before                  | After Kafka-based Refactor                    |
    | -------------------- | ----------------------- | --------------------------------------------- |
    | **Processing Model** | In-memory async threads | Distributed message consumers                 |
    | **Throughput**       | ~5K–20K emails/min      | 100K–1M+ emails/min (depends on cluster size) |
    | **Resilience**       | Single-point scheduler  | Horizontally scalable                         |
    | **Failure Handling** | Logs errors only        | Retry, DLQ, and monitoring support            |
    | **Deployment**       | Single Spring Boot app  | Multi-service (Scheduler + Workers)           |


    