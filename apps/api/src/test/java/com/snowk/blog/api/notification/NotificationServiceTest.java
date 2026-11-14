package com.snowk.blog.api.notification;

import static org.junit.jupiter.api.Assertions.*;

import com.snowk.blog.api.comment.CommentService;
import com.snowk.blog.api.global.exception.BaseException;

import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationEventPublisher;
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

class NotificationServiceTest {
    static class Time extends Clock {
        Instant now = Instant.now();

        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        public Clock withZone(ZoneId zone) {
            return this;
        }

        public Instant instant() {
            return now;
        }

        void advance() {
            now = now.plusSeconds(5);
        }
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource dataSource() {
            // Keep the DDL session alive: H2 constant-set CHECK expressions retain its comparator.
            return new SingleConnectionDataSource(
                    "jdbc:h2:mem:"
                            + UUID.randomUUID()
                            + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                    "sa",
                    "",
                    true);
        }

        @Bean
        JdbcTemplate jdbc(DataSource ds) {
            return new JdbcTemplate(ds);
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource ds) {
            return new DataSourceTransactionManager(ds);
        }

        @Bean
        Time time() {
            return new Time();
        }

        @Bean
        CommentService comments(JdbcTemplate jdbc, Time time, ApplicationEventPublisher events) {
            return new CommentService(jdbc, time, events);
        }

        @Bean
        NotificationService notifications(JdbcTemplate jdbc) {
            return new NotificationService(jdbc, "20:9002,21:9002");
        }
    }

    AnnotationConfigApplicationContext context;
    JdbcTemplate jdbc;
    CommentService comments;
    NotificationService notifications;
    Time clock;
    TransactionTemplate tx;

