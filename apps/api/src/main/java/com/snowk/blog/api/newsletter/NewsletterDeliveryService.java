package com.snowk.blog.api.newsletter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
public class NewsletterDeliveryService {
    private final JdbcTemplate jdbc;
    private final NewsletterSettings settings;
    private final NewsletterCrypto crypto;
    private final NewsletterService service;
    private final NewsletterGateway gateway;
    private final Clock clock;
    private final ObjectMapper json = new ObjectMapper();

    @Autowired
    public NewsletterDeliveryService(
            JdbcTemplate jdbc,
            NewsletterSettings settings,
            NewsletterCrypto crypto,
            NewsletterService service,
            NewsletterGateway gateway) {
        this(jdbc, settings, crypto, service, gateway, Clock.systemUTC());
    }

    public NewsletterDeliveryService(
            JdbcTemplate jdbc,
            NewsletterSettings settings,
            NewsletterCrypto crypto,
            NewsletterService service,
            NewsletterGateway gateway,
            Clock clock) {
        this.jdbc = jdbc;
        this.settings = settings;
        this.crypto = crypto;
        this.service = service;
        this.gateway = gateway;
        this.clock = clock;
    }

    @Transactional
    public void expandOneCampaign() {
        if (!settings.ready()) return;
        var candidates =
                jdbc.queryForList(
                        "SELECT post_id FROM newsletter_campaigns WHERE complete=FALSE ORDER BY"
                                + " created_at LIMIT 1",
                        Long.class);
        if (candidates.isEmpty()) return;
        long post = candidates.getFirst();
        var posts = jdbc.queryForList("SELECT * FROM posts WHERE post_id=? FOR UPDATE", post);
        if (posts.isEmpty()) return;
        var rows =
                jdbc.queryForList(
                        "SELECT * FROM newsletter_campaigns WHERE post_id=? FOR UPDATE", post);
        if (rows.isEmpty() || Boolean.TRUE.equals(rows.getFirst().get("complete"))) return;
        var campaign = rows.getFirst();
        var content = posts.getFirst();
        if (!"PUBLIC".equals(content.get("visibility"))
                || !"PUBLISHED".equals(content.get("status"))) {
            jdbc.update("UPDATE newsletter_campaigns SET complete=TRUE WHERE post_id=?", post);
            return;
        }
        long cursor = ((Number) campaign.get("recipient_cursor")).longValue();
        var subscribers =
                jdbc.queryForList(
                        "SELECT * FROM newsletter_subscribers WHERE status='ACTIVE' AND"
                            + " subscriber_id>? AND confirmed_at<=? ORDER BY subscriber_id LIMIT"
                            + " 50",
                        cursor,
                        campaign.get("created_at"));
        for (var sub : subscribers) {
            long id = ((Number) sub.get("subscriber_id")).longValue();
            String token = crypto.open(sub.get("unsubscribe_cipher").toString()),
                    unsubscribe = settings.siteUrl() + "/subscribe/unsubscribe#token=" + token;
            String title = content.get("title").toString().replaceAll("[\\r\\n]", " ");
            if (title.length() > 180) title = title.substring(0, 180);
            String excerpt = Objects.toString(content.get("excerpt"), "");
            if (excerpt.length() > 600) excerpt = excerpt.substring(0, 600);
            String slug = content.get("slug").toString();
            if (!slug.matches("[A-Za-z0-9_-]{1,150}"))
                throw new IllegalStateException("Invalid public post slug");
            service.enqueue(
                    id,
                    post,
                    ((Number) sub.get("generation")).longValue(),
                    "POST",
                    "post:" + post + ":" + id,
                    "[s-nowk] " + title,
                    title
                            + "\n\n"
                            + excerpt
                            + "\n\n글 읽기: "
                            + settings.siteUrl()
                            + "/posts/"
                            + slug
                            + "\n\n구독 해지: "
                            + unsubscribe,
                    unsubscribe);
            cursor = id;
        }
        jdbc.update(
                "UPDATE newsletter_campaigns SET recipient_cursor=?,complete=? WHERE post_id=?",
                cursor,
                subscribers.size() < 50,
                post);
    }

