package com.snowk.blog.api.comment;

import static org.junit.jupiter.api.Assertions.*;

import com.snowk.blog.api.global.exception.BaseException;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

class CommentServiceTest {
    static class Time extends Clock {
        Instant now = Instant.parse("2026-11-14T00:00:00Z");

        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        public Clock withZone(ZoneId zone) {
            return this;
        }

        public Instant instant() {
            return now;
        }

        void add(long seconds) {
            now = now.plusSeconds(seconds);
        }
    }

    JdbcTemplate jdbc;
    CommentService service;
    TransactionTemplate tx;
    Time time;

    @BeforeEach
    void setup() {
        var ds =
                new DriverManagerDataSource(
                        "jdbc:h2:mem:"
                                + UUID.randomUUID()
                                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000",
                        "sa",
                        "");
        jdbc = new JdbcTemplate(ds);
        tx = new TransactionTemplate(new DataSourceTransactionManager(ds));
        time = new Time();
        service = new CommentService(jdbc, time);
        for (String kind : List.of("post", "project")) {
            jdbc.execute(
                    "CREATE TABLE "
                            + kind
                            + "s("
                            + kind
                            + "_id BIGINT PRIMARY KEY,visibility VARCHAR(20),status VARCHAR(20))");
            jdbc.update(
                    "INSERT INTO " + kind + "s VALUES(1,'PUBLIC',?)",
                    kind.equals("post") ? "PUBLISHED" : "ACTIVE");
        }
        jdbc.execute(
                "CREATE TABLE comments(comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,post_id"
                    + " BIGINT,project_id BIGINT,parent_id BIGINT,depth INT,identity_subject"
                    + " VARCHAR(64),display_name VARCHAR(40),body TEXT,request_key"
                    + " CHAR(36),request_hash CHAR(64),created_at TIMESTAMP,updated_at"
                    + " TIMESTAMP,deleted_at TIMESTAMP,UNIQUE(identity_subject,request_key),FOREIGN"
                    + " KEY(post_id) REFERENCES posts(post_id) ON DELETE CASCADE,FOREIGN"
                    + " KEY(project_id) REFERENCES projects(project_id) ON DELETE CASCADE,FOREIGN"
                    + " KEY(parent_id) REFERENCES comments(comment_id) ON DELETE SET NULL)");
        jdbc.execute("ALTER TABLE comments ALTER COLUMN comment_id RESTART WITH 9007199254741000");
        jdbc.execute(
                "CREATE TABLE comment_rate_limits(identity_subject VARCHAR(64) PRIMARY"
                    + " KEY,last_write TIMESTAMP,minute_start TIMESTAMP,minute_count INT,day_start"
                    + " DATE,day_count INT)");
    }

    CommentService.Item create(String kind, String subject, String text, Long parent, String key) {
        return tx.execute(
                s ->
                        service.create(
                                kind,
                                1,
                                List.of(subject),
                                "member@example.test",
                                text,
                                parent,
                                key));
    }

    CommentService.Item create(String text) {
        return create("posts", "10", text, null, UUID.randomUUID().toString());
    }

    @Test
    void ownershipAliasesModerationAndSoftDeletionPreserveReplies() {
        var a = create("parent");
        time.add(5);
        var b = create("posts", "10", "reply", Long.valueOf(a.id()), UUID.randomUUID().toString());
        assertEquals(1, b.depth());
        assertEquals(a.id(), b.parentId());
        assertEquals("회원", a.author());
        time.add(5);
        var edited =
                tx.execute(
                        s ->
                                service.edit(
                                        "posts",
                                        1,
                                        Long.parseLong(a.id()),
                                        List.of("20", "10"),
                                        "edited"));
        assertEquals("edited", edited.body());
        assertThrows(
                BaseException.class,
                () ->
                        tx.execute(
                                s ->
                                        service.edit(
                                                "posts",
                                                1,
                                                Long.parseLong(a.id()),
                                                List.of("99"),
                                                "forged")));
        assertThrows(
                BaseException.class,
                () ->
                        tx.execute(
                                s ->
                                        service.delete(
                                                "posts",
                                                1,
                                                Long.parseLong(a.id()),
                                                List.of("99"),
                                                false)));
        var removed =
                tx.execute(
                        s ->
                                service.delete(
                                        "posts", 1, Long.parseLong(a.id()), List.of("99"), true));
        assertTrue(removed.deleted());
        assertEquals("", removed.body());
        var page = service.list("posts", 1, 0, List.of(), false);
        assertEquals(2, page.items().size());
        assertEquals("reply", page.items().get(1).body());
        assertFalse(page.items().get(1).canEdit());
    }

