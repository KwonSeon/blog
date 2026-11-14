package com.snowk.blog.api.notification.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Component
public class TelegramSettings {
    final boolean configured;
    final String token;
    final URI inbox;
    final Map<String, String> recipients;
    final String fingerprint;

    public TelegramSettings(
            @Value("${BLOG_TELEGRAM_ENABLED:false}") boolean enabled,
            @Value("${BLOG_TELEGRAM_TOKEN:}") String token,
            @Value("${BLOG_TELEGRAM_RECIPIENTS:}") String bindings,
            @Value("${BLOG_TELEGRAM_SITE_URL:}") String site) {
        configured = enabled;
        this.token = token;
        var map = new TreeMap<String, String>();
        if (!bindings.isBlank())
            for (String binding : bindings.split(",")) {
                String[] pair = binding.trim().split(":", -1);
                if (pair.length != 2
                        || !pair[0].matches("[0-9]{1,20}")
                        || !pair[1].matches("[1-9][0-9]{0,19}")
                        || map.putIfAbsent(pair[0], pair[1]) != null)
                    throw new IllegalArgumentException("Invalid Telegram recipient binding");
            }
        recipients = Map.copyOf(map);
        URI base;
        try {
            base = site.isBlank() ? null : URI.create(site);
        } catch (IllegalArgumentException ignored) {
            throw new IllegalArgumentException("Invalid Telegram site URL");
        }
        if (enabled
                && (!token.matches("[0-9]{1,20}:[A-Za-z0-9_-]{20,200}")
                        || map.isEmpty()
                        || base == null
                        || !"https".equals(base.getScheme())
                        || base.getHost() == null
                        || base.getRawUserInfo() != null
                        || base.getRawQuery() != null
                        || base.getRawFragment() != null
                        || !(base.getPath().isEmpty() || base.getPath().equals("/"))))
            throw new IllegalArgumentException("Incomplete Telegram configuration");
        inbox = base == null ? null : base.resolve("/account/notifications");
        try {
            fingerprint =
                    HexFormat.of()
                            .formatHex(
                                    MessageDigest.getInstance("SHA-256")
                                            .digest(
                                                    (token + "\n" + map + "\n" + site)
                                                            .getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Telegram fingerprint unavailable");
        }
    }

    boolean matches(Object active) {
        return configured && fingerprint.equals(active);
    }
}
