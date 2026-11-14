package com.snowk.blog.api.newsletter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class NewsletterController {
    private final NewsletterSettings settings;
    private final NewsletterCrypto crypto;
    private final NewsletterService service;
    private final ObjectMapper json = new ObjectMapper();

    public NewsletterController(
            NewsletterSettings settings, NewsletterCrypto crypto, NewsletterService service) {
        this.settings = settings;
        this.crypto = crypto;
        this.service = service;
    }

    public record Envelope(String data, String client, long timestamp, String signature) {}

    @GetMapping("/api/public/newsletter/status")
    public Map<String, Boolean> status() {
        return Map.of("enabled", settings.ready());
    }

    @PostMapping("/api/public/newsletter/{action}")
    public Map<String, Boolean> action(@PathVariable String action, @RequestBody Envelope input) {
        if (!java.util.Set.of("subscribe", "confirm", "unsubscribe").contains(action))
            throw new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND);
        crypto.verify(action, input.data(), input.client(), input.timestamp(), input.signature());
        try {
            var data = json.readTree(input.data());
            if (data == null || !data.isObject())
                throw new BaseException(CommonErrorStatus.BAD_REQUEST);
            switch (action) {
                case "subscribe" ->
                        service.subscribe(
                                data.path("email").asText(null),
                                input.client(),
                                data.path("consent").asBoolean(false));
                case "confirm" -> service.confirm(data.path("token").asText(null));
                case "unsubscribe" -> service.unsubscribe(data.path("token").asText(null));
            }
            return Map.of("accepted", true);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }
    }
}
