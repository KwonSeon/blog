package com.snowk.blog.api.notification.telegram;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.snowk.blog.api.notification.*;

import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.*;
import java.time.*;
import java.util.*;

import javax.sql.DataSource;

class TelegramDeliveryTest {
    static TelegramSettings settings() {
        return new TelegramSettings(
                true, "123456:synthetic_token_for_tests_only", "20:1234567", "https://blog.test");
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource source() {
            return new SingleConnectionDataSource(
                    "jdbc:h2:mem:"
                            + UUID.randomUUID()
                            + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                    "sa",
                    "",
                    true);
        }

        @Bean
        JdbcTemplate jdbc(DataSource source) {
            return new JdbcTemplate(source);
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource source) {
            return new DataSourceTransactionManager(source);
        }

        @Bean
        TelegramSettings settings() {
            return TelegramDeliveryTest.settings();
        }

        @Bean
        TelegramGateway gateway() {
            return mock(TelegramGateway.class);
        }

        @Bean
        TelegramDelivery delivery(
                JdbcTemplate jdbc,
                TelegramSettings settings,
                TelegramGateway gateway,
                PlatformTransactionManager manager) {
            return new TelegramDelivery(jdbc, settings, gateway, manager);
        }

        @Bean
        NotificationService notifications(JdbcTemplate jdbc) {
            return new NotificationService(jdbc, "20:9002,21:9002");
        }
    }

    AnnotationConfigApplicationContext context;
    JdbcTemplate jdbc;
    TelegramDelivery delivery;
    TelegramGateway gateway;
    TransactionTemplate tx;

    @BeforeEach
    void setup() throws Exception {
        context = new AnnotationConfigApplicationContext(Config.class);
        jdbc = context.getBean(JdbcTemplate.class);
        delivery = context.getBean(TelegramDelivery.class);
        gateway = context.getBean(TelegramGateway.class);
        tx = new TransactionTemplate(context.getBean(PlatformTransactionManager.class));
        jdbc.execute(
                "CREATE TABLE posts(post_id BIGINT PRIMARY KEY,visibility VARCHAR(20),status"
                        + " VARCHAR(20),author_user_id BIGINT)");
        jdbc.execute(
                "CREATE TABLE projects(project_id BIGINT PRIMARY KEY,visibility VARCHAR(20),status"
                        + " VARCHAR(20))");
        jdbc.update("INSERT INTO posts VALUES(1,'PUBLIC','PUBLISHED',9002)");
        for (String name :
                List.of(
                        "V6__comments.sql",
                        "V9__notifications.sql",
                        "V10__external_notification_delivery.sql")) {
            String ddl =
                    Files.readString(Path.of("../../infra/compose/migrations/blog", name))
                            .replace(" CHARACTER SET ascii COLLATE ascii_bin", "")
                            .replace(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4", "");
            for (String statement : ddl.split(";"))
                if (!statement.isBlank()) jdbc.execute(statement);
        }
        when(gateway.send("20")).thenReturn(new TelegramGateway.Result("SENT", "OK", 0));
    }

    @AfterEach
    void close() {
        context.close();
    }

    void comment(long id) {
        tx.executeWithoutResult(
                s -> {
                    jdbc.update(
                            "INSERT INTO"
                                + " comments(comment_id,post_id,identity_subject,display_name,body,request_key,request_hash,created_at,updated_at)"
                                + " VALUES(?,1,'10','Member','Synthetic',?,'synthetic',?,?)",
                            id,
                            UUID.randomUUID().toString(),
                            LocalDateTime.now(ZoneOffset.UTC),
                            LocalDateTime.now(ZoneOffset.UTC));
                    context.publishEvent(new CommentCreated(id, List.of("10")));
                });
    }

    String state() {
        return jdbc.queryForObject("SELECT state FROM notification_deliveries", String.class);
    }

    int count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM notification_deliveries", Integer.class);
    }

