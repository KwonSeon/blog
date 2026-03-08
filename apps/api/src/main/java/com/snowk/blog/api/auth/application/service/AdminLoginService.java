package com.snowk.blog.api.auth.application.service;

import com.snowk.blog.api.auth.application.command.AdminLoginCommand;
import com.snowk.blog.api.auth.application.port.in.AdminLoginUseCase;
import com.snowk.blog.api.auth.application.result.AdminLoginResult;
import com.snowk.blog.api.auth.domain.error.AuthErrorStatus;
import com.snowk.blog.api.global.config.properties.JwtProperties;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.user.application.port.out.UserRepositoryPort;
import com.snowk.blog.api.user.domain.entity.User;
import com.snowk.blog.api.user.domain.enumtype.UserRole;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminLoginService implements AdminLoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    @Override
    public AdminLoginResult login(AdminLoginCommand command) {
        // 로그인 입력값이 비어 있으면 동일한 401 응답으로 처리한다.
        if (command == null
            || !StringUtils.hasText(command.username())
            || !StringUtils.hasText(command.password())) {
            throw new BaseException(AuthErrorStatus.ADMIN_LOGIN_FAILED);
        }

        User user = userRepositoryPort.findByUsername(command.username())
            .orElseThrow(() -> new BaseException(AuthErrorStatus.ADMIN_LOGIN_FAILED));

        // 현재 P0 범위에서는 ADMIN 계정만 관리자 로그인 대상으로 허용한다.
        if (user.getRole() != UserRole.ADMIN) {
            throw new BaseException(AuthErrorStatus.ADMIN_LOGIN_FAILED);
        }

        // 비밀번호는 평문 비교가 아니라 저장된 bcrypt hash와 matches로 검증한다.
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new BaseException(AuthErrorStatus.ADMIN_LOGIN_FAILED);
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getAccessToken().getExpiretime(), ChronoUnit.MINUTES);

        // 이후 resource server가 그대로 해석할 수 있도록 식별자와 권한 claim을 함께 담는다.
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(jwtProperties.getIssuer())
            .subject(String.valueOf(user.getUserId()))
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("username", user.getUsername())
            .claim("roles", List.of("ROLE_ADMIN"))
            .build();

        String accessToken = jwtEncoder.encode(
            JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
            )
        ).getTokenValue();

        return new AdminLoginResult(accessToken, expiresAt);
    }
}
