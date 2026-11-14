package com.snowk.blog.api.notification;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Service
public class NotificationService {
    private final JdbcTemplate jdbc;
    private final Map<Long, Set<String>> authors = new HashMap<>();

    public NotificationService(
            JdbcTemplate jdbc, @Value("${BLOG_IDENTITY_EDITOR_BINDINGS:}") String bindings) {
        this.jdbc = jdbc;
        if (!bindings.isBlank())
            for (String pair : bindings.split(",")) {
                String[] fields = pair.trim().split(":", -1);
                if (fields.length != 2
                        || !fields[0].matches("[0-9]{1,20}")
                        || !fields[1].matches("[1-9][0-9]*"))
                    throw new IllegalArgumentException("Invalid editor notification binding");
                authors.computeIfAbsent(Long.parseLong(fields[1]), ignored -> new LinkedHashSet<>())
                        .add(fields[0]);
            }
    }

    // Synchronous domain subscriber: inbox insertion commits/rolls back with the comment.
    // MANDATORY prevents an accidental untransactional publisher from creating partial state.
    @EventListener
    @Order(0)
    @Transactional(propagation = Propagation.MANDATORY)
    public void onComment(CommentCreated event) {
        var rows =
                jdbc.queryForList(
                        "SELECT c.comment_id,c.parent_id,p.identity_subject AS"
                            + " parent_subject,p.deleted_at AS parent_deleted, post.author_user_id"
                            + " AS content_author,c.project_id FROM comments c LEFT JOIN comments p"
                            + " ON p.comment_id=c.parent_id LEFT JOIN posts post ON"
                            + " post.post_id=c.post_id LEFT JOIN projects project ON"
                            + " project.project_id=c.project_id WHERE c.comment_id=? AND"
                            + " c.deleted_at IS NULL",
                        event.commentId());
        if (rows.isEmpty()) return;
        var row = rows.getFirst();
        Map<String, String> recipients = new LinkedHashMap<>();
        if (row.get("content_author") instanceof Number author)
            for (String subject : authors.getOrDefault(author.longValue(), Set.of()))
                recipients.put(subject, "COMMENT");
        // Projects are a shared editor-managed catalog and have no individual owner column.
        if (row.get("project_id") != null)
            authors.values()
                    .forEach(group -> group.forEach(subject -> recipients.put(subject, "COMMENT")));
        if (row.get("parent_subject") != null && row.get("parent_deleted") == null)
            recipients.put(row.get("parent_subject").toString(), "REPLY");
        event.actorSubjects().forEach(recipients::remove);
        recipients.forEach(
                (subject, kind) ->
                        jdbc.update(
                                "INSERT INTO"
                                    + " notifications(identity_subject,comment_id,kind,created_at)"
                                    + " VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE"
                                    + " notification_id=notification_id",
                                subject,
                                event.commentId(),
                                kind,
                                LocalDateTime.now(ZoneOffset.UTC)));
    }

    public record Item(
            String id,
            String kind,
            String title,
            String actor,
            String path,
            String createdAt,
            boolean read) {}

    public record Page(List<Item> items, String nextCursor, String snapshotId, long unreadCount) {}

    private static final String JOIN =
            " FROM notifications n JOIN comments c ON c.comment_id=n.comment_id LEFT JOIN posts p"
                + " ON p.post_id=c.post_id LEFT JOIN projects j ON j.project_id=c.project_id";
    private static final String VISIBLE =
            "n.created_at>=? AND c.deleted_at IS NULL AND ((p.visibility='PUBLIC' AND"
                + " p.status='PUBLISHED') OR (j.visibility='PUBLIC' AND j.status='ACTIVE'))";

    private String owner(List<String> subjects) {
        if (subjects.isEmpty()) throw new BaseException(CommonErrorStatus.FORBIDDEN);
        return "n.identity_subject IN ("
                + String.join(",", Collections.nCopies(subjects.size(), "?"))
                + ")";
    }

    private String instant(Object value) {
        var time =
                value instanceof LocalDateTime local
                        ? local
                        : ((Timestamp) value).toLocalDateTime();
        return time.toInstant(ZoneOffset.UTC).toString();
    }

