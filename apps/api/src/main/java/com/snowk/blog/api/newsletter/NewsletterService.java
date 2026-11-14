package com.snowk.blog.api.newsletter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Service
public class NewsletterService {
    private final JdbcTemplate jdbc;
    private final NewsletterSettings settings;
    private final NewsletterCrypto crypto;
    private final Clock clock;
    private final ObjectMapper json = new ObjectMapper();

    @Autowired
    public NewsletterService(
            JdbcTemplate jdbc, NewsletterSettings settings, NewsletterCrypto crypto) {
        this(jdbc, settings, crypto, Clock.systemUTC());
    }

    public NewsletterService(
            JdbcTemplate jdbc, NewsletterSettings settings, NewsletterCrypto crypto, Clock clock) {
        this.jdbc = jdbc;
        this.settings = settings;
        this.crypto = crypto;
        this.clock = clock;
    }

    static LocalDateTime time(Object value) {
        return value instanceof LocalDateTime t ? t : ((Timestamp) value).toLocalDateTime();
    }

    private BaseException bad() {
        return new BaseException(CommonErrorStatus.BAD_REQUEST);
    }

    public String normalize(String email) {
        if (email == null) throw bad();
        String value = email.strip().toLowerCase(Locale.ROOT);
        if (value.length() > 254
                || !value.matches(
                        "[a-z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-z0-9](?:[a-z0-9.-]*[a-z0-9])?\\.[a-z]{2,63}")
                || value.contains("..")
                || value.startsWith(".")
                || value.contains(".@")) throw bad();
        return value;
    }

    private boolean limit(String key, int max, long seconds, boolean consume) {
        var now = LocalDateTime.now(clock);
        jdbc.update(
                "INSERT INTO newsletter_limits(limit_key,window_start,request_count) VALUES(?,?,0)"
                        + " ON DUPLICATE KEY UPDATE limit_key=limit_key",
                key,
                now);
        var r =
                jdbc.queryForMap(
                        "SELECT * FROM newsletter_limits WHERE limit_key=? FOR UPDATE", key);
        var start = time(r.get("window_start"));
        int n = ((Number) r.get("request_count")).intValue();
        if (!now.isBefore(start.plusSeconds(seconds))) {
            start = now;
            n = 0;
        }
        if (n >= max) return false;
        jdbc.update(
                "UPDATE newsletter_limits SET window_start=?,request_count=? WHERE limit_key=?",
                start,
                n + (consume ? 1 : 0),
                key);
        return true;
    }

    void enqueue(
            long subscriber,
            Long post,
            long generation,
            String type,
            String key,
            String subject,
            String text,
            String unsubscribe) {
        try {
            String payload =
                    crypto.seal(
                            json.writeValueAsString(
                                    Map.of(
                                            "subject",
                                            subject,
                                            "text",
                                            text,
                                            "unsubscribe",
                                            unsubscribe)));
            var now = LocalDateTime.now(clock);
            jdbc.update(
                    "INSERT INTO"
                        + " newsletter_outbox(message_id,subscriber_id,post_id,generation,message_type,dedupe_key,payload_cipher,status,attempts,next_attempt_at,created_at)"
                        + " VALUES(?,?,?,?,?,?,?,'PENDING',0,?,?) ON DUPLICATE KEY UPDATE"
                        + " dedupe_key=dedupe_key",
                    UUID.randomUUID().toString(),
                    subscriber,
                    post,
                    generation,
                    type,
                    key,
                    payload,
                    now,
                    now);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Newsletter payload unavailable");
        }
    }

