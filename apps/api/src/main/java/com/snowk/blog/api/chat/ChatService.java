package com.snowk.blog.api.chat;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.time.*;
import java.util.*;

@Service
public class ChatService {
    private final JdbcTemplate jdbc;
    private final ChatSettings settings;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    @Autowired
    public ChatService(JdbcTemplate jdbc, ChatSettings settings, ApplicationEventPublisher events) {
        this(jdbc, settings, events, Clock.systemUTC());
    }

    public ChatService(
            JdbcTemplate jdbc,
            ChatSettings settings,
            ApplicationEventPublisher events,
            Clock clock) {
        this.jdbc = jdbc;
        this.settings = settings;
        this.events = events;
        this.clock = clock;
    }

    public record Changed() {}

    public record Item(
            String id,
            String author,
            String body,
            String createdAt,
            boolean deleted,
            boolean canDelete) {}

    public record Page(List<Item> items, String olderCursor, boolean enabled) {}

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    private void available() {
        if (!settings.enabled) throw new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND);
    }

    private LocalDateTime date(Object value) {
        return value instanceof LocalDateTime local ? local : ((Timestamp) value).toLocalDateTime();
    }

    private Item item(Map<String, Object> row, List<String> subjects, boolean editor) {
        boolean deleted = row.get("deleted_at") != null;
        return new Item(
                row.get("message_id").toString(),
                deleted ? "삭제된 메시지" : row.get("display_name").toString(),
                deleted ? "" : row.get("body").toString(),
                date(row.get("created_at")).toInstant(ZoneOffset.UTC).toString(),
                deleted,
                !deleted && (editor || subjects.contains(row.get("identity_subject").toString())));
    }

    @Transactional(readOnly = true)
    public Page list(long before, List<String> subjects, boolean editor) {
        if (!settings.enabled) return new Page(List.of(), null, false);
        if (before < 0 || subjects.isEmpty())
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        var rows =
                jdbc.queryForList(
                        "SELECT * FROM chat_messages WHERE room_id=1 AND created_at>=? AND (?=0 OR"
                                + " message_id<?) ORDER BY message_id DESC LIMIT 51",
                        now().minusDays(90),
                        before,
                        before);
        boolean more = rows.size() > 50;
        var items =
                new ArrayList<>(
                        rows.stream().limit(50).map(row -> item(row, subjects, editor)).toList());
        Collections.reverse(items);
        return new Page(items, more ? items.getFirst().id() : null, true);
    }

    private String hash(String body) {
        try {
            return HexFormat.of()
                    .formatHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Chat hash unavailable");
        }
    }

    @Transactional
    public Item send(
            List<String> subjects, String name, String input, String requestKey, boolean editor) {
        available();
        if (subjects.isEmpty()
                || input == null
                || requestKey == null
                || !requestKey.matches(
                        "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        String body = input.strip();
        if (body.isEmpty()
                || body.length() > 2000
                || body.codePoints()
                        .anyMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\t'))
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        String subject = subjects.getFirst(),
                key = requestKey.toLowerCase(Locale.ROOT),
                digest = hash(body);
        var at = now();
        // Serialize room writes: commit order matches message IDs, including simultaneous retries.
        jdbc.queryForObject(
                "SELECT room_id FROM chat_rooms WHERE room_id=1 FOR UPDATE", Long.class);
        var existing =
                jdbc.queryForList(
                        "SELECT * FROM chat_messages WHERE identity_subject=? AND request_key=?",
                        subject,
                        key);
        if (!existing.isEmpty()) {
            var row = existing.getFirst();
            if (!digest.equals(row.get("request_hash")))
                throw new BaseException(CommonErrorStatus.BAD_REQUEST);
            return item(row, subjects, editor);
        }
        jdbc.update(
                "INSERT INTO"
                    + " chat_rate_limits(identity_subject,minute_start,minute_count,day_start,day_count,last_write)"
                    + " VALUES(?,?,0,?,0,?) ON DUPLICATE KEY UPDATE"
                    + " identity_subject=identity_subject",
                subject,
                at,
                at.toLocalDate(),
                at.minusDays(1));
        var rate =
                jdbc.queryForMap(
                        "SELECT * FROM chat_rate_limits WHERE identity_subject=? FOR UPDATE",
                        subject);
        LocalDate day =
                rate.get("day_start") instanceof LocalDate local
                        ? local
                        : ((java.sql.Date) rate.get("day_start")).toLocalDate();
        int minute =
                at.isBefore(date(rate.get("minute_start")).plusMinutes(1))
                        ? ((Number) rate.get("minute_count")).intValue()
                        : 0;
        int daily = at.toLocalDate().equals(day) ? ((Number) rate.get("day_count")).intValue() : 0;
        if (at.isBefore(date(rate.get("last_write")).plusSeconds(1))
                || minute >= 30
                || daily >= 500) throw new BaseException(CommonErrorStatus.TOO_MANY_REQUESTS);
        String display = name == null ? "회원" : name.replaceAll("[\\p{Cntrl}]", "").strip();
        if (display.isBlank() || display.contains("@")) display = "회원";
        if (display.length() > 40) display = display.substring(0, 40);
        final String author = display;
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbc.update(
                c -> {
                    var statement =
                            c.prepareStatement(
                                    "INSERT INTO"
                                        + " chat_messages(room_id,identity_subject,display_name,body,request_key,request_hash,created_at)"
                                        + " VALUES(1,?,?,?,?,?,?)",
                                    Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, subject);
                    statement.setString(2, author);
                    statement.setString(3, body);
                    statement.setString(4, key);
                    statement.setString(5, digest);
                    statement.setObject(6, at);
                    return statement;
                },
                holder);
        jdbc.update(
                "UPDATE chat_rate_limits SET"
                    + " minute_start=?,minute_count=?,day_start=?,day_count=?,last_write=? WHERE"
                    + " identity_subject=?",
                minute == 0 ? at : date(rate.get("minute_start")),
                minute + 1,
                at.toLocalDate(),
                daily + 1,
                at,
                subject);
        events.publishEvent(new Changed());
        return item(
                jdbc.queryForMap(
                        "SELECT * FROM chat_messages WHERE message_id=?",
                        holder.getKey().longValue()),
                subjects,
                editor);
    }

    @Transactional
    public void delete(long id, List<String> subjects, boolean editor) {
        available();
        jdbc.queryForObject(
                "SELECT room_id FROM chat_rooms WHERE room_id=1 FOR UPDATE", Long.class);
        var rows =
                jdbc.queryForList(
                        "SELECT * FROM chat_messages WHERE room_id=1 AND message_id=?", id);
        if (rows.isEmpty()
                || (!editor
                        && !subjects.contains(rows.getFirst().get("identity_subject").toString())))
            throw new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND);
        jdbc.update(
                "UPDATE chat_messages SET body='',display_name='',deleted_at=COALESCE(deleted_at,?)"
                        + " WHERE message_id=?",
                now(),
                id);
        events.publishEvent(new Changed());
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 3600000)
    public void retain() {
        if (!settings.enabled) return;
        jdbc.update("DELETE FROM chat_messages WHERE created_at<? LIMIT 1000", now().minusDays(90));
        jdbc.update(
                "DELETE FROM chat_rate_limits WHERE last_write<? LIMIT 1000", now().minusDays(2));
    }
}