    private ArrayList<Object> visibleArgs(List<String> subjects) {
        var args = new ArrayList<Object>(subjects);
        args.add(LocalDateTime.now(ZoneOffset.UTC).minusDays(90));
        return args;
    }

    @Transactional(readOnly = true)
    public long unread(List<String> subjects) {
        return jdbc.queryForObject(
                "SELECT COUNT(*)"
                        + JOIN
                        + " WHERE "
                        + owner(subjects)
                        + " AND "
                        + VISIBLE
                        + " AND n.read_at IS NULL",
                Long.class,
                visibleArgs(subjects).toArray());
    }

    @Transactional(readOnly = true)
    public Page list(List<String> subjects, long before) {
        if (before < 0) throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        var args = visibleArgs(subjects);
        String where = " WHERE " + owner(subjects) + " AND " + VISIBLE;
        if (before > 0) {
            where += " AND n.notification_id<?";
            args.add(before);
        }
        var rows =
                jdbc.queryForList(
                        "SELECT"
                            + " n.notification_id,n.kind,n.created_at,n.read_at,c.comment_id,c.display_name,"
                            + " COALESCE(p.title,j.title) AS title,COALESCE(p.slug,j.slug) AS"
                            + " slug,c.post_id"
                                + JOIN
                                + where
                                + " ORDER BY n.notification_id DESC LIMIT 51",
                        args.toArray());
        var items =
                rows.stream()
                        .limit(50)
                        .map(
                                r ->
                                        new Item(
                                                r.get("notification_id").toString(),
                                                r.get("kind").toString(),
                                                r.get("title").toString(),
                                                r.get("display_name").toString(),
                                                "/"
                                                        + (r.get("post_id") == null
                                                                ? "projects"
                                                                : "posts")
                                                        + "/"
                                                        + r.get("slug")
                                                        + "#comment-"
                                                        + r.get("comment_id"),
                                                instant(r.get("created_at")),
                                                r.get("read_at") != null))
                        .toList();
        long unread = unread(subjects);
        String snapshot =
                jdbc.queryForObject(
                                "SELECT COALESCE(MAX(n.notification_id),0)"
                                        + JOIN
                                        + " WHERE "
                                        + owner(subjects)
                                        + " AND "
                                        + VISIBLE,
                                Long.class,
                                visibleArgs(subjects).toArray())
                        .toString();
        return new Page(items, rows.size() > 50 ? items.getLast().id() : null, snapshot, unread);
    }

    @Transactional
    public void read(List<String> subjects, long id) {
        if (id <= 0) throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        var args = visibleArgs(subjects);
        args.add(id);
        var ids =
                jdbc.queryForList(
                        "SELECT n.notification_id"
                                + JOIN
                                + " WHERE "
                                + owner(subjects)
                                + " AND "
                                + VISIBLE
                                + " AND n.notification_id=?",
                        Long.class,
                        args.toArray());
        if (ids.isEmpty()) throw new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND);
        // Recipient predicate remains on the write, even if the preceding read was authorized.
        var write = new ArrayList<Object>();
        write.add(LocalDateTime.now(ZoneOffset.UTC));
        write.addAll(subjects);
        write.add(id);
        jdbc.update(
                "UPDATE notifications n SET read_at=COALESCE(read_at,?) WHERE "
                        + owner(subjects)
                        + " AND n.notification_id=?",
                write.toArray());
    }

    @Transactional
    public void readAll(List<String> subjects, long through) {
        if (through < 0) throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        var args = new ArrayList<Object>();
        args.add(LocalDateTime.now(ZoneOffset.UTC));
        args.addAll(subjects);
        args.add(through);
        jdbc.update(
                "UPDATE notifications n SET read_at=? WHERE "
                        + owner(subjects)
                        + " AND n.notification_id<=? AND read_at IS NULL",
                args.toArray());
    }

    @Scheduled(initialDelay = 3600000, fixedDelay = 3600000)
    public void retain() {
        jdbc.update(
                "DELETE FROM notifications WHERE created_at<? ORDER BY notification_id LIMIT 1000",
                LocalDateTime.now(ZoneOffset.UTC).minusDays(90));
    }
}
