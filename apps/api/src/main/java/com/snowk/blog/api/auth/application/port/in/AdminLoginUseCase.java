package com.snowk.blog.api.auth.application.port.in;

import com.snowk.blog.api.auth.application.command.AdminLoginCommand;
import com.snowk.blog.api.auth.application.result.AdminLoginResult;

public interface AdminLoginUseCase {

    AdminLoginResult login(AdminLoginCommand command);
}
