package com.snowk.blog.api.newsletter;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public record NewsletterSettings(
        boolean enabled,
        String secret,
        String siteUrl,
        String from,
        String delivery,
        String resendKey,
        String mailpitUrl) {
    public NewsletterSettings(
            @Value("${BLOG_NEWSLETTER_ENABLED:false}") boolean enabled,
            @Value("${BLOG_NEWSLETTER_SECRET:}") String secret,
            @Value("${BLOG_NEWSLETTER_SITE_URL:}") String siteUrl,
            @Value("${BLOG_NEWSLETTER_FROM:}") String from,
            @Value("${BLOG_NEWSLETTER_DELIVERY:off}") String delivery,
            @Value("${BLOG_NEWSLETTER_RESEND_KEY:}") String resendKey,
            @Value("${BLOG_NEWSLETTER_MAILPIT_URL:}") String mailpitUrl) {
        this.enabled = enabled;
        this.secret = secret;
        this.siteUrl = siteUrl;
        this.from = from;
        this.delivery = delivery;
        this.resendKey = resendKey;
        this.mailpitUrl = mailpitUrl;
    }

    public void requireEnabled() {
        if (!enabled || secret.length() < 32 || !from.matches("[^\\s<>@]+@[^\\s<>@]+\\.[^\\s<>@]+"))
            throw new BaseException(CommonErrorStatus.SERVICE_UNAVAILABLE);
        URI uri = URI.create(siteUrl);
        if (!"https".equals(uri.getScheme())
                || uri.getHost() == null
                || uri.getUserInfo() != null
                || uri.getQuery() != null
                || uri.getFragment() != null
                || !uri.getPath().isEmpty())
            throw new BaseException(CommonErrorStatus.SERVICE_UNAVAILABLE);
        if (!delivery.equals("resend") && !delivery.equals("mailpit"))
            throw new BaseException(CommonErrorStatus.SERVICE_UNAVAILABLE);
        if (delivery.equals("mailpit")) {
            URI mail = URI.create(mailpitUrl);
            if (!"http".equals(mail.getScheme())
                    || mail.getHost() == null
                    || mail.getUserInfo() != null
                    || mail.getQuery() != null
                    || mail.getFragment() != null
                    || !mail.getPath().isEmpty())
                throw new BaseException(CommonErrorStatus.SERVICE_UNAVAILABLE);
        }
        if (delivery.equals("resend") && resendKey.isBlank())
            throw new BaseException(CommonErrorStatus.SERVICE_UNAVAILABLE);
    }

    public boolean ready() {
        try {
            requireEnabled();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
