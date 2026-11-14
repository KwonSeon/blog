package com.snowk.blog.api.comment;

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
class CommentAuthorizationTest {
    @Autowired MockMvc mvc;
    @MockitoBean CommentService service;

    private org.springframework.security.core.Authentication member(String bindings) {
        return new JwtAuthenticatedUserConverter("https://identity.test", bindings)
                .convert(
                        Jwt.withTokenValue("test")
                                .header("alg", "RS256")
                                .subject("10")
                                .claim("merged_subjects", List.of("9"))
                                .claim("roles", List.of("ROLE_ADMIN"))
                                .build());
    }

    @Test
    void anonymousCanReadButNotMutate() throws Exception {
        when(service.list("posts", 1, 0, List.of(), false))
                .thenReturn(new CommentService.Page(List.of(), null, false));
        mvc.perform(get("/api/public/comments/posts/1")).andExpect(status().isOk());
        mvc.perform(post("/api/comments/posts/1").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/comments/posts/1/2")).andExpect(status().isUnauthorized());
    }

    @Test
    void memberSubjectAndAliasesComeOnlyFromSignedToken() throws Exception {
        mvc.perform(
                        post("/api/comments/posts/1")
                                .with(authentication(member("")))
                                .contentType("application/json")
                                .content(
                                        "{\"body\":\"hello\",\"parentId\":\"9007199254741000\",\"requestKey\":\"key\",\"identitySubject\":\"999\"}"))
                .andExpect(status().isOk());
        verify(service)
                .create("posts", 1, List.of("10", "9"), null, "hello", 9007199254741000L, "key");
    }

    @Test
    void issuerAdminClaimDoesNotGrantModerationWithoutLocalBinding() throws Exception {
        mvc.perform(delete("/api/comments/posts/1/2").with(authentication(member(""))))
                .andExpect(status().isOk());
        verify(service).delete("posts", 1, 2, List.of("10", "9"), false);
        mvc.perform(delete("/api/comments/posts/1/3").with(authentication(member("10:9002"))))
                .andExpect(status().isOk());
        verify(service).delete("posts", 1, 3, List.of("10", "9"), true);
    }
}
