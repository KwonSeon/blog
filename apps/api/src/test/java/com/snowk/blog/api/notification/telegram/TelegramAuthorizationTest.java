package com.snowk.blog.api.notification.telegram;

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

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TelegramAuthorizationTest {
    @Autowired MockMvc mvc;
    @MockitoBean TelegramDelivery delivery;

    org.springframework.security.core.Authentication user(boolean editor) {
        return new JwtAuthenticatedUserConverter("https://identity.test", editor ? "10:9002" : "")
                .convert(Jwt.withTokenValue("test").header("alg", "RS256").subject("10").build());
    }

    final String path = "/api/admin/notifications/telegram";

    @Test
    void anonymousAndOrdinaryMembersCannotInspectOrActivate() throws Exception {
        mvc.perform(get(path)).andExpect(status().isUnauthorized());
        mvc.perform(get(path).with(authentication(user(false)))).andExpect(status().isForbidden());
        mvc.perform(
                        put(path)
                                .with(authentication(user(false)))
                                .contentType("application/json")
                                .content("{\"enabled\":true,\"confirmed\":true}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(delivery);
    }

    @Test
    void activationRequiresExplicitConfirmationAndStatusIsPrivate() throws Exception {
        when(delivery.status())
                .thenReturn(new TelegramDelivery.Status(false, false, false, 0, Map.of()));
        mvc.perform(get(path).with(authentication(user(true))))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "private, no-store"));
        mvc.perform(
                        put(path)
                                .with(authentication(user(true)))
                                .contentType("application/json")
                                .content("{\"enabled\":true}"))
                .andExpect(status().isBadRequest());
        verify(delivery, never()).setActive(anyBoolean());
        mvc.perform(
                        put(path)
                                .with(authentication(user(true)))
                                .contentType("application/json")
                                .content("{\"enabled\":false}"))
                .andExpect(status().isOk());
        verify(delivery).setActive(false);
        mvc.perform(
                        put(path)
                                .with(authentication(user(true)))
                                .contentType("application/json")
                                .content("{\"enabled\":true,\"confirmed\":true}"))
                .andExpect(status().isOk());
        verify(delivery).setActive(true);
    }
}
