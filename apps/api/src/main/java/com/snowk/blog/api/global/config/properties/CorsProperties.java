package com.snowk.blog.api.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.cors.allow")
public class CorsProperties {

    // 허용 오리진 기본값: 로컬 개발용
    private String[] origins = new String[] {"http://localhost:3000"};
}
