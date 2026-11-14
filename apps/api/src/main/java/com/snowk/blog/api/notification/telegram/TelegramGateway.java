package com.snowk.blog.api.notification.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;

@Component
public class TelegramGateway {
    private final TelegramSettings settings;
    private final HttpClient client;
    private final ObjectMapper json = new ObjectMapper();

    @Autowired
    public TelegramGateway(TelegramSettings settings) {
        this(
                settings,
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(3))
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .build());
    }

    public TelegramGateway(TelegramSettings settings, HttpClient client) {
        this.settings = settings;
        this.client = client;
    }

    public record Result(String state, String code, long retrySeconds) {}

    public Result send(String subject) {
        if (!settings.configured || !settings.recipients.containsKey(subject))
            return new Result("SKIPPED", "NOT_CONFIGURED", 0);
        try {
            // Only a generic inbox link leaves the service; no title/body/name/account subject.
            var payload =
                    Map.of(
                            "chat_id",
                            settings.recipients.get(subject),
                            "text",
                            "블로그에 새 알림이 있습니다.\n" + settings.inbox,
                            "link_preview_options",
                            Map.of("is_disabled", true),
                            "allow_paid_broadcast",
                            false);
            var request =
                    HttpRequest.newBuilder(
                                    URI.create(
                                            "https://api.telegram.org/bot"
                                                    + settings.token
                                                    + "/sendMessage"))
                            .timeout(Duration.ofSeconds(8))
                            .header("Content-Type", "application/json")
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            json.writeValueAsString(payload)))
                            .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().length() > 65536)
                return new Result("UNKNOWN", "OVERSIZE_RESPONSE", 0);
            var body = json.readTree(response.body());
            if (response.statusCode() == 200 && body.path("ok").asBoolean())
                return new Result("SENT", "OK", 0);
            if (response.statusCode() == 429 || body.path("error_code").asInt() == 429)
                return new Result(
                        "QUEUED",
                        "RATE_LIMIT",
                        Math.max(
                                5,
                                Math.min(
                                        3600,
                                        body.path("parameters").path("retry_after").asLong(60))));
            if (response.statusCode() >= 400 && response.statusCode() < 500)
                return new Result("FAILED", "HTTP_" + response.statusCode(), 0);
            return new Result("UNKNOWN", "AMBIGUOUS_RESPONSE", 0);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return new Result("UNKNOWN", "INTERRUPTED", 0);
        } catch (Exception ignored) {
            return new Result("UNKNOWN", "TRANSPORT_OR_PARSE", 0);
        }
        // Never log exception text: a provider URL includes the bot secret.
    }
}
