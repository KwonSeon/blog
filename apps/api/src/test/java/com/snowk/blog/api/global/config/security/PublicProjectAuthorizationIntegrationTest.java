package com.snowk.blog.api.global.config.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicProjectAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("공개 프로젝트 목록 엔드포인트는 토큰이 없어도 200 응답을 반환한다")
    void publicListEndpoint_returnsOk_whenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공개 프로젝트 상세 엔드포인트는 토큰이 없어도 401이 아니라 404 응답을 반환한다")
    void publicDetailEndpoint_returnsNotFound_whenTokenIsMissingAndProjectDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/projects/not-found"))
            .andExpect(status().isNotFound());
    }
}
