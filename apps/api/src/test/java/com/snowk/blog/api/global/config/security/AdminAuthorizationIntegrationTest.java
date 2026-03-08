package com.snowk.blog.api.global.config.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snowk.blog.api.global.config.properties.JwtProperties;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AdminAuthorizationIntegrationTest.TestAdminController.class)
class AdminAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    @DisplayName("관리자 보호구역은 토큰이 없으면 401 응답을 반환한다")
    void adminEndpoint_returnsUnauthorized_whenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/admin/test/ping"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("관리자 보호구역은 ADMIN 권한이 없으면 403 응답을 반환한다")
    void adminEndpoint_returnsForbidden_whenRoleIsNotAdmin() throws Exception {
        String accessToken = createAccessToken("2", "writer", List.of("ROLE_USER"));

        mockMvc.perform(
                get("/api/admin/test/ping")
                    .header(AUTHORIZATION, "Bearer " + accessToken)
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 보호구역은 ADMIN 권한이 있으면 200 응답을 반환한다")
    void adminEndpoint_returnsOk_whenRoleIsAdmin() throws Exception {
        String accessToken = createAccessToken("1", "admin", List.of("ROLE_ADMIN"));

        mockMvc.perform(
                get("/api/admin/test/ping")
                    .header(AUTHORIZATION, "Bearer " + accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(content().string("pong"));
    }

    private String createAccessToken(String subject, String username, List<String> roles) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(jwtProperties.getIssuer())
            .subject(subject)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(1800))
            .claim("username", username)
            .claim("roles", roles)
            .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @RestController
    static class TestAdminController {

        @GetMapping("/api/admin/test/ping")
        String ping() {
            return "pong";
        }
    }
}
