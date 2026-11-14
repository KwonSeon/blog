package com.snowk.blog.api.auth.presentation.controller;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class SharedSessionController {
    @GetMapping("/api/admin/session")
    public Map<String, Boolean> session() { return Map.of("editor", true); }
}
