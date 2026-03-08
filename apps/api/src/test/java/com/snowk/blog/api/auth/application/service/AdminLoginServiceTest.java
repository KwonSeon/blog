package com.snowk.blog.api.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.auth.application.command.AdminLoginCommand;
import com.snowk.blog.api.auth.application.result.AdminLoginResult;
import com.snowk.blog.api.auth.domain.error.AuthErrorStatus;
import com.snowk.blog.api.global.config.properties.JwtProperties;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.user.application.port.out.UserRepositoryPort;
import com.snowk.blog.api.user.domain.entity.User;
import com.snowk.blog.api.user.domain.enumtype.UserRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@ExtendWith(MockitoExtension.class)
class AdminLoginServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    private AdminLoginService adminLoginService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("https://auth.test.s-nowk.com");
        jwtProperties.setSecretKey("test-jwt-secret-key-0123456789abcd");

        JwtProperties.AccessToken accessToken = new JwtProperties.AccessToken();
        accessToken.setExpiretime(30L);
        jwtProperties.setAccessToken(accessToken);

        adminLoginService = new AdminLoginService(
            userRepositoryPort,
            passwordEncoder,
            jwtEncoder,
            jwtProperties
        );
    }

    @Test
    @DisplayName("관리자 로그인 성공 시 access token과 만료 시각을 반환한다")
    void login_returnsAccessToken_whenAdminCredentialsAreValid() {
        AdminLoginCommand command = new AdminLoginCommand("admin", "plain-password");
        User user = mockUser(1L, "admin", "hashed-password", UserRole.ADMIN);
        Jwt jwt = Jwt.withTokenValue("access-token")
            .header("alg", "HS256")
            .claim("sub", "1")
            .build();
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);

        when(userRepositoryPort.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", "hashed-password")).thenReturn(true);
        when(jwtEncoder.encode(parametersCaptor.capture())).thenReturn(jwt);

        AdminLoginResult result = adminLoginService.login(command);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.expiresAt()).isNotNull();

        JwtClaimsSet claims = parametersCaptor.getValue().getClaims();
        assertThat(claims.getIssuer().toString()).isEqualTo("https://auth.test.s-nowk.com");
        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.getClaims()).containsEntry("username", "admin");
        assertThat(claims.getClaims()).containsEntry("roles", List.of("ROLE_ADMIN"));
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiresAt()).isNotNull();

        verify(userRepositoryPort).findByUsername("admin");
        verify(passwordEncoder).matches("plain-password", "hashed-password");
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    @DisplayName("사용자를 찾지 못하면 관리자 로그인에 실패한다")
    void login_throwsException_whenUserNotFound() {
        AdminLoginCommand command = new AdminLoginCommand("admin", "plain-password");
        when(userRepositoryPort.findByUsername("admin")).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> adminLoginService.login(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.ADMIN_LOGIN_FAILED);
        verify(userRepositoryPort).findByUsername("admin");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
    }

    @Test
    @DisplayName("관리자 권한이 아니면 관리자 로그인에 실패한다")
    void login_throwsException_whenUserRoleIsNotAdmin() {
        AdminLoginCommand command = new AdminLoginCommand("member", "plain-password");
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getRole()).thenReturn(UserRole.USER);

        when(userRepositoryPort.findByUsername("member")).thenReturn(Optional.of(user));

        BaseException exception = catchThrowableOfType(
            () -> adminLoginService.login(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.ADMIN_LOGIN_FAILED);
        verify(userRepositoryPort).findByUsername("member");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
    }

    @Test
    @DisplayName("비밀번호가 다르면 관리자 로그인에 실패한다")
    void login_throwsException_whenPasswordDoesNotMatch() {
        AdminLoginCommand command = new AdminLoginCommand("admin", "wrong-password");
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getRole()).thenReturn(UserRole.ADMIN);
        when(user.getPasswordHash()).thenReturn("hashed-password");

        when(userRepositoryPort.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        BaseException exception = catchThrowableOfType(
            () -> adminLoginService.login(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus.ADMIN_LOGIN_FAILED);
        verify(userRepositoryPort).findByUsername("admin");
        verify(passwordEncoder).matches("wrong-password", "hashed-password");
        verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
    }

    private User mockUser(Long userId, String username, String passwordHash, UserRole role) {
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getUserId()).thenReturn(userId);
        when(user.getUsername()).thenReturn(username);
        when(user.getPasswordHash()).thenReturn(passwordHash);
        when(user.getRole()).thenReturn(role);
        return user;
    }
}