    void ready() {
        jdbc.update(
                "UPDATE notification_delivery_control SET next_allowed_at=?",
                LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));
        jdbc.update(
                "UPDATE notification_deliveries SET next_attempt_at=?",
                LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));
    }

    @Test
    void defaultsOffAndEventsRequireActivation() {
        assertFalse(delivery.status().active());
        comment(1);
        assertEquals(0, count());
        delivery.tick();
        verifyNoInteractions(gateway);
    }

    @Test
    void actualEventOrderQueuesOnlyMappedRecipientsAndDeduplicates() {
        delivery.setActive(true);
        comment(1);
        tx.executeWithoutResult(s -> context.publishEvent(new CommentCreated(1, List.of("10"))));
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM notifications", Integer.class));
        assertEquals(1, count());
        delivery.tick();
        delivery.tick();
        assertEquals("SENT", state());
        verify(gateway, times(1)).send("20");
    }

    @Test
    void commentInboxAndQueueRollbackTogether() {
        delivery.setActive(true);
        assertThrows(
                IllegalStateException.class,
                () ->
                        tx.executeWithoutResult(
                                s -> {
                                    comment(1);
                                    throw new IllegalStateException();
                                }));
        assertEquals(0, count());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM notifications", Integer.class));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM comments", Integer.class));
    }

    @Test
    void explicitRateLimitRetriesWithBound() {
        delivery.setActive(true);
        comment(1);
        when(gateway.send("20")).thenReturn(new TelegramGateway.Result("QUEUED", "RATE_LIMIT", 30));
        delivery.tick();
        assertEquals("QUEUED", state());
        delivery.tick();
        verify(gateway, times(1)).send("20");
        for (int i = 1; i < 6; i++) {
            ready();
            delivery.tick();
        }
        assertEquals("FAILED", state());
        assertEquals(
                "RETRY_LIMIT",
                jdbc.queryForObject("SELECT last_code FROM notification_deliveries", String.class));
    }

    @Test
    void ambiguousOutcomeNeverAutomaticallyRetries() {
        delivery.setActive(true);
        comment(1);
        when(gateway.send("20"))
                .thenReturn(new TelegramGateway.Result("UNKNOWN", "TRANSPORT_OR_PARSE", 0));
        delivery.tick();
        ready();
        delivery.tick();
        assertEquals("UNKNOWN", state());
        verify(gateway, times(1)).send("20");
    }

    @Test
    void interruptedClaimIsUnknown() {
        delivery.setActive(true);
        comment(1);
        jdbc.update(
                "UPDATE notification_deliveries SET state='SENDING',claimed_at=?",
                LocalDateTime.now(ZoneOffset.UTC).minusMinutes(6));
        delivery.tick();
        assertEquals("UNKNOWN", state());
        verifyNoInteractions(gateway);
    }

    @Test
    void stopAndConfigurationRotationPreventOldWorkers() {
        delivery.setActive(true);
        comment(1);
        delivery.setActive(false);
        delivery.tick();
        assertEquals("QUEUED", state());
        delivery.setActive(true);
        jdbc.update(
                "UPDATE notification_delivery_control SET active_config_hash=?", "a".repeat(64));
        assertFalse(delivery.status().currentConfiguration());
        delivery.tick();
        verifyNoInteractions(gateway);
    }

    @Test
    void privateContentIsSkipped() {
        delivery.setActive(true);
        comment(1);
        jdbc.update("UPDATE posts SET visibility='PRIVATE'");
        delivery.tick();
        assertEquals("SKIPPED", state());
        verifyNoInteractions(gateway);
    }

    @Test
    void deletedAndExpiredContentIsSkipped() {
        delivery.setActive(true);
        comment(1);
        jdbc.update(
                "UPDATE notifications SET created_at=?",
                LocalDateTime.now(ZoneOffset.UTC).minusDays(91));
        delivery.tick();
        assertEquals("SKIPPED", state());
        verifyNoInteractions(gateway);
    }

    @Test
    void cascadeRemovesDeliveryAndInvalidStateRejected() {
        delivery.setActive(true);
        comment(1);
        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> jdbc.update("UPDATE notification_deliveries SET state='INVALID'"));
        jdbc.update("DELETE FROM posts");
        assertEquals(0, count());
    }

    @Test
    void disabledWorkerDoesNotTouchDatabase() {
        var ds = new TelegramSettings(false, "", "", "");
        var db = mock(JdbcTemplate.class);
        var worker =
                new TelegramDelivery(
                        db, ds, gateway, context.getBean(PlatformTransactionManager.class));
        worker.tick();
        worker.enqueue(new CommentCreated(1, List.of("10")));
        verifyNoInteractions(db, gateway);
    }
}