    @Transactional
    public void deliverOne() {
        if (!settings.ready()) return;
        var now = LocalDateTime.now(clock);
        var candidates =
                jdbc.queryForList(
                        "SELECT message_id,subscriber_id,post_id FROM newsletter_outbox WHERE"
                            + " status='PENDING' AND next_attempt_at<=? ORDER BY next_attempt_at"
                            + " LIMIT 1",
                        now);
        if (candidates.isEmpty()) return;
        var candidate = candidates.getFirst();
        String id = candidate.get("message_id").toString();
        boolean publicPost = true;
        if (candidate.get("post_id") != null) {
            var posts =
                    jdbc.queryForList(
                            "SELECT visibility,status FROM posts WHERE post_id=? FOR UPDATE",
                            candidate.get("post_id"));
            publicPost =
                    !posts.isEmpty()
                            && "PUBLIC".equals(posts.getFirst().get("visibility"))
                            && "PUBLISHED".equals(posts.getFirst().get("status"));
        }
        var subscribers =
                jdbc.queryForList(
                        "SELECT * FROM newsletter_subscribers WHERE subscriber_id=? FOR UPDATE",
                        candidate.get("subscriber_id"));
        var jobs =
                jdbc.queryForList(
                        "SELECT * FROM newsletter_outbox WHERE message_id=? FOR UPDATE", id);
        if (jobs.isEmpty() || !"PENDING".equals(jobs.getFirst().get("status"))) return;
        var job = jobs.getFirst();
        if (now.isBefore(NewsletterService.time(job.get("next_attempt_at")))) return;
        var sub = subscribers.isEmpty() ? null : subscribers.getFirst();
        boolean confirm = "CONFIRM".equals(job.get("message_type"));
        if (sub == null
                || sub.get("email") == null
                || !Objects.equals(sub.get("generation"), job.get("generation"))
                || !(confirm ? "PENDING" : "ACTIVE").equals(sub.get("status"))
                || !publicPost
                || (confirm && !now.isBefore(NewsletterService.time(sub.get("token_expires"))))) {
            finish(id, "CANCELLED", null);
            return;
        }
        int attempts = ((Number) job.get("attempts")).intValue();
        var first =
                job.get("first_attempt_at") == null
                        ? now
                        : NewsletterService.time(job.get("first_attempt_at"));
        if (attempts >= 6
                || !now.isBefore(NewsletterService.time(job.get("created_at")).plusHours(23))) {
            finish(id, "FAILED", "retry_window_exhausted");
            return;
        }
        try {
            var payload = json.readTree(crypto.open(job.get("payload_cipher").toString()));
            gateway.send(
                    id,
                    sub.get("email").toString(),
                    payload.get("subject").asText(),
                    payload.get("text").asText(),
                    payload.get("unsubscribe").asText());
            jdbc.update(
                    "UPDATE newsletter_outbox SET"
                        + " status='SENT',payload_cipher=NULL,sent_at=?,attempts=?,first_attempt_at=?,error_code=NULL"
                        + " WHERE message_id=?",
                    now,
                    attempts + 1,
                    first,
                    id);
        } catch (Exception e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            String code =
                    e instanceof NewsletterGateway.DeliveryFailure f
                            ? "provider_" + f.status()
                            : "transport_error";
            boolean permanent =
                    e instanceof NewsletterGateway.DeliveryFailure f
                            && f.status() >= 400
                            && f.status() < 500
                            && f.status() != 408
                            && f.status() != 429
                            && f.status() != 409;
            if (permanent) {
                finish(id, "FAILED", code);
            } else
                jdbc.update(
                        "UPDATE newsletter_outbox SET"
                            + " attempts=?,first_attempt_at=?,next_attempt_at=?,error_code=? WHERE"
                            + " message_id=?",
                        attempts + 1,
                        first,
                        now.plusSeconds(Math.min(3600, 30L * (1L << attempts))),
                        code,
                        id);
        }
    }

    private void finish(String id, String status, String code) {
        jdbc.update(
                "UPDATE newsletter_outbox SET status=?,payload_cipher=NULL,error_code=? WHERE"
                        + " message_id=?",
                status,
                code,
                id);
    }
}
