package com.snowk.blog.api.comment;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.*;
import java.util.*;

@Service
public class CommentService {
    private final JdbcTemplate jdbc;
    private final Clock clock;

    @Autowired
    public CommentService(JdbcTemplate jdbc) {
        this(jdbc, Clock.systemUTC());
    }

    public CommentService(JdbcTemplate jdbc, Clock clock) {
        this.jdbc = jdbc;
        this.clock = clock;
    }

    public record Item(
            String id,
            String parentId,
            int depth,
            String author,
            String body,
            String createdAt,
            boolean deleted,
            boolean canEdit,
            boolean canDelete) {}

    public record Page(List<Item> items, String nextCursor, boolean authenticated) {}

    private record Target(String table, String id, String status) {}

    private Target target(String kind) {
        return switch (kind) {
            case "posts" -> new Target("posts", "post_id", "PUBLISHED");
            case "projects" -> new Target("projects", "project_id", "ACTIVE");
            default -> throw error(CommonErrorStatus.RESOURCE_NOT_FOUND);
        };
    }

    private BaseException error(CommonErrorStatus status) {
        return new BaseException(status);
    }

    private void visible(Target t, long id, boolean lock) {
        if (jdbc.queryForList(
                        "SELECT "
                                + t.id
                                + " FROM "
                                + t.table
                                + " WHERE "
                                + t.id
                                + "=? AND visibility='PUBLIC' AND status=?"
                                + (lock ? " FOR UPDATE" : ""),
                        Long.class,
                        id,
                        t.status)
                .isEmpty()) throw error(CommonErrorStatus.RESOURCE_NOT_FOUND);
    }

    // JDBC drivers return DATETIME/DATE as either java.time or legacy java.sql types.
    static LocalDateTime dateTime(Object value) {
        return value instanceof LocalDateTime local ? local : ((Timestamp) value).toLocalDateTime();
    }

    static LocalDate date(Object value) {
        return value instanceof LocalDate local ? local : ((java.sql.Date) value).toLocalDate();
    }

    private Item item(Map<String, Object> r, List<String> subjects, boolean editor) {
        boolean deleted = r.get("deleted_at") != null,
                owner = subjects.contains(r.get("identity_subject").toString());
        return new Item(
                r.get("comment_id").toString(),
                r.get("parent_id") == null ? null : r.get("parent_id").toString(),
                ((Number) r.get("depth")).intValue(),
                deleted ? "삭제된 댓글" : r.get("display_name").toString(),
                deleted ? "" : r.get("body").toString(),
                dateTime(r.get("created_at")).toInstant(ZoneOffset.UTC).toString(),
                deleted,
                !deleted && owner,
                !deleted && (owner || editor));
    }

    private Map<String, Object> row(Target t, long content, long id) {
        var rows =
                jdbc.queryForList(
                        "SELECT * FROM comments WHERE " + t.id + "=? AND comment_id=?",
                        content,
                        id);
        if (rows.isEmpty()) throw error(CommonErrorStatus.RESOURCE_NOT_FOUND);
        return rows.getFirst();
    }

    @Transactional(readOnly = true)
    public Page list(String kind, long id, long after, List<String> subjects, boolean editor) {
        if (after < 0) throw error(CommonErrorStatus.BAD_REQUEST);
        var t = target(kind);
        visible(t, id, false);
        var rows =
                jdbc.queryForList(
                        "SELECT * FROM comments WHERE "
                                + t.id
                                + "=? AND comment_id>? ORDER BY comment_id LIMIT 51",
                        id,
                        after);
        boolean more = rows.size() > 50;
        var page = rows.stream().limit(50).map(r -> item(r, subjects, editor)).toList();
        return new Page(page, more ? page.getLast().id() : null, !subjects.isEmpty());
    }

