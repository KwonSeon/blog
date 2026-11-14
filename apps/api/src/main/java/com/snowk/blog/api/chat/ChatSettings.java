package com.snowk.blog.api.chat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class ChatSettings {
    final boolean enabled;
    final String origin;

    public ChatSettings(
            @Value("${BLOG_CHAT_ENABLED:false}") boolean enabled,
            @Value("${BLOG_CHAT_ORIGIN:}") String origin) {
        this.enabled = enabled;
        this.origin = origin;
        if (enabled) {
            URI uri;
            try {
                uri = URI.create(origin);
            } catch (Exception ignored) {
                throw new IllegalArgumentException("Invalid chat origin");
            }
            if (!"https".equals(uri.getScheme())
                    || uri.getHost() == null
                    || uri.getRawUserInfo() != null
                    || uri.getRawQuery() != null
                    || uri.getRawFragment() != null
                    || !uri.getPath().isEmpty())
                throw new IllegalArgumentException("Chat requires an exact HTTPS origin");
        }
    }
}
