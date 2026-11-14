package com.snowk.blog.api.notification;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.snowk.blog.api.global.security.converter.JwtAuthenticatedUserConverter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationAuthorizationTest {
    @Autowired MockMvc mvc;
    @MockitoBean NotificationService service;

    private org.springframework.security.core.Authentication member() {
        return new JwtAuthenticatedUserConverter("https://identity.test", "")
                .convert(
                        Jwt.withTokenValue("test")
                                .header("alg", "RS256")
                                .subject("10")
                                .claim("merged_subjects", List.of("9"))
                                .build());
    }

    @Test
    void anonymousCannotReadOrMark() throws Exception {
        mvc.perform(get("/api/notifications")).andExpect(status().isUnauthorized());
        mvc.perform(patch("/api/notifications/1/read")).andExpect(status().isUnauthorized());
        mvc.perform(
                        post("/api/notifications/read-all")
                                .contentType("application/json")
                                .content("{\"through\":\"1\"}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test
    void onlySignedSubjectsDetermineOwnershipAndLargeIdsRemainExact() throws Exception {
        when(service.list(List.of("10", "9"), 0))
                .thenReturn(new NotificationService.Page(List.of(), null, "9007199254741000", 2));
        mvc.perform(get("/api/notifications?identitySubject=999").with(authentication(member())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshotId").value("9007199254741000"))
                .andExpect(header().string("Cache-Control", "private, no-store"));
        mvc.perform(
                        patch("/api/notifications/9007199254741000/read")
                                .with(authentication(member())))
                .andExpect(status().isOk());
        verify(service).read(List.of("10", "9"), 9007199254741000L);
        mvc.perform(
                        post("/api/notifications/read-all")
                                .with(authentication(member()))
                                .contentType("application/json")
                                .content(
                                        "{\"through\":\"9007199254741000\",\"identitySubject\":\"999\"}"))
                .andExpect(status().isOk());
        verify(service).readAll(List.of("10", "9"), 9007199254741000L);
    }
}
