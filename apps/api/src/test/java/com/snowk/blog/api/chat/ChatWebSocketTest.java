package com.snowk.blog.api.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"BLOG_CHAT_ENABLED=true", "BLOG_CHAT_ORIGIN=https://chat.test"})
@ActiveProfiles("test")
class ChatWebSocketTest {
    @LocalServerPort int port;
    @MockitoBean JwtDecoder decoder;
    @MockitoBean ChatService service;
    @Autowired SimpMessagingTemplate template;
    @Autowired SimpUserRegistry users;
    @Autowired ChatWebSocketConfig sockets;
    @Autowired org.springframework.context.ApplicationContext context;

    @Test
    void heartbeatSchedulerCannotBeSelectedForOrdinaryBackgroundJobs() {
        assertFalse(
                context.getBeansOfType(org.springframework.scheduling.TaskScheduler.class)
                        .values()
                        .contains(sockets.chatHeartbeat()));
    }

    final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    @BeforeEach
    void token() {
        when(decoder.decode("synthetic-member"))
                .thenReturn(
                        Jwt.withTokenValue("synthetic-member")
                                .header("alg", "RS256")
                                .subject("10")
                                .claim("roles", List.of("ROLE_USER"))
                                .expiresAt(Instant.now().plusSeconds(60))
                                .build());
    }

    static class Frames implements WebSocket.Listener {
        final BlockingQueue<String> frames = new LinkedBlockingQueue<>();
        final StringBuilder pending = new StringBuilder();

        public void onOpen(WebSocket socket) {
            socket.request(1);
        }

        public CompletionStage<?> onText(WebSocket socket, CharSequence text, boolean last) {
            pending.append(text);
            if (last) {
                frames.add(pending.toString());
                pending.setLength(0);
            }
            socket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        String next() throws Exception {
            String frame = frames.poll(5, TimeUnit.SECONDS);
            assertNotNull(frame);
            return frame;
        }
    }

    URI url() {
        return URI.create("ws://localhost:" + port + "/api/chat/ws");
    }

    @Test
    void realHandshakeCookieStompSubscriptionAndBrokerHint() throws Exception {
        var frames = new Frames();
        var socket =
                client.newWebSocketBuilder()
                        .header("Origin", "https://chat.test")
                        .header("Cookie", "blog.access=synthetic-member")
                        .subprotocols("v12.stomp")
                        .buildAsync(url(), frames)
                        .get(5, TimeUnit.SECONDS);
        try {
            socket.sendText("CONNECT\naccept-version:1.2\nheart-beat:0,0\n\n\0", true).join();
            assertTrue(frames.next().startsWith("CONNECTED"));
            socket.sendText(
                            "SUBSCRIBE\nid:chat\ndestination:/topic/member-chat\nack:auto\n\n\0",
                            true)
                    .join();
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (System.nanoTime() < deadline
                    && (users.getUser("10") == null
                            || users.getUser("10").getSessions().stream()
                                    .allMatch(s -> s.getSubscriptions().isEmpty())))
                Thread.sleep(10);
            assertNotNull(users.getUser("10"));
            assertTrue(
                    users.getUser("10").getSessions().stream()
                            .anyMatch(s -> !s.getSubscriptions().isEmpty()));
            template.convertAndSend("/topic/member-chat", (Object) Map.of("changed", true));
            var hint = frames.next();
            assertTrue(hint.startsWith("MESSAGE"));
            assertTrue(hint.contains("changed"));
            socket.sendText("SEND\ndestination:/topic/member-chat\n\nforged\0", true).join();
            assertTrue(frames.next().startsWith("ERROR"));
        } finally {
            socket.abort();
        }
    }

    @Test
    void missingCookieOrWrongOriginCannotUpgrade() throws Exception {
        for (boolean wrongOrigin : List.of(false, true)) {
            var builder =
                    client.newWebSocketBuilder()
                            .header(
                                    "Origin",
                                    wrongOrigin ? "https://evil.test" : "https://chat.test");
            if (wrongOrigin) builder.header("Cookie", "blog.access=synthetic-member");
            var failure =
                    assertThrows(
                            ExecutionException.class,
                            () -> builder.buildAsync(url(), new Frames()).get(5, TimeUnit.SECONDS));
            assertInstanceOf(WebSocketHandshakeException.class, failure.getCause());
            int status =
                    ((WebSocketHandshakeException) failure.getCause()).getResponse().statusCode();
            assertEquals(wrongOrigin ? 403 : 401, status);
        }
    }

    @Test
    void cookieAuthenticationIsNotEnabledForOrdinaryHttpApi() throws Exception {
        var response =
                client.send(
                        HttpRequest.newBuilder(
                                        URI.create(
                                                "http://localhost:" + port + "/api/chat/messages"))
                                .header("Cookie", "blog.access=synthetic-member")
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.discarding());
        assertEquals(401, response.statusCode());
    }
}
