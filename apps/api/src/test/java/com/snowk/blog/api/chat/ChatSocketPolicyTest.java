package com.snowk.blog.api.chat;

import static org.junit.jupiter.api.Assertions.*;

import com.snowk.blog.api.global.security.converter.JwtAuthenticatedUserConverter;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

class ChatSocketPolicyTest {
    final ChatSocketPolicy policy = new ChatSocketPolicy();

    StompHeaderAccessor frame(
            StompCommand command, String destination, Map<String, Object> attrs, boolean expired) {
        var a = StompHeaderAccessor.create(command);
        a.setDestination(destination);
        a.setSessionAttributes(attrs);
        a.setSubscriptionId("one");
        a.setUser(
                new JwtAuthenticatedUserConverter("https://identity.test", "")
                        .convert(
                                Jwt.withTokenValue("synthetic")
                                        .header("alg", "RS256")
                                        .subject("10")
                                        .expiresAt(Instant.now().plusSeconds(expired ? -1 : 60))
                                        .build()));
        return a;
    }

    void run(StompHeaderAccessor a) {
        policy.preSend(
                MessageBuilder.createMessage(new byte[0], a.getMessageHeaders()),
                new ExecutorSubscribableChannel());
    }

    @Test
    void permitsOnlyAuthenticatedSharedTopicSubscription() {
        var attrs = new HashMap<String, Object>();
        run(frame(StompCommand.CONNECT, null, attrs, false));
        run(frame(StompCommand.SUBSCRIBE, "/topic/member-chat", attrs, false));
        assertThrows(
                AccessDeniedException.class,
                () -> run(frame(StompCommand.SEND, "/topic/member-chat", attrs, false)));
        assertThrows(
                AccessDeniedException.class,
                () -> run(frame(StompCommand.SUBSCRIBE, "/topic/other", new HashMap<>(), false)));
        assertThrows(
                AccessDeniedException.class,
                () ->
                        run(
                                frame(
                                        StompCommand.SUBSCRIBE,
                                        "/topic/member-chat",
                                        new HashMap<>(),
                                        true)));
    }

    @Test
    void fakeUnsubscribeDoesNotBypassOneSubscriptionLimit() {
        var attrs = new HashMap<String, Object>();
        run(frame(StompCommand.SUBSCRIBE, "/topic/member-chat", attrs, false));
        var fake = frame(StompCommand.UNSUBSCRIBE, null, attrs, false);
        fake.setSubscriptionId("different");
        run(fake);
        assertThrows(
                AccessDeniedException.class,
                () -> run(frame(StompCommand.SUBSCRIBE, "/topic/member-chat", attrs, false)));
        run(frame(StompCommand.UNSUBSCRIBE, null, attrs, false));
        run(frame(StompCommand.SUBSCRIBE, "/topic/member-chat", attrs, false));
    }

    @Test
    void rejectsAnonymousAndInvalidOriginConfiguration() {
        var a = StompHeaderAccessor.create(StompCommand.CONNECT);
        assertThrows(AccessDeniedException.class, () -> run(a));
        for (String origin :
                List.of(
                        "*",
                        "http://chat.test",
                        "https://chat.test/path",
                        "https://chat.test/",
                        "https://chat.test?x=1"))
            assertThrows(IllegalArgumentException.class, () -> new ChatSettings(true, origin));
    }
}
