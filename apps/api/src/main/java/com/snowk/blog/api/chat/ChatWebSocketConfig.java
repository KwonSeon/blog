package com.snowk.blog.api.chat;

import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.http.server.*;
import org.springframework.messaging.simp.config.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocketMessageBroker
@ConditionalOnProperty(name = "BLOG_CHAT_ENABLED", havingValue = "true")
public class ChatWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ChatSettings settings;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public ChatWebSocketConfig(ChatSettings settings) {
        this.settings = settings;
    }

    @Bean
    public ThreadPoolTaskScheduler chatHeartbeat() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("chat-heartbeat-");
        return scheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/chat/ws")
                .setAllowedOrigins(settings.origin)
                .addInterceptors(
                        new HandshakeInterceptor() {
                            public boolean beforeHandshake(
                                    ServerHttpRequest request,
                                    ServerHttpResponse response,
                                    WebSocketHandler handler,
                                    Map<String, Object> attributes) {
                                boolean valid =
                                        settings.origin.equals(request.getHeaders().getOrigin())
                                                && request.getPrincipal()
                                                        instanceof
                                                        AuthenticatedUserAuthenticationToken;
                                if (!valid) response.setStatusCode(HttpStatus.FORBIDDEN);
                                return valid;
                            }

                            public void afterHandshake(
                                    ServerHttpRequest r,
                                    ServerHttpResponse s,
                                    WebSocketHandler h,
                                    Exception e) {}
                        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic")
                .setTaskScheduler(chatHeartbeat())
                .setHeartbeatValue(new long[] {10000, 10000});
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChatSocketPolicy());
        registration.taskExecutor().corePoolSize(2).maxPoolSize(4).queueCapacity(256);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(2).maxPoolSize(4).queueCapacity(256);
    }

    private void close(WebSocketSession session) {
        try {
            session.close(CloseStatus.POLICY_VIOLATION);
        } catch (Exception ignored) {
            sessions.remove(session.getId());
        }
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration
                .setMessageSizeLimit(8192)
                .setSendBufferSizeLimit(16384)
                .setSendTimeLimit(5000)
                .setTimeToFirstMessage(5000);
        registration.addDecoratorFactory(
                handler ->
                        new WebSocketHandlerDecorator(handler) {
                            @Override
                            public void afterConnectionEstablished(WebSocketSession session)
                                    throws Exception {
                                synchronized (sessions) {
                                    if (sessions.size() >= 200
                                            || sessions.values().stream()
                                                            .filter(
                                                                    s ->
                                                                            s.getPrincipal() != null
                                                                                    && s.getPrincipal()
                                                                                            .getName()
                                                                                            .equals(
                                                                                                    session.getPrincipal()
                                                                                                            .getName()))
                                                            .count()
                                                    >= 5) {
                                        close(session);
                                        return;
                                    }
                                    sessions.put(session.getId(), session);
                                }
                                try {
                                    super.afterConnectionEstablished(session);
                                } catch (Exception failure) {
                                    sessions.remove(session.getId());
                                    throw failure;
                                }
                            }

                            @Override
                            public void afterConnectionClosed(
                                    WebSocketSession session, CloseStatus status) throws Exception {
                                sessions.remove(session.getId());
                                super.afterConnectionClosed(session, status);
                            }
                        });
    }

    @Scheduled(fixedDelay = 10000)
    public void expire() {
        for (var session : sessions.values())
            if (!(session.getPrincipal() instanceof AuthenticatedUserAuthenticationToken user)
                    || user.getToken().getExpiresAt() == null
                    || !user.getToken().getExpiresAt().isAfter(Instant.now())) close(session);
    }
}
