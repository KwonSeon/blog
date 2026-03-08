package com.snowk.blog.api.auth.application.command;

public record AdminLoginCommand(
    String username,
    String password
) {
}
