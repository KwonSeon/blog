package com.snowk.blog.api.chat;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class ChatBroadcast {
    private final ObjectProvider<SimpMessagingTemplate> templates;

    public ChatBroadcast(ObjectProvider<SimpMessagingTemplate> templates) {
        this.templates = templates;
    }

    @TransactionalEventListener
    public void changed(ChatService.Changed event) {
        // DB commit is the source of truth. Hints contain no message text or account IDs.
        var template = templates.getIfAvailable();
        if (template != null)
            try {
                template.convertAndSend("/topic/member-chat", (Object) Map.of("changed", true));
            } catch (Exception ignored) {
            }
    }
}
