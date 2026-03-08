package com.snowk.blog.api.auth.application.result;

import java.time.Instant;

public record AdminLoginResult(
    String accessToken,
    Instant expiresAt
) {
}