    @BeforeEach
    void setup() throws Exception {
        context = new AnnotationConfigApplicationContext(Config.class);
        jdbc = context.getBean(JdbcTemplate.class);
        comments = context.getBean(CommentService.class);
        notifications = context.getBean(NotificationService.class);
        clock = context.getBean(Time.class);
        tx = new TransactionTemplate(context.getBean(PlatformTransactionManager.class));
        jdbc.execute(
                "CREATE TABLE posts(post_id BIGINT PRIMARY KEY,slug VARCHAR(150),title"
                        + " VARCHAR(200),visibility VARCHAR(20),status VARCHAR(20),author_user_id"
                        + " BIGINT)");
        jdbc.execute(
                "CREATE TABLE projects(project_id BIGINT PRIMARY KEY,slug VARCHAR(150),title"
                        + " VARCHAR(200),visibility VARCHAR(20),status VARCHAR(20))");
        jdbc.update("INSERT INTO posts VALUES(1,'sample','Sample','PUBLIC','PUBLISHED',9002)");
        jdbc.update("INSERT INTO projects VALUES(1,'project','Project','PUBLIC','ACTIVE')");
        for (String name : List.of("V6__comments.sql", "V9__notifications.sql")) {
            String ddl =
                    Files.readString(Path.of("../../infra/compose/migrations/blog", name))
                            .replace(" CHARACTER SET ascii COLLATE ascii_bin", "")
                            .replace(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4", "");
            for (String statement : ddl.split(";"))
                if (!statement.isBlank()) jdbc.execute(statement);
        }
        jdbc.execute(
                "ALTER TABLE notifications ALTER COLUMN notification_id RESTART WITH"
                        + " 9007199254741000");
    }

    @AfterEach
    void close() {
        context.close();
    }

    CommentService.Item create(
            String kind, List<String> actor, Long parent, String text, String key) {
        clock.advance();
        return comments.create(kind, 1, actor, "Member", text, parent, key);
    }

    CommentService.Item create(String actor, Long parent, String text) {
        return create("posts", List.of(actor), parent, text, UUID.randomUUID().toString());
    }

    @Test
    void realSubscriberCreatesOwnerAndReplyNotificationsWithoutSelfNotifications() {
        var parent = create("10", null, "Parent");
        create("11", Long.valueOf(parent.id()), "Reply");
        var page = notifications.list(List.of("10"), 0);
        assertEquals(1, page.unreadCount());
        assertEquals("REPLY", page.items().getFirst().kind());
        assertTrue(Long.parseLong(page.items().getFirst().id()) > 9007199254740991L);
        assertEquals(2, notifications.unread(List.of("20")));
        assertEquals(0, notifications.unread(List.of("11")));
        assertTrue(page.items().getFirst().path().startsWith("/posts/sample#comment-"));
    }

    @Test
    void aliasesSuppressSelfRepliesAndCanReadTheirOwnOldInbox() {
        var parent = create("10", null, "Parent");
        create(
                "posts",
                List.of("11", "10"),
                Long.valueOf(parent.id()),
                "Own alias reply",
                UUID.randomUUID().toString());
        assertEquals(0, notifications.unread(List.of("10")));
        create("12", Long.valueOf(parent.id()), "Another member");
        var item = notifications.list(List.of("99", "10"), 0).items().getFirst();
        assertThrows(
                BaseException.class,
                () -> notifications.read(List.of("12"), Long.parseLong(item.id())));
        notifications.read(List.of("99", "10"), Long.parseLong(item.id()));
        assertEquals(0, notifications.unread(List.of("10")));
    }

    @Test
    void replayAndRepeatedDomainEventDoNotDuplicateNotifications() {
        String key = UUID.randomUUID().toString();
        var item = create("posts", List.of("10"), null, "Replay", key);
        create("posts", List.of("10"), null, "Replay", key);
        tx.executeWithoutResult(
                status ->
                        context.publishEvent(
                                new CommentCreated(Long.parseLong(item.id()), List.of("10"))));
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM notifications", Integer.class));
    }

    @Test
    void commentAndNotificationRollbackTogether() {
        assertThrows(
                IllegalStateException.class,
                () ->
                        tx.executeWithoutResult(
                                status -> {
                                    create("10", null, "Rolled back");
                                    throw new IllegalStateException("rollback");
                                }));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM comments", Integer.class));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM notifications", Integer.class));
    }

    @Test
    void privateDraftDeletedAndExpiredNotificationsAreInvisible() {
        var item = create("10", null, "Visible");
        assertEquals(1, notifications.unread(List.of("20")));
        jdbc.update("UPDATE posts SET visibility='PRIVATE'");
        assertTrue(notifications.list(List.of("20"), 0).items().isEmpty());
        jdbc.update("UPDATE posts SET visibility='PUBLIC',status='DRAFT'");
        assertEquals(0, notifications.unread(List.of("20")));
        jdbc.update("UPDATE posts SET status='PUBLISHED'");
        comments.delete("posts", 1, Long.parseLong(item.id()), List.of("10"), false);
        assertEquals(0, notifications.unread(List.of("20")));
        create("11", null, "Old");
        jdbc.update("UPDATE notifications SET created_at=?", LocalDateTime.now().minusDays(91));
        assertTrue(notifications.list(List.of("20"), 0).items().isEmpty());
    }

    @Test
    void keysetPaginationAndReadSnapshotDoNotSwallowNewArrivals() {
        var parent = create("10", null, "Parent");
        for (int i = 0; i < 55; i++)
            create(Integer.toString(100 + i), Long.parseLong(parent.id()), "Reply " + i);
        var first = notifications.list(List.of("10"), 0);
        assertEquals(50, first.items().size());
        assertNotNull(first.nextCursor());
        var second = notifications.list(List.of("10"), Long.parseLong(first.nextCursor()));
        assertEquals(5, second.items().size());
        assertNull(second.nextCursor());
        assertTrue(
                Collections.disjoint(
                        first.items().stream().map(NotificationService.Item::id).toList(),
                        second.items().stream().map(NotificationService.Item::id).toList()));
        create("999", Long.parseLong(parent.id()), "New arrival");
        notifications.readAll(List.of("10"), Long.parseLong(first.snapshotId()));
        assertEquals(1, notifications.unread(List.of("10")));
    }

    @Test
    void sharedProjectsNotifyOnlyBoundEditorsAndCascadeOnRemoval() {
        create("projects", List.of("20"), null, "Project feedback", UUID.randomUUID().toString());
        assertEquals(0, notifications.unread(List.of("20")));
        assertEquals(1, notifications.unread(List.of("21")));
        assertEquals(0, notifications.unread(List.of("9002")));
        jdbc.update("DELETE FROM projects WHERE project_id=1");
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM notifications", Integer.class));
    }

    @Test
    void deletedParentDoesNotReceiveNewReply() {
        var parent = create("10", null, "Parent");
        comments.delete("posts", 1, Long.parseLong(parent.id()), List.of("10"), false);
        create("11", Long.parseLong(parent.id()), "Reply");
        assertEquals(0, notifications.unread(List.of("10")));
        assertEquals(1, notifications.unread(List.of("20")));
    }

    @Test
    void constraintsAndTransactionBoundaryAreEnforced() {
        var item = create("10", null, "Boundary");
        assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () ->
                        jdbc.update(
                                "INSERT INTO"
                                    + " notifications(identity_subject,comment_id,kind,created_at)"
                                    + " VALUES('999',?,'INVALID',?)",
                                Long.parseLong(item.id()),
                                LocalDateTime.now()));
        assertThrows(
                org.springframework.transaction.IllegalTransactionStateException.class,
                () ->
                        context.publishEvent(
                                new CommentCreated(Long.parseLong(item.id()), List.of("10"))));
        assertThrows(BaseException.class, () -> notifications.list(List.of(), 0));
        assertThrows(BaseException.class, () -> notifications.list(List.of("10"), -1));
    }
}
