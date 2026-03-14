package com.snowk.blog.api.auth.presentation.dto.response;

import java.time.Instant;

public record AdminLoginResponse(
    String accessToken,
    Instant expiresAt
) {
}
