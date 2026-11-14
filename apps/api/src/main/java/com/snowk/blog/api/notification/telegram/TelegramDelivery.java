package com.snowk.blog.api.notification.telegram;

import com.snowk.blog.api.notification.CommentCreated;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.*;
import java.util.*;

@Service
public class TelegramDelivery {
    private final JdbcTemplate jdbc;
    private final TelegramSettings settings;
    private final TelegramGateway gateway;
    private final TransactionTemplate tx;

    public TelegramDelivery(
            JdbcTemplate jdbc,
            TelegramSettings settings,
            TelegramGateway gateway,
            PlatformTransactionManager manager) {
        this.jdbc = jdbc;
        this.settings = settings;
        this.gateway = gateway;
        tx = new TransactionTemplate(manager);
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    private Map<String, Object> control(boolean lock) {
        return jdbc.queryForMap(
                "SELECT * FROM notification_delivery_control WHERE channel='telegram'"
                        + (lock ? " FOR UPDATE" : ""));
    }

    private LocalDateTime date(Object value) {
        return value instanceof LocalDateTime local
                ? local
                : ((java.sql.Timestamp) value).toLocalDateTime();
    }

    @EventListener
    @Order(10)
    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(CommentCreated event) {
        if (!settings.configured || !settings.matches(control(false).get("active_config_hash")))
            return;
        var rows =
                jdbc.queryForList(
                        "SELECT notification_id,identity_subject FROM notifications WHERE"
                                + " comment_id=?",
                        event.commentId());
        for (var row : rows)
            if (settings.recipients.containsKey(row.get("identity_subject").toString()))
                jdbc.update(
                        "INSERT INTO"
                            + " notification_deliveries(notification_id,config_hash,state,next_attempt_at)"
                            + " VALUES(?,?,'QUEUED',?) ON DUPLICATE KEY UPDATE"
                            + " delivery_id=delivery_id",
                        row.get("notification_id"),
                        settings.fingerprint,
                        now());
    }

    public record Status(
            boolean configured,
            boolean active,
            boolean currentConfiguration,
            int recipientCount,
            Map<String, Long> states) {}

    @Transactional(readOnly = true)
    public Status status() {
        var active = control(false).get("active_config_hash");
        var states = new LinkedHashMap<String, Long>();
        for (var row :
                jdbc.queryForList(
                        "SELECT state,COUNT(*) AS amount FROM notification_deliveries GROUP BY"
                                + " state"))
            states.put(row.get("state").toString(), ((Number) row.get("amount")).longValue());
        return new Status(
                settings.configured,
                active != null,
                settings.matches(active),
                settings.recipients.size(),
                states);
    }

    @Transactional
    public Status setActive(boolean enabled) {
        if (enabled && !settings.configured)
            throw new com.snowk.blog.api.global.exception.BaseException(
                    com.snowk.blog.api.global.error.CommonErrorStatus.BAD_REQUEST);
        control(true);
        jdbc.update(
                "UPDATE notification_delivery_control SET active_config_hash=? WHERE"
                        + " channel='telegram'",
                enabled ? settings.fingerprint : null);
        return status();
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 5000)
    public void tick() {
        if (!settings.configured) return;
        // A crash after claiming or sending is ambiguous. Never automatically resend it.
        jdbc.update(
                "UPDATE notification_deliveries SET"
                        + " state='UNKNOWN',last_code='WORKER_INTERRUPTED',completed_at=? WHERE"
                        + " state='SENDING' AND claimed_at<?",
                now(),
                now().minusMinutes(5));
        Long id =
                tx.execute(
                        status -> {
                            var guard = control(true);
                            if (!settings.matches(guard.get("active_config_hash"))
                                    || now().isBefore(date(guard.get("next_allowed_at"))))
                                return null;
                            var rows =
                                    jdbc.queryForList(
                                            "SELECT delivery_id FROM notification_deliveries WHERE"
                                                + " state='QUEUED' AND config_hash=? AND"
                                                + " next_attempt_at<=? ORDER BY delivery_id LIMIT 1"
                                                + " FOR UPDATE SKIP LOCKED",
                                            Long.class,
                                            settings.fingerprint,
                                            now());
                            if (rows.isEmpty()) return null;
                            long selected = rows.getFirst();
                            jdbc.update(
                                    "UPDATE notification_deliveries SET"
                                        + " state='SENDING',attempts=attempts+1,claimed_at=? WHERE"
                                        + " delivery_id=?",
                                    now(),
                                    selected);
                            jdbc.update(
                                    "UPDATE notification_delivery_control SET next_allowed_at=?"
                                            + " WHERE channel='telegram'",
                                    now().plusSeconds(4));
                            return selected;
                        });
        if (id == null) return;
        tx.executeWithoutResult(
                status -> {
                    var guard = control(true);
                    var jobs =
                            jdbc.queryForList(
                                    "SELECT * FROM notification_deliveries WHERE delivery_id=? FOR"
                                            + " UPDATE",
                                    id);
                    if (jobs.isEmpty() || !"SENDING".equals(jobs.getFirst().get("state"))) return;
                    var job = jobs.getFirst();
                    var recipients =
                            jdbc.queryForList(
                                    "SELECT n.identity_subject FROM notifications n JOIN comments c"
                                        + " ON c.comment_id=n.comment_id LEFT JOIN posts p ON"
                                        + " p.post_id=c.post_id LEFT JOIN projects j ON"
                                        + " j.project_id=c.project_id WHERE n.notification_id=? AND"
                                        + " n.created_at>=? AND c.deleted_at IS NULL AND"
                                        + " ((p.visibility='PUBLIC' AND p.status='PUBLISHED') OR"
                                        + " (j.visibility='PUBLIC' AND j.status='ACTIVE'))",
                                    String.class,
                                    job.get("notification_id"),
                                    now().minusDays(90));
                    TelegramGateway.Result result;
                    if (!settings.matches(guard.get("active_config_hash"))
                            || !settings.fingerprint.equals(job.get("config_hash"))
                            || recipients.isEmpty())
                        result = new TelegramGateway.Result("SKIPPED", "INELIGIBLE", 0);
                    else result = gateway.send(recipients.getFirst());
                    if ("QUEUED".equals(result.state())
                            && ((Number) job.get("attempts")).intValue() >= 6)
                        result = new TelegramGateway.Result("FAILED", "RETRY_LIMIT", 0);
                    jdbc.update(
                            "UPDATE notification_deliveries SET"
                                    + " state=?,last_code=?,next_attempt_at=?,completed_at=? WHERE"
                                    + " delivery_id=?",
                            result.state(),
                            result.code(),
                            now().plusSeconds(result.retrySeconds()),
                            "QUEUED".equals(result.state()) ? null : now(),
                            id);
                    if ("QUEUED".equals(result.state()))
                        jdbc.update(
                                "UPDATE notification_delivery_control SET next_allowed_at=? WHERE"
                                        + " channel='telegram'",
                                now().plusSeconds(result.retrySeconds()));
                });
    }
}
