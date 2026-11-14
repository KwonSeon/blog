package com.snowk.blog.api.chat;

import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;

import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;

public class ChatSocketPolicy implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var a = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (a == null) throw new AccessDeniedException("Chat authentication required");
        if (a.getCommand() == StompCommand.DISCONNECT) return message;
        if (!(a.getUser() instanceof AuthenticatedUserAuthenticationToken user)
                || user.getToken().getExpiresAt() == null
                || !user.getToken().getExpiresAt().isAfter(Instant.now()))
            throw new AccessDeniedException("Chat authentication required");
        var command = a.getCommand();
        if (command == StompCommand.CONNECT || command == StompCommand.STOMP || command == null)
            return message;
        if (command == StompCommand.SUBSCRIBE && "/topic/member-chat".equals(a.getDestination())) {
            var attrs = a.getSessionAttributes();
            if (attrs == null
                    || a.getSubscriptionId() == null
                    || a.getSubscriptionId().length() > 64)
                throw new AccessDeniedException("Subscription required");
            synchronized (attrs) {
                if (attrs.containsKey("chat.subscription"))
                    throw new AccessDeniedException("One chat subscription allowed");
                attrs.put("chat.subscription", a.getSubscriptionId());
            }
            return message;
        }
        if (command == StompCommand.UNSUBSCRIBE) {
            var attrs = a.getSessionAttributes();
            if (attrs != null)
                synchronized (attrs) {
                    if (java.util.Objects.equals(
                            attrs.get("chat.subscription"), a.getSubscriptionId()))
                        attrs.remove("chat.subscription");
                }
            return message;
        }
        throw new AccessDeniedException("Chat operation not allowed");
    }
}