    @Test
    void idempotentRetriesAreSerializedAndDifferentPayloadConflicts() throws Exception {
        String key = UUID.randomUUID().toString();
        try (var pool = Executors.newFixedThreadPool(8)) {
            var jobs = new ArrayList<Callable<String>>();
            for (int i = 0; i < 16; i++)
                jobs.add(() -> create("posts", "10", "same", null, key).id());
            var ids = new HashSet<String>();
            for (var f : pool.invokeAll(jobs)) ids.add(f.get());
            assertEquals(1, ids.size());
        }
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM comments", Long.class));
        assertThrows(BaseException.class, () -> create("posts", "10", "different", null, key));
    }

    @Test
    void spacingDuplicateAndMinuteBudgetsRollbackRejectedWrites() {
        create("first");
        assertThrows(BaseException.class, () -> create("too soon"));
        time.add(4);
        assertThrows(BaseException.class, () -> create("first"));
        for (int i = 0; i < 9; i++) {
            time.add(4);
            create("entry " + i);
        }
        time.add(4);
        assertThrows(BaseException.class, () -> create("over minute limit"));
        assertEquals(10, jdbc.queryForObject("SELECT COUNT(*) FROM comments", Long.class));
        time.add(61);
        create("new window");
    }

    @Test
    void dayBudgetAndNextDayReset() {
        for (int i = 0; i < 100; i++) {
            time.add(61);
            create("day entry " + i);
        }
        time.add(61);
        assertThrows(BaseException.class, () -> create("over day"));
        time.add(86400);
        assertNotNull(create("tomorrow"));
    }

    @Test
    void privateDraftCrossContentAndInvalidInputsAreRejected() {
        var p = create("parent");
        assertThrows(
                BaseException.class,
                () ->
                        create(
                                "projects",
                                "20",
                                "wrong parent",
                                Long.valueOf(p.id()),
                                UUID.randomUUID().toString()));
        jdbc.update("UPDATE posts SET visibility='PRIVATE'");
        assertThrows(BaseException.class, () -> service.list("posts", 1, 0, List.of(), false));
        assertThrows(BaseException.class, () -> create("hidden"));
        jdbc.update("UPDATE posts SET visibility='PUBLIC',status='DRAFT'");
        assertThrows(BaseException.class, () -> create("draft"));
        assertThrows(BaseException.class, () -> create(" "));
        assertThrows(BaseException.class, () -> create("x".repeat(2001)));
        assertThrows(BaseException.class, () -> create("control\u0000"));
        assertThrows(
                BaseException.class,
                () ->
                        service.create(
                                "posts",
                                1,
                                List.of(),
                                "anonymous",
                                "text",
                                null,
                                UUID.randomUUID().toString()));
    }

    @Test
    void cursorPaginationPreservesLargeIdsAndDoesNotSkip() {
        for (int i = 0; i < 55; i++) {
            time.add(61);
            create("page " + i);
        }
        var first = service.list("posts", 1, 0, List.of(), false);
        assertEquals(50, first.items().size());
        assertEquals("9007199254741000", first.items().getFirst().id());
        assertNotNull(first.nextCursor());
        var next =
                service.list("posts", 1, Long.parseLong(first.nextCursor()), List.of("10"), false);
        assertEquals(5, next.items().size());
        assertNull(next.nextCursor());
        assertTrue(next.items().getFirst().canEdit());
    }

    @Test
    void arbitraryDepthAndParentContentDeletionCascade() {
        var parent = create("root");
        jdbc.update(
                "UPDATE comments SET depth=10000 WHERE comment_id=?", Long.parseLong(parent.id()));
        time.add(5);
        var reply =
                create(
                        "posts",
                        "10",
                        "deep reply",
                        Long.valueOf(parent.id()),
                        UUID.randomUUID().toString());
        assertEquals(10001, reply.depth());
        jdbc.update("DELETE FROM posts WHERE post_id=1");
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM comments", Long.class));
    }
}
