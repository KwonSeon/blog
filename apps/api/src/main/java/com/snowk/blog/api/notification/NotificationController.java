package com.snowk.blog.api.notification;

import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;
import com.snowk.blog.api.reaction.ReactionService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    private List<String> subjects(AuthenticatedUserAuthenticationToken user) {
        return ReactionService.subjects(
                user.getName(), user.getToken().getClaimAsStringList("merged_subjects"));
    }

    @GetMapping
    public NotificationService.Page list(
            @RequestParam(defaultValue = "0") long before,
            AuthenticatedUserAuthenticationToken user) {
        return service.list(subjects(user), before);
    }

    @ModelAttribute
    public void privateResponse(HttpServletResponse response) {
        response.setHeader("Cache-Control", "private, no-store");
    }

    public record ReadResult(long unreadCount) {}

    @PatchMapping("/{id}/read")
    public ReadResult read(@PathVariable long id, AuthenticatedUserAuthenticationToken user) {
        service.read(subjects(user), id);
        return new ReadResult(service.unread(subjects(user)));
    }

    public record ReadAll(long through) {}

    @PostMapping("/read-all")
    public ReadResult readAll(
            @RequestBody ReadAll input, AuthenticatedUserAuthenticationToken user) {
        service.readAll(subjects(user), input.through());
        return new ReadResult(service.unread(subjects(user)));
    }
}
