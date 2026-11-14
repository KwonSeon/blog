package com.snowk.blog.api.comment;

import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;
import com.snowk.blog.api.reaction.ReactionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    public record Create(String body, Long parentId, String requestKey) {}

    public record Edit(String body) {}

    private List<String> subjects(AuthenticatedUserAuthenticationToken a) {
        return a == null
                ? List.of()
                : ReactionService.subjects(
                        a.getName(), a.getToken().getClaimAsStringList("merged_subjects"));
    }

    private boolean editor(AuthenticatedUserAuthenticationToken a) {
        return a != null
                && a.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping("/api/public/comments/{kind}/{id}")
    public CommentService.Page list(
            @PathVariable String kind,
            @PathVariable long id,
            @RequestParam(defaultValue = "0") long after,
            AuthenticatedUserAuthenticationToken a) {
        return service.list(kind, id, after, subjects(a), editor(a));
    }

    @PostMapping("/api/comments/{kind}/{id}")
    public CommentService.Item create(
            @PathVariable String kind,
            @PathVariable long id,
            @RequestBody Create input,
            AuthenticatedUserAuthenticationToken a) {
        return service.create(
                kind,
                id,
                subjects(a),
                a.getToken().getClaimAsString("nickname"),
                input.body(),
                input.parentId(),
                input.requestKey());
    }

    @PatchMapping("/api/comments/{kind}/{id}/{comment}")
    public CommentService.Item edit(
            @PathVariable String kind,
            @PathVariable long id,
            @PathVariable long comment,
            @RequestBody Edit input,
            AuthenticatedUserAuthenticationToken a) {
        return service.edit(kind, id, comment, subjects(a), input.body());
    }

    @DeleteMapping("/api/comments/{kind}/{id}/{comment}")
    public CommentService.Item delete(
            @PathVariable String kind,
            @PathVariable long id,
            @PathVariable long comment,
            AuthenticatedUserAuthenticationToken a) {
        return service.delete(kind, id, comment, subjects(a), editor(a));
    }
}
