package com.snowk.blog.api.chat;

import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;
import com.snowk.blog.api.reaction.ReactionService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/messages")
public class ChatController {
    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    private List<String> subjects(AuthenticatedUserAuthenticationToken user) {
        return ReactionService.subjects(
                user.getName(), user.getToken().getClaimAsStringList("merged_subjects"));
    }

    private boolean editor(AuthenticatedUserAuthenticationToken user) {
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @ModelAttribute
    public void cache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "private, no-store");
    }

    @GetMapping
    public ChatService.Page list(
            @RequestParam(defaultValue = "0") long before,
            AuthenticatedUserAuthenticationToken user) {
        return service.list(before, subjects(user), editor(user));
    }

    public record Create(String body, String requestKey) {}

    @PostMapping
    public ChatService.Item send(
            @RequestBody Create input, AuthenticatedUserAuthenticationToken user) {
        return service.send(
                subjects(user),
                user.getToken().getClaimAsString("nickname"),
                input.body(),
                input.requestKey(),
                editor(user));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id, AuthenticatedUserAuthenticationToken user) {
        service.delete(id, subjects(user), editor(user));
    }
}
