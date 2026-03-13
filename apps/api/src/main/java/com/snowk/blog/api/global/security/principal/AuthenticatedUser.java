package com.snowk.blog.api.global.security.principal;

import java.util.List;

public final class AuthenticatedUser {

    private final String subject;
    private final Long userId;
    private final String username;
    private final List<String> roles;

    public AuthenticatedUser(String subject, Long userId, String username, List<String> roles) {
        this.subject = subject;
        this.userId = userId;
        this.username = username;
        this.roles = roles == null ? List.of() : List.copyOf(roles);
    }

    public String getSubject() {
        return subject;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
