package com.snowk.blog.api.reaction;

import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReactionController {
    private final ReactionService service;
    public ReactionController(ReactionService service) { this.service = service; }
    public record Change(@NotNull Boolean liked) {}
    private List<String> subjects(AuthenticatedUserAuthenticationToken authentication) {
        return authentication == null ? List.of() : ReactionService.subjects(authentication.getName(), authentication.getToken().getClaimAsStringList("merged_subjects"));
    }
    @GetMapping("/api/public/reactions/{kind}/{id}")
    public ReactionService.State read(@PathVariable String kind, @PathVariable long id, AuthenticatedUserAuthenticationToken authentication) {
        return service.read(kind, id, subjects(authentication));
    }
    @PutMapping("/api/reactions/{kind}/{id}")
    public ReactionService.State set(@PathVariable String kind, @PathVariable long id, @Valid @RequestBody Change body, AuthenticatedUserAuthenticationToken authentication) {
        return service.set(kind, id, subjects(authentication), body.liked());
    }
}