    private String body(String value) {
        if (value == null) throw error(CommonErrorStatus.BAD_REQUEST);
        String text = value.strip();
        if (text.isEmpty()
                || text.length() > 2000
                || text.codePoints()
                        .anyMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\t'))
            throw error(CommonErrorStatus.BAD_REQUEST);
        return text;
    }

    private String hash(String value) {
        try {
            return HexFormat.of()
                    .formatHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private void member(List<String> subjects) {
        if (subjects.isEmpty()) throw error(CommonErrorStatus.FORBIDDEN);
    }

    private void guard(String subject) {
        var now = LocalDateTime.now(clock);
        jdbc.update(
                "INSERT IGNORE INTO"
                    + " comment_rate_limits(identity_subject,last_write,minute_start,minute_count,day_start,day_count)"
                    + " VALUES (?, ?, ?, 0, ?, 0)",
                subject,
                now.minusYears(1),
                now,
                now.toLocalDate());
        jdbc.queryForList(
                "SELECT identity_subject FROM comment_rate_limits WHERE identity_subject=? FOR"
                        + " UPDATE",
                subject);
    }

    private void consume(String subject) {
        var r =
                jdbc.queryForMap(
                        "SELECT * FROM comment_rate_limits WHERE identity_subject=?", subject);
        LocalDateTime now = LocalDateTime.now(clock),
                last = dateTime(r.get("last_write")),
                minute = dateTime(r.get("minute_start"));
        var day = date(r.get("day_start"));
        int minuteCount = ((Number) r.get("minute_count")).intValue(),
                dayCount = ((Number) r.get("day_count")).intValue();
        if (now.isBefore(last.plusSeconds(3))) throw error(CommonErrorStatus.TOO_MANY_REQUESTS);
        if (!now.isBefore(minute.plusMinutes(1))) {
            minute = now;
            minuteCount = 0;
        }
        if (!day.equals(now.toLocalDate())) {
            day = now.toLocalDate();
            dayCount = 0;
        }
        if (minuteCount >= 10 || dayCount >= 100) throw error(CommonErrorStatus.TOO_MANY_REQUESTS);
        jdbc.update(
                "UPDATE comment_rate_limits SET"
                    + " last_write=?,minute_start=?,minute_count=?,day_start=?,day_count=? WHERE"
                    + " identity_subject=?",
                now,
                minute,
                minuteCount + 1,
                day,
                dayCount + 1,
                subject);
    }

    @Transactional
    public Item create(
            String kind,
            long id,
            List<String> subjects,
            String name,
            String text,
            Long parent,
            String key) {
        member(subjects);
        String content = body(text);
        if (key == null
                || !key.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"))
            throw error(CommonErrorStatus.BAD_REQUEST);
        var t = target(kind);
        guard(subjects.getFirst());
        visible(t, id, true);
        String fingerprint = hash(kind + ":" + id + ":" + parent + ":" + content);
        var replay =
                jdbc.queryForList(
                        "SELECT * FROM comments WHERE identity_subject=? AND request_key=?",
                        subjects.getFirst(),
                        key);
        if (!replay.isEmpty()) {
            if (!fingerprint.equals(replay.getFirst().get("request_hash")))
                throw error(CommonErrorStatus.CONFLICT);
            return item(replay.getFirst(), subjects, false);
        }
        int depth = 0;
        if (parent != null) {
            var p = row(t, id, parent);
            depth = Math.addExact(((Number) p.get("depth")).intValue(), 1);
        }
        var now = LocalDateTime.now(clock);
        var args = new ArrayList<Object>(subjects);
        args.add(now.minusMinutes(5));
        args.add(content);
        long duplicate =
                jdbc.queryForObject(
                        "SELECT COUNT(*) FROM comments WHERE identity_subject IN ("
                                + String.join(",", Collections.nCopies(subjects.size(), "?"))
                                + ") AND created_at>? AND body=? AND deleted_at IS NULL",
                        Long.class,
                        args.toArray());
        if (duplicate > 0) throw error(CommonErrorStatus.CONFLICT);
        consume(subjects.getFirst());
        String label = name == null ? "회원" : name.strip().replaceAll("[\\p{Cntrl}]", "");
        if (label.isBlank() || label.contains("@")) label = "회원";
        if (label.length() > 40) label = label.substring(0, 40);
        final String author = label;
        final int nesting = depth;
        var holder = new GeneratedKeyHolder();
        jdbc.update(
                connection -> {
                    var ps =
                            connection.prepareStatement(
                                    "INSERT INTO comments("
                                            + t.id
                                            + ",parent_id,depth,identity_subject,display_name,body,request_key,request_hash,created_at,updated_at)"
                                            + " VALUES (?,?,?,?,?,?,?,?,?,?)",
                                    Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, id);
                    if (parent == null) ps.setNull(2, Types.BIGINT);
                    else ps.setLong(2, parent);
                    ps.setInt(3, nesting);
                    ps.setString(4, subjects.getFirst());
                    ps.setString(5, author);
                    ps.setString(6, content);
                    ps.setString(7, key);
                    ps.setString(8, fingerprint);
                    ps.setTimestamp(9, Timestamp.valueOf(now));
                    ps.setTimestamp(10, Timestamp.valueOf(now));
                    return ps;
                },
                holder);
        return item(
                row(t, id, Objects.requireNonNull(holder.getKey()).longValue()), subjects, false);
    }

    @Transactional
    public Item edit(String kind, long id, long comment, List<String> subjects, String text) {
        member(subjects);
        var t = target(kind);
        String content = body(text);
        guard(subjects.getFirst());
        visible(t, id, true);
        var r = row(t, id, comment);
        if (!subjects.contains(r.get("identity_subject").toString()))
            throw error(CommonErrorStatus.FORBIDDEN);
        if (r.get("deleted_at") != null) throw error(CommonErrorStatus.CONFLICT);
        if (!content.equals(r.get("body"))) {
            consume(subjects.getFirst());
            jdbc.update(
                    "UPDATE comments SET body=?,updated_at=? WHERE comment_id=?",
                    content,
                    LocalDateTime.now(clock),
                    comment);
        }
        return item(row(t, id, comment), subjects, false);
    }

    @Transactional
    public Item delete(String kind, long id, long comment, List<String> subjects, boolean editor) {
        member(subjects);
        var t = target(kind);
        visible(t, id, true);
        var r = row(t, id, comment);
        if (!editor && !subjects.contains(r.get("identity_subject").toString()))
            throw error(CommonErrorStatus.FORBIDDEN);
        if (r.get("deleted_at") == null)
            jdbc.update(
                    "UPDATE comments SET body='',display_name='',deleted_at=?,updated_at=? WHERE"
                            + " comment_id=?",
                    LocalDateTime.now(clock),
                    LocalDateTime.now(clock),
                    comment);
        return item(row(t, id, comment), subjects, editor);
    }
}
