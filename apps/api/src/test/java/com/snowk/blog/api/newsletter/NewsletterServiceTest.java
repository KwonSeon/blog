package com.snowk.blog.api.newsletter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.*;
import java.time.*;
import java.util.*;

class NewsletterServiceTest {
    static class Time extends Clock {
        Instant now = Instant.parse("2026-11-14T00:00:00Z");

        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        public Clock withZone(ZoneId z) {
            return this;
        }

        public Instant instant() {
            return now;
        }

        void add(long s) {
            now = now.plusSeconds(s);
        }
    }

    JdbcTemplate db;
    TransactionTemplate tx;
    Time time;
    NewsletterService service;
    NewsletterCrypto crypto;
    NewsletterDeliveryService delivery;
    NewsletterGateway gateway;

    @BeforeEach
    void setup() throws Exception {
        var ds =
                new DriverManagerDataSource(
                        "jdbc:h2:mem:"
                                + UUID.randomUUID()
                                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                        "sa",
                        "");
        db = new JdbcTemplate(ds);
        tx = new TransactionTemplate(new DataSourceTransactionManager(ds));
        time = new Time();
        db.execute(
                "CREATE TABLE posts(post_id BIGINT PRIMARY KEY,title VARCHAR(200),excerpt TEXT,slug"
                    + " VARCHAR(150),visibility VARCHAR(20),status VARCHAR(20))");
        String ddl =
                Files.readString(Path.of("../../infra/compose/migrations/blog/V7__newsletter.sql"))
                        .replace(" CHARACTER SET ascii COLLATE ascii_bin", "")
                        .replace(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4", "");
        for (String sql : ddl.split(";")) if (!sql.isBlank()) db.execute(sql);
        var settings =
                new NewsletterSettings(
                        true,
                        "test-secret-with-at-least-32-characters",
                        "https://blog.example.test",
                        "blog@example.test",
                        "mailpit",
                        "",
                        "http://mailpit:8025");
        crypto = new NewsletterCrypto(settings);
        service = new NewsletterService(db, settings, crypto, time);
        gateway = mock(NewsletterGateway.class);
        delivery = new NewsletterDeliveryService(db, settings, crypto, service, gateway, time);
    }

    void run(Runnable action) {
        tx.executeWithoutResult(s -> action.run());
    }

    void subscribe() {
        run(() -> service.subscribe("reader@example.test", "a".repeat(64), true));
    }

    String token(String action) {
        String body =
                crypto.open(
                        db.queryForObject(
                                "SELECT payload_cipher FROM newsletter_outbox WHERE"
                                    + " message_type='CONFIRM' ORDER BY created_at DESC LIMIT 1",
                                String.class));
        return body.split("/subscribe/" + action + "#token=")[1].substring(0, 43);
    }

    String status() {
        return db.queryForObject("SELECT status FROM newsletter_subscribers", String.class);
    }

    int jobs() {
        return db.queryForObject("SELECT COUNT(*) FROM newsletter_outbox", Integer.class);
    }

    void campaign(long id, String visibility) {
        db.update(
                "INSERT INTO posts VALUES(?, 'Title','Excerpt',? ,?,'PUBLISHED')",
                id,
                "post-" + id,
                visibility);
        db.update(
                "INSERT INTO newsletter_campaigns(post_id,created_at) VALUES(?,?)",
                id,
                LocalDateTime.now(time));
    }

    @Test
    void doubleOptInAndIdempotentUnsubscribeEraseAddress() {
        subscribe();
        assertEquals("PENDING", status());
        String confirm = token("confirm"), stop = token("unsubscribe");
        assertFalse(
                db.queryForObject("SELECT payload_cipher FROM newsletter_outbox", String.class)
                        .contains(confirm));
        run(() -> service.confirm(confirm));
        run(() -> service.confirm(confirm));
        assertEquals("ACTIVE", status());
        run(() -> service.unsubscribe(stop));
        run(() -> service.unsubscribe(stop));
        assertEquals("UNSUBSCRIBED", status());
        assertNull(db.queryForObject("SELECT email FROM newsletter_subscribers", String.class));
        assertThrows(RuntimeException.class, () -> run(() -> service.confirm(confirm)));
    }

    @Test
    void invalidExpiredAndMissingConsentRejected() {
        assertThrows(
                RuntimeException.class,
                () -> run(() -> service.subscribe("reader@example.test", "a".repeat(64), false)));
        subscribe();
        String t = token("confirm");
        time.add(86400);
        assertThrows(RuntimeException.class, () -> run(() -> service.confirm(t)));
        assertThrows(RuntimeException.class, () -> run(() -> service.confirm("x".repeat(43))));
    }

    @Test
    void cooldownRateAndActiveSubscriberDoNotSendAgain() {
        subscribe();
        subscribe();
        assertEquals(1, jobs());
        time.add(61);
        subscribe();
        assertEquals(2, jobs());
        time.add(61);
        subscribe();
        assertEquals(2, jobs());
        run(() -> service.confirm(token("confirm")));
        time.add(86400);
        subscribe();
        assertEquals(2, jobs());
    }

    @Test
    void expiryPrunesPlaintext() {
        subscribe();
        time.add(172801);
        run(service::prune);
        assertEquals("EXPIRED", status());
        assertNull(db.queryForObject("SELECT email FROM newsletter_subscribers", String.class));
    }

    @Test
    void noDeliveryWithoutMatchingStateAndGeneration() throws Exception {
        subscribe();
        String stop = token("unsubscribe");
        run(() -> service.unsubscribe(stop));
        run(delivery::deliverOne);
        verifyNoInteractions(gateway);
        assertEquals(
                "CANCELLED",
                db.queryForObject("SELECT status FROM newsletter_outbox", String.class));
    }

    @Test
    void deliverySuccessClearsEncryptedPayload() throws Exception {
        subscribe();
        run(delivery::deliverOne);
        verify(gateway)
                .send(
                        anyString(),
                        eq("reader@example.test"),
                        anyString(),
                        anyString(),
                        anyString());
        assertEquals(
                "SENT", db.queryForObject("SELECT status FROM newsletter_outbox", String.class));
        assertNull(db.queryForObject("SELECT payload_cipher FROM newsletter_outbox", String.class));
        run(delivery::deliverOne);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void retryUsesSameMessageIdAndStopsAfterWindow() throws Exception {
        subscribe();
        doThrow(new java.io.IOException())
                .when(gateway)
                .send(anyString(), anyString(), anyString(), anyString(), anyString());
        run(delivery::deliverOne);
        assertEquals(1, db.queryForObject("SELECT attempts FROM newsletter_outbox", Integer.class));
        String id = db.queryForObject("SELECT message_id FROM newsletter_outbox", String.class);
        time.add(31);
        run(delivery::deliverOne);
        verify(gateway, times(2)).send(eq(id), anyString(), anyString(), anyString(), anyString());
        time.add(23 * 3600);
        run(delivery::deliverOne);
        assertEquals(
                "FAILED", db.queryForObject("SELECT status FROM newsletter_outbox", String.class));
    }

    @Test
    void campaignsOnlyTargetAlreadyConfirmedReadersAndDoNotDuplicate() {
        subscribe();
        campaign(1, "PUBLIC");
        time.add(1);
        run(() -> service.confirm(token("confirm")));
        run(delivery::expandOneCampaign);
        assertEquals(1, jobs());
        campaign(2, "PUBLIC");
        run(delivery::expandOneCampaign);
        run(delivery::expandOneCampaign);
        assertEquals(2, jobs());
    }

    @Test
    void withdrawnCampaignCannotBlockLaterPublication() {
        subscribe();
        run(() -> service.confirm(token("confirm")));
        campaign(1, "PRIVATE");
        time.add(1);
        campaign(2, "PUBLIC");
        run(delivery::expandOneCampaign);
        run(delivery::expandOneCampaign);
        assertEquals(2, jobs());
        assertEquals(
                2L,
                db.queryForObject(
                        "SELECT post_id FROM newsletter_outbox WHERE message_type='POST'",
                        Long.class));
    }

    @Test
    void withdrawnPostCancelsQueuedMail() throws Exception {
        subscribe();
        run(() -> service.confirm(token("confirm")));
        campaign(1, "PUBLIC");
        run(delivery::expandOneCampaign);
        db.update("UPDATE posts SET visibility='PRIVATE'");
        run(delivery::deliverOne);
        run(delivery::deliverOne);
        verifyNoInteractions(gateway);
        assertEquals(
                2,
                db.queryForObject(
                        "SELECT COUNT(*) FROM newsletter_outbox WHERE status='CANCELLED'",
                        Integer.class));
    }

    @Test
    void encryptedPayloadRejectsTamperingAndSignatureBindsAction() {
        String value = crypto.seal("secret");
        assertEquals("secret", crypto.open(value));
        byte[] bytes = Base64.getDecoder().decode(value);
        bytes[15] ^= 1;
        assertThrows(
                IllegalStateException.class,
                () -> crypto.open(Base64.getEncoder().encodeToString(bytes)));
        long stamp = Instant.now().getEpochSecond();
        String data = "{}",
                client = "a".repeat(64),
                sig = crypto.hmac("subscribe\n" + stamp + "\n" + client + "\n" + data);
        crypto.verify("subscribe", data, client, stamp, sig);
        assertThrows(
                RuntimeException.class,
                () -> crypto.verify("unsubscribe", data, client, stamp, sig));
        assertThrows(
                RuntimeException.class,
                () -> crypto.verify("subscribe", data, client, stamp - 130, sig));
    }
}
