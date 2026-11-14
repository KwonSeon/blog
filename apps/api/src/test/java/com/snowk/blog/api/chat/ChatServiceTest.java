package com.snowk.blog.api.chat;

import static org.junit.jupiter.api.Assertions.*;

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

class ChatServiceTest {
    static class Time extends Clock {
        Instant at = Instant.now();

        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        public Clock withZone(ZoneId zone) {
            return this;
        }

        public Instant instant() {
            return at;
        }

        void advance(long seconds) {
            at = at.plusSeconds(seconds);
        }
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
        Time time() {
            return new Time();
        }

        @Bean
        ChatService chat(JdbcTemplate jdbc, ApplicationEventPublisher events, Time time) {
            return new ChatService(jdbc, new ChatSettings(true, "https://chat.test"), events, time);
        }
    }

    AnnotationConfigApplicationContext context;
    JdbcTemplate jdbc;
    ChatService chat;
    Time clock;
    TransactionTemplate tx;

    @BeforeEach
    void setup() throws Exception {
        context = new AnnotationConfigApplicationContext(Config.class);
        jdbc = context.getBean(JdbcTemplate.class);
        chat = context.getBean(ChatService.class);
        clock = context.getBean(Time.class);
        tx = new TransactionTemplate(context.getBean(PlatformTransactionManager.class));
        String ddl =
                Files.readString(
                                Path.of("../../infra/compose/migrations/blog/V11__member_chat.sql"))
                        .replace(" CHARACTER SET ascii COLLATE ascii_bin", "")
                        .replace(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4", "");
        for (String statement : ddl.split(";")) if (!statement.isBlank()) jdbc.execute(statement);
        jdbc.execute(
                "ALTER TABLE chat_messages ALTER COLUMN message_id RESTART WITH 9007199254741000");
    }

    @AfterEach
    void close() {
        context.close();
    }

    ChatService.Item send(String subject, String body) {
        clock.advance(2);
        return chat.send(List.of(subject), "회원", body, UUID.randomUUID().toString(), false);
    }

    @Test
    void allMembersSeeMessagesButOnlyOwnerOrEditorCanDelete() {
        var item = send("10", "Hello");
        assertTrue(Long.parseLong(item.id()) > 9007199254740991L);
        assertFalse(chat.list(0, List.of("20"), false).items().getFirst().canDelete());
        assertTrue(chat.list(0, List.of("20"), true).items().getFirst().canDelete());
        assertThrows(
                BaseException.class,
                () -> chat.delete(Long.parseLong(item.id()), List.of("20"), false));
        chat.delete(Long.parseLong(item.id()), List.of("11", "10"), false);
        var deleted = chat.list(0, List.of("20"), false).items().getFirst();
        assertTrue(deleted.deleted());
        assertEquals("", deleted.body());
        assertEquals("", jdbc.queryForObject("SELECT body FROM chat_messages", String.class));
        assertEquals(
                "", jdbc.queryForObject("SELECT display_name FROM chat_messages", String.class));
    }

    @Test
    void editorCanModerateAndDeleteIsIdempotent() {
        var item = send("10", "Moderate");
        chat.delete(Long.parseLong(item.id()), List.of("20"), true);
        chat.delete(Long.parseLong(item.id()), List.of("20"), true);
        assertTrue(chat.list(0, List.of("10"), false).items().getFirst().deleted());
    }

    @Test
    void retryKeepsOneMessageAndChangedBodyIsRejected() {
        String key = UUID.randomUUID().toString();
        var first = chat.send(List.of("10"), "Member", "Retry", key, false);
        assertEquals(first.id(), chat.send(List.of("10"), "Member", "Retry", key, false).id());
        assertThrows(
                BaseException.class,
                () -> chat.send(List.of("10"), "Member", "Changed", key, false));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM chat_messages", Integer.class));
    }

    @Test
    void requestAndRateBudgetRollBackTogether() {
        assertThrows(
                IllegalStateException.class,
                () ->
                        tx.executeWithoutResult(
                                s -> {
                                    send("10", "Rollback");
                                    throw new IllegalStateException();
                                }));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM chat_messages", Integer.class));
        assertEquals(
                0, jdbc.queryForObject("SELECT COUNT(*) FROM chat_rate_limits", Integer.class));
    }

    @Test
    void keysetPagesStayDisjointDuringNewArrivals() {
        for (int i = 0; i < 55; i++) send(Integer.toString(i), "Message " + i);
        var first = chat.list(0, List.of("10"), false);
        assertEquals(50, first.items().size());
        assertNotNull(first.olderCursor());
        send("99", "New");
        var older = chat.list(Long.parseLong(first.olderCursor()), List.of("10"), false);
        assertEquals(5, older.items().size());
        assertNull(older.olderCursor());
        assertTrue(
                Collections.disjoint(
                        first.items().stream().map(ChatService.Item::id).toList(),
                        older.items().stream().map(ChatService.Item::id).toList()));
        for (int i = 1; i < 50; i++)
            assertTrue(
                    Long.parseLong(first.items().get(i - 1).id())
                            < Long.parseLong(first.items().get(i).id()));
    }

    @Test
    void throttlesRapidMinuteAndDailyWrites() {
        send("10", "One");
        assertThrows(
                BaseException.class,
                () ->
                        chat.send(
                                List.of("10"),
                                "Member",
                                "Too soon",
                                UUID.randomUUID().toString(),
                                false));
        jdbc.update("UPDATE chat_rate_limits SET minute_count=30");
        clock.advance(2);
        assertThrows(
                BaseException.class,
                () ->
                        chat.send(
                                List.of("10"),
                                "Member",
                                "Minute",
                                UUID.randomUUID().toString(),
                                false));
        clock.advance(61);
        send("10", "Next minute");
        jdbc.update("UPDATE chat_rate_limits SET day_count=500");
        clock.advance(2);
        assertThrows(
                BaseException.class,
                () ->
                        chat.send(
                                List.of("10"),
                                "Member",
                                "Daily",
                                UUID.randomUUID().toString(),
                                false));
        clock.advance(86400);
        send("10", "Next day");
    }

    @Test
    void validatesInputAndAvoidsEmailDisplay() {
        for (String body : List.of("", "   ", "x".repeat(2001), "x\u0000"))
            assertThrows(BaseException.class, () -> send("10", body));
        assertThrows(
                BaseException.class,
                () -> chat.send(List.of("10"), "Member", "Body", "not-uuid", false));
        var item =
                chat.send(
                        List.of("10"),
                        "private@example.test",
                        "<script>alert(1)</script>",
                        UUID.randomUUID().toString(),
                        false);
        assertEquals("회원", item.author());
        assertEquals("<script>alert(1)</script>", item.body());
    }

    @Test
    void retentionHidesThenRemovesOldMessages() {
        send("10", "Old");
        clock.advance(91L * 86400);
        assertTrue(chat.list(0, List.of("10"), false).items().isEmpty());
        chat.retain();
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM chat_messages", Integer.class));
        assertEquals(
                0, jdbc.queryForObject("SELECT COUNT(*) FROM chat_rate_limits", Integer.class));
    }

    @Test
    void disabledFeatureDoesNotQueryTables() {
        var db = org.mockito.Mockito.mock(JdbcTemplate.class);
        var disabled = new ChatService(db, new ChatSettings(false, ""), event -> {}, clock);
        assertFalse(disabled.list(0, List.of("10"), false).enabled());
        disabled.retain();
        assertThrows(
                BaseException.class,
                () ->
                        disabled.send(
                                List.of("10"),
                                "Member",
                                "Body",
                                UUID.randomUUID().toString(),
                                false));
        org.mockito.Mockito.verifyNoInteractions(db);
    }
}
