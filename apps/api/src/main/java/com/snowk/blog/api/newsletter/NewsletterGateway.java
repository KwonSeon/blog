package com.snowk.blog.api.newsletter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

@Component
public class NewsletterGateway {
    private final NewsletterSettings settings;
    private final HttpClient client;
    private final ObjectMapper json = new ObjectMapper();

    public NewsletterGateway(NewsletterSettings settings) {
        this.settings = settings;
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    public void send(String id, String recipient, String subject, String text, String unsubscribe)
            throws Exception {
        settings.requireEnabled();
        String url;
        Map<String, Object> body;
        boolean resend = settings.delivery().equals("resend");
        if (resend) {
            url = "https://api.resend.com/emails";
            body =
                    Map.of(
                            "from",
                            "s-nowk Blog <" + settings.from() + ">",
                            "to",
                            List.of(recipient),
                            "subject",
                            subject,
                            "text",
                            text);
        } else {
            URI uri = URI.create(settings.mailpitUrl());
            if (!"http".equals(uri.getScheme())
                    || uri.getHost() == null
                    || uri.getUserInfo() != null)
                throw new IllegalStateException("Invalid development mail endpoint");
            url = settings.mailpitUrl() + "/api/v1/send";
            body =
                    Map.of(
                            "From",
                            Map.of("Email", settings.from(), "Name", "s-nowk Blog"),
                            "To",
                            List.of(Map.of("Email", recipient)),
                            "Subject",
                            subject,
                            "Text",
                            text,
                            "Tags",
                            List.of("blog-newsletter"),
                            "Headers",
                            Map.of("X-Blog-Message-ID", id));
        }
        var request =
                HttpRequest.newBuilder(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)));
        if (resend)
            request.header("Authorization", "Bearer " + settings.resendKey())
                    .header("Idempotency-Key", "blog-newsletter/" + id);
        var response = client.send(request.build(), HttpResponse.BodyHandlers.discarding());
        if (response.statusCode() < 200 || response.statusCode() >= 300)
            throw new DeliveryFailure(response.statusCode());
    }

    public static class DeliveryFailure extends Exception {
        private final int status;

        public DeliveryFailure(int status) {
            super("Mail provider rejected request");
            this.status = status;
        }

        public int status() {
            return status;
        }
    }
}
