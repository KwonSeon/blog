package com.snowk.blog.api.auth.presentation.dto.response;

import com.snowk.blog.api.auth.application.result.AdminLoginResult;
import java.time.Instant;

public record AdminLoginResponse(
    String accessToken,
    Instant expiresAt
) {
    public static AdminLoginResponse from(AdminLoginResult result) {
        return new AdminLoginResponse(result.accessToken(), result.expiresAt());
    }
}
