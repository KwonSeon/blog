package com.snowk.blog.api.chat;

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
class ChatAuthorizationTest {
    @Autowired MockMvc mvc;
    @MockitoBean ChatService service;

    org.springframework.security.core.Authentication member(boolean editor) {
        return new JwtAuthenticatedUserConverter("https://identity.test", editor ? "10:9002" : "")
                .convert(
                        Jwt.withTokenValue("synthetic")
                                .header("alg", "RS256")
                                .subject("10")
                                .claim("merged_subjects", List.of("9"))
                                .claim("nickname", "회원")
                                .build());
    }

    @Test
    void anonymousCannotReadSendOrDelete() throws Exception {
        mvc.perform(get("/api/chat/messages")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/chat/messages").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/chat/messages/1")).andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test
    void usesSignedRolesAndAliasesOnly() throws Exception {
        when(service.list(0, List.of("10", "9"), false))
                .thenReturn(new ChatService.Page(List.of(), null, true));
        mvc.perform(get("/api/chat/messages?editor=true").with(authentication(member(false))))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "private, no-store"));
        mvc.perform(
                        delete("/api/chat/messages/9007199254741000")
                                .with(authentication(member(false))))
                .andExpect(status().isOk());
        verify(service).delete(9007199254741000L, List.of("10", "9"), false);
        mvc.perform(
                        delete("/api/chat/messages/9007199254741000")
                                .with(authentication(member(true))))
                .andExpect(status().isOk());
        verify(service).delete(9007199254741000L, List.of("10", "9"), true);
    }
}
