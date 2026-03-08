package com.snowk.blog.api.global.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    @NotBlank
    private String issuer;

    @NotBlank
    @Size(min = 32)
    private String secretKey;

    @Valid
    private AccessToken accessToken = new AccessToken();

    @Getter
    @Setter
    public static class AccessToken {

        @NotNull
        @Min(1)
        private Long expiretime;
    }
}
