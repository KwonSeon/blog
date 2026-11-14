package com.snowk.blog.api.newsletter;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;

import jakarta.persistence.EntityManager;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class NewsletterPublisher {
    private final NewsletterSettings settings;
    private final JdbcTemplate jdbc;
    private final EntityManager em;

    public NewsletterPublisher(NewsletterSettings settings, JdbcTemplate jdbc, EntityManager em) {
        this.settings = settings;
        this.jdbc = jdbc;
        this.em = em;
    }

    public static boolean publicPost(Post post) {
        return post.getStatus() == PostStatus.PUBLISHED
                && post.getVisibility() == Visibility.PUBLIC;
    }

    public void firstPublication(Post post) {
        if (!settings.ready() || !publicPost(post)) return;
        em.flush();
        jdbc.update(
                "INSERT INTO newsletter_campaigns(post_id,created_at,recipient_cursor,complete)"
                        + " VALUES(?,?,0,FALSE) ON DUPLICATE KEY UPDATE post_id=post_id",
                post.getPostId(),
                LocalDateTime.now(ZoneOffset.UTC));
    }
}
