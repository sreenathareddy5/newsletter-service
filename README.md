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
      1)
      ./mvnw spring-boot:run \
     --Dspring-boot.run.arguments="--spring.mail.username=your_email@gmail.com \
    --spring.mail.password=your_app_password \
    --spring.datasource.username=db_user \
    --spring.datasource.password=db_pass"

    2) IntelliJ VM Option 
     -Dspring.mail.username=<> -Dspring.mail.password=<> -Dspring.datasource.username=<> -Dspring.datasource.password=<>


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


## Local App Logs 


    2025-11-09T21:55:19.247+05:30  INFO 80721 --- [           main] c.acme.newsletter.NewsletterApplication  : Starting NewsletterApplication using Java 19.0.1 with PID 80721 (/Users/sreenathareddy/newstellet/newsletter-service/target/classes started by sreenathareddy in /Users/sreenathareddy/newstellet/newsletter-service)
    2025-11-09T21:55:19.252+05:30  INFO 80721 --- [           main] c.acme.newsletter.NewsletterApplication  : The following 1 profile is active: "dev"
    2025-11-09T21:55:21.101+05:30  INFO 80721 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
    2025-11-09T21:55:21.197+05:30  INFO 80721 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 89 ms. Found 4 JPA repository interfaces.
    2025-11-09T21:55:22.324+05:30  INFO 80721 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
    2025-11-09T21:55:22.336+05:30  INFO 80721 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2025-11-09T21:55:22.336+05:30  INFO 80721 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.16]
    2025-11-09T21:55:22.427+05:30  INFO 80721 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2025-11-09T21:55:22.433+05:30  INFO 80721 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 3061 ms
    2025-11-09T21:55:22.661+05:30  INFO 80721 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
    2025-11-09T21:55:22.861+05:30  INFO 80721 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@24c685e7
    2025-11-09T21:55:22.878+05:30  INFO 80721 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
    2025-11-09T21:55:23.217+05:30  INFO 80721 --- [           main] liquibase.database                       : Set default schema name to public
    2025-11-09T21:55:23.776+05:30  INFO 80721 --- [           main] liquibase.changelog                      : Reading from public.databasechangelog
    Database is up to date, no changesets to execute
    2025-11-09T21:55:23.843+05:30  INFO 80721 --- [           main] liquibase.changelog                      : Reading from public.databasechangelog
    2025-11-09T21:55:23.854+05:30  INFO 80721 --- [           main] liquibase.util                           : UPDATE SUMMARY
    2025-11-09T21:55:23.854+05:30  INFO 80721 --- [           main] liquibase.util                           : Run:                          0
    2025-11-09T21:55:23.854+05:30  INFO 80721 --- [           main] liquibase.util                           : Previously run:               1
    2025-11-09T21:55:23.854+05:30  INFO 80721 --- [           main] liquibase.util                           : Filtered out:                 0
    2025-11-09T21:55:23.854+05:30  INFO 80721 --- [           main] liquibase.util                           : -------------------------------
    2025-11-09T21:55:23.854+05:30  INFO 80721 --- [           main] liquibase.util                           : Total change sets:            1
    2025-11-09T21:55:23.854+05:30  INFO 80721 --- [           main] liquibase.util                           : Update summary generated
    2025-11-09T21:55:23.877+05:30  INFO 80721 --- [           main] liquibase.lockservice                    : Successfully released change log lock
    2025-11-09T21:55:23.883+05:30  INFO 80721 --- [           main] liquibase.command                        : Command execution complete
    2025-11-09T21:55:24.047+05:30  INFO 80721 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
    2025-11-09T21:55:24.099+05:30  INFO 80721 --- [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.3.1.Final
    2025-11-09T21:55:24.124+05:30  INFO 80721 --- [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
    2025-11-09T21:55:24.295+05:30  INFO 80721 --- [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
    2025-11-09T21:55:25.485+05:30  INFO 80721 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
    2025-11-09T21:55:25.492+05:30  INFO 80721 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
    2025-11-09T21:55:26.000+05:30  INFO 80721 --- [           main] o.s.d.j.r.query.QueryEnhancerFactory     : Hibernate is in classpath; If applicable, HQL parser will be used.
    2025-11-09T21:55:27.067+05:30  WARN 80721 --- [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
    2025-11-09T21:55:27.982+05:30  INFO 80721 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path ''
    2025-11-09T21:55:28.022+05:30  INFO 80721 --- [           main] c.acme.newsletter.NewsletterApplication  : Started NewsletterApplication in 9.357 seconds (process running for 10.25)
    2025-11-09T21:55:28.025+05:30  INFO 80721 --- [   scheduling-1] c.a.n.schedular.NewsletterScheduler      : Starting processPendingNewsletters at 2025-11-09T21:55:28.025194+05:30
    2025-11-09T21:55:28.315+05:30  INFO 80721 --- [   scheduling-1] c.a.n.schedular.NewsletterScheduler      : Found 2 pending content(s)
    2025-11-09T21:55:28.315+05:30  INFO 80721 --- [   scheduling-1] c.a.n.schedular.NewsletterScheduler      : Processing Content ID: feb8be3b-d69b-44ba-8655-c0f8cb1b5049, Title: Java 21 Released
    2025-11-09T21:55:28.486+05:30  INFO 80721 --- [   scheduling-1] c.a.n.schedular.NewsletterScheduler      : Content ID: feb8be3b-d69b-44ba-8655-c0f8cb1b5049 marked as SENT
    2025-11-09T21:55:28.486+05:30  INFO 80721 --- [   scheduling-1] c.a.n.schedular.NewsletterScheduler      : Processing Content ID: 056e5069-79cb-4b38-b5e0-00755e102ba1, Title: Midnight Update
    2025-11-09T21:55:28.503+05:30  INFO 80721 --- [   scheduling-1] c.a.n.schedular.NewsletterScheduler      : Content ID: 056e5069-79cb-4b38-b5e0-00755e102ba1 marked as SENT
    2025-11-09T21:55:28.503+05:30  INFO 80721 --- [   scheduling-1] c.a.n.schedular.NewsletterScheduler      : processPendingNewsletters finished at 2025-11-09T21:55:28.503412+05:30 


    