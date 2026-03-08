package com.snowk.blog.api.auth.presentation.controller;

import com.snowk.blog.api.auth.application.command.AdminLoginCommand;
import com.snowk.blog.api.auth.application.port.in.AdminLoginUseCase;
import com.snowk.blog.api.auth.application.result.AdminLoginResult;
import com.snowk.blog.api.auth.domain.error.AuthErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminAuthControllerTest {

    @Mock
    private AdminLoginUseCase adminLoginUseCase;

    @InjectMocks
    private AdminAuthController adminAuthController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(adminAuthController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .setMessageConverters(new JacksonJsonHttpMessageConverter())
            .build();
    }

    @Test
    @DisplayName("관리자 로그인 요청이 성공하면 access token 응답을 반환한다")
    void login_returnsAccessTokenResponse_whenRequestIsValid() throws Exception {
        Instant expiresAt = Instant.parse("2026-03-08T12:00:00Z");
        ArgumentCaptor<AdminLoginCommand> commandCaptor = ArgumentCaptor.forClass(AdminLoginCommand.class);

        when(adminLoginUseCase.login(commandCaptor.capture()))
            .thenReturn(new AdminLoginResult("access-token", expiresAt));

        mockMvc.perform(
                post("/api/admin/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "username": "admin",
                          "password": "plain-password"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.expiresAt").exists());

        assertThat(commandCaptor.getValue().username()).isEqualTo("admin");
        assertThat(commandCaptor.getValue().password()).isEqualTo("plain-password");
    }

    @Test
    @DisplayName("관리자 로그인 요청 본문이 유효하지 않으면 400 응답을 반환한다")
    void login_returnsBadRequest_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(
                post("/api/admin/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "username": "",
                          "password": "plain-password"
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("COMMON400"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("관리자 로그인에 실패하면 401 응답을 반환한다")
    void login_returnsUnauthorized_whenUseCaseThrowsAuthException() throws Exception {
        when(adminLoginUseCase.login(org.mockito.ArgumentMatchers.any(AdminLoginCommand.class)))
            .thenThrow(new BaseException(AuthErrorStatus.ADMIN_LOGIN_FAILED));

        mockMvc.perform(
                post("/api/admin/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "username": "admin",
                          "password": "wrong-password"
                        }
                        """)
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTH4011"))
            .andExpect(jsonPath("$.isSuccess").value(false));

        verify(adminLoginUseCase).login(org.mockito.ArgumentMatchers.any(AdminLoginCommand.class));
    }
}