    @Transactional
    public void subscribe(String email, String client, boolean consent) {
        settings.requireEnabled();
        if (!consent || client == null || !client.matches("[a-f0-9]{64}")) throw bad();
        String address = normalize(email), hash = crypto.hmac("email:" + address);
        if (!limit("global", 500, 86400, false)
                || !limit("client:" + client, 5, 3600, true)
                || !limit("email:" + hash, 3, 86400, true)) return;
        var now = LocalDateTime.now(clock);
        jdbc.update(
                "INSERT INTO"
                    + " newsletter_subscribers(email_hash,email,status,generation,created_at,updated_at)"
                    + " VALUES(?,?,'NEW',0,?,?) ON DUPLICATE KEY UPDATE email_hash=email_hash",
                hash,
                address,
                now,
                now);
        var s =
                jdbc.queryForMap(
                        "SELECT * FROM newsletter_subscribers WHERE email_hash=? FOR UPDATE", hash);
        if ("ACTIVE".equals(s.get("status"))) return;
        if ("PENDING".equals(s.get("status"))
                && now.isBefore(time(s.get("updated_at")).plusMinutes(1))) return;
        limit("global", 500, 86400, true);
        long id = ((Number) s.get("subscriber_id")).longValue(),
                generation = ((Number) s.get("generation")).longValue() + 1;
        String confirm = crypto.token(), unsubscribe = crypto.token();
        jdbc.update(
                "UPDATE newsletter_subscribers SET"
                    + " email=?,status='PENDING',generation=?,confirm_hash=?,unsubscribe_hash=?,unsubscribe_cipher=?,token_expires=?,confirmed_at=NULL,updated_at=?"
                    + " WHERE subscriber_id=?",
                address,
                generation,
                crypto.hash(confirm),
                crypto.hash(unsubscribe),
                crypto.seal(unsubscribe),
                now.plusHours(24),
                now,
                id);
        String link = settings.siteUrl() + "/subscribe/confirm#token=" + confirm,
                stop = settings.siteUrl() + "/subscribe/unsubscribe#token=" + unsubscribe;
        enqueue(
                id,
                null,
                generation,
                "CONFIRM",
                "confirm:" + id + ":" + generation,
                "s-nowk Blog 이메일 구독 확인",
                "새 글 알림을 신청하셨나요? 아래 주소에서 확인 버튼을 눌러 주세요. 확인 링크는 24시간 동안 유효합니다.\n\n"
                        + link
                        + "\n\n신청하지 않았다면 무시하거나 아래 주소에서 해지할 수 있습니다.\n"
                        + stop,
                stop);
    }

    private Map<String, Object> byToken(String column, String token) {
        if (token == null || !token.matches("[A-Za-z0-9_-]{43}")) throw bad();
        var rows =
                jdbc.queryForList(
                        "SELECT * FROM newsletter_subscribers WHERE " + column + "=? FOR UPDATE",
                        crypto.hash(token));
        if (rows.isEmpty()) throw bad();
        return rows.getFirst();
    }

    @Transactional
    public void confirm(String token) {
        settings.requireEnabled();
        var s = byToken("confirm_hash", token);
        if ("ACTIVE".equals(s.get("status"))) return;
        if (!"PENDING".equals(s.get("status"))
                || !LocalDateTime.now(clock).isBefore(time(s.get("token_expires")))) throw bad();
        var now = LocalDateTime.now(clock);
        jdbc.update(
                "UPDATE newsletter_subscribers SET status='ACTIVE',confirmed_at=?,updated_at=?"
                        + " WHERE subscriber_id=?",
                now,
                now,
                s.get("subscriber_id"));
    }

    @Transactional
    public void unsubscribe(String token) {
        settings.requireEnabled();
        var s = byToken("unsubscribe_hash", token);
        jdbc.update(
                "UPDATE newsletter_subscribers SET"
                    + " status='UNSUBSCRIBED',email=NULL,unsubscribe_cipher=NULL,confirm_hash=NULL,updated_at=?"
                    + " WHERE subscriber_id=?",
                LocalDateTime.now(clock),
                s.get("subscriber_id"));
    }

    @Transactional
    public void prune() {
        if (!settings.ready()) return;
        var now = LocalDateTime.now(clock);
        jdbc.update(
                "UPDATE newsletter_subscribers SET"
                    + " status='EXPIRED',email=NULL,confirm_hash=NULL,unsubscribe_cipher=NULL,updated_at=?"
                    + " WHERE status='PENDING' AND token_expires<?",
                now,
                now.minusHours(24));
        jdbc.update(
                "DELETE FROM newsletter_outbox WHERE status<>'PENDING' AND created_at<?",
                now.minusDays(30));
        jdbc.update(
                "DELETE FROM newsletter_subscribers WHERE status IN ('UNSUBSCRIBED','EXPIRED') AND"
                        + " updated_at<?",
                now.minusDays(90));
        jdbc.update("DELETE FROM newsletter_limits WHERE window_start<?", now.minusDays(2));
    }
}
