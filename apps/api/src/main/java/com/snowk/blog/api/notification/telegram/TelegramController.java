package com.snowk.blog.api.notification.telegram;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications/telegram")
public class TelegramController {
    private final TelegramDelivery delivery;

    public TelegramController(TelegramDelivery delivery) {
        this.delivery = delivery;
    }

    @ModelAttribute
    public void privateResponse(HttpServletResponse response) {
        response.setHeader("Cache-Control", "private, no-store");
    }

    @GetMapping
    public TelegramDelivery.Status status() {
        return delivery.status();
    }

    public record Activation(Boolean enabled, Boolean confirmed) {}

    @PutMapping
    public TelegramDelivery.Status activate(@RequestBody Activation input) {
        if (input.enabled() == null || (input.enabled() && !Boolean.TRUE.equals(input.confirmed())))
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        return delivery.setActive(input.enabled());
    }
}
