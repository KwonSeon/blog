package com.snowk.blog.api.auth.presentation.controller;

import com.snowk.blog.api.auth.application.command.AdminLoginCommand;
import com.snowk.blog.api.auth.application.port.in.AdminLoginUseCase;
import com.snowk.blog.api.auth.application.result.AdminLoginResult;
import com.snowk.blog.api.auth.presentation.dto.request.AdminLoginRequest;
import com.snowk.blog.api.auth.presentation.dto.response.AdminLoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminLoginUseCase adminLoginUseCase;

    @PostMapping("/login")
    public AdminLoginResponse login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResult result = adminLoginUseCase.login(
            new AdminLoginCommand(request.username(), request.password())
        );

        return new AdminLoginResponse(result.accessToken(), result.expiresAt());
    }
}
