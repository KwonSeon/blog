package com.snowk.blog.api.global.config.security;

import com.snowk.blog.api.global.config.properties.CorsProperties;
import com.snowk.blog.api.global.config.properties.SecurityProperties;
import com.snowk.blog.api.global.security.converter.JwtAuthenticatedUserConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticatedUserConverter jwtAuthenticatedUserConverter
    ) throws Exception {
        RequestMatcher[] permitAllMatchers = toRequestMatcher(securityProperties.getRequestMatchers());

        http
            // API 서버 기본 정책: 세션을 사용하지 않음
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 브라우저 폼 로그인/기본 인증을 사용하지 않음
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            // JWT 기반 API를 기준으로 CSRF 비활성화
            .csrf(csrf -> csrf.disable())
            // application.yaml의 허용 오리진 기반 CORS 적용
            .cors(Customizer.withDefaults())
            // Bearer 토큰을 읽어 JwtDecoder/JwtAuthenticationConverter로 인증 객체를 구성한다.
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticatedUserConverter))
            )
            .authorizeHttpRequests(auth -> auth
                // 환경설정으로 등록한 엔드포인트는 인증 없이 허용
                .requestMatchers(permitAllMatchers).permitAll()
                // 사전 요청(Preflight)은 항상 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 관리자 API는 로그인 토큰 안의 ROLE_ADMIN 권한이 있어야만 접근할 수 있다.
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 그 외는 인증만 통과하면 접근 가능하다.
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 프론트 도메인 허용 목록
        config.setAllowedOrigins(List.of(corsProperties.getOrigins()));
        // 기본 HTTP 메서드 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 요청 헤더 전체 허용
        config.setAllowedHeaders(List.of("*"));
        // 인증 헤더/쿠키를 포함할 수 있도록 허용
        config.setAllowCredentials(true);
        // 브라우저 preflight 캐시 시간(초)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private RequestMatcher[] toRequestMatcher(List<String> urlMethods) {
        if (urlMethods == null || urlMethods.isEmpty()) {
            return new RequestMatcher[0];
        }

        List<RequestMatcher> matchers = new ArrayList<>();

        for (String entry : urlMethods) {
            if (!StringUtils.hasText(entry) || !entry.contains(":")) {
                continue;
            }

            String[] parts = entry.split(":", 2);
            String url = parts[0].trim();
            String methodSegment = parts[1].trim();

            if (!StringUtils.hasText(url) || !StringUtils.hasText(methodSegment)) {
                continue;
            }

            Stream.of(methodSegment.split("\\s*,\\s*"))
                .filter(StringUtils::hasText)
                // "경로 + HTTP 메서드" 단위로 매칭 객체 생성
                .map(method -> PathPatternRequestMatcher.pathPattern(HttpMethod.valueOf(method.trim()), url))
                .forEach(matchers::add);
        }

        return matchers.toArray(RequestMatcher[]::new);
    }
}
