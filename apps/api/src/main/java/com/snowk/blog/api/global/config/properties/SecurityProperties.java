package com.snowk.blog.api.global.config.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.public")
public class SecurityProperties {

    // "경로:METHOD1,METHOD2" 형식의 permitAll 엔드포인트 목록
    private List<String> requestMatchers = new ArrayList<>();
}
