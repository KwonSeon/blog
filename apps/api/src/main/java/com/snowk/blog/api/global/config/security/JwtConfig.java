package com.snowk.blog.api.global.config.security;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.snowk.blog.api.global.config.properties.JwtProperties;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public JwtEncoder jwtEncoder() {
        // 로그인 시 access token을 서명해서 발급할 때 사용하는 인코더다.
        SecretKey secretKey = new SecretKeySpec(
            jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );

        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(
        @Value("${BLOG_IDENTITY_ISSUER:}") String issuer,
        @Value("${BLOG_IDENTITY_JWKS_URI:}") String jwks,
        @Value("${BLOG_IDENTITY_AUDIENCE:blog-api}") String audience
    ) {
        if (!issuer.isBlank()) {
            if (!issuer.startsWith("https://") || jwks.isBlank() || audience.isBlank())
                throw new IllegalArgumentException("Identity issuer, JWKS and audience are required");
            var decoder = NimbusJwtDecoder.withJwkSetUri(jwks).build();
            OAuth2TokenValidator<Jwt> audienceValidator = token -> token.getAudience().contains(audience)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience", null));
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), audienceValidator));
            return decoder;
        }
        // 이후 resource server가 Bearer 토큰을 검증할 때 같은 비밀키를 사용한다.
        SecretKey secretKey = new SecretKeySpec(
            jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );

        return NimbusJwtDecoder.withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    }
}
