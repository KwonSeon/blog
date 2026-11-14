package com.snowk.blog.api.newsletter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.http.*;

class NewsletterGatewayTest {
    @Test
    void resendRetriesCarryStableProviderIdempotencyKey() throws Exception {
        var client = mock(HttpClient.class);
        HttpResponse<Void> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(202);
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);
        var settings =
                new NewsletterSettings(
                        true,
                        "a".repeat(32),
                        "https://blog.example.test",
                        "blog@example.test",
                        "resend",
                        "synthetic-api-key",
                        "");
        var gateway = new NewsletterGateway(settings, client);
        gateway.send(
                "message-1",
                "reader@example.test",
                "subject",
                "text",
                "https://blog.example.test/subscribe/unsubscribe");
        var request = ArgumentCaptor.forClass(HttpRequest.class);
        verify(client).send(request.capture(), any(HttpResponse.BodyHandler.class));
        assertEquals("https://api.resend.com/emails", request.getValue().uri().toString());
        assertEquals(
                "blog-newsletter/message-1",
                request.getValue().headers().firstValue("Idempotency-Key").orElseThrow());
        when(response.statusCode()).thenReturn(429);
        assertThrows(
                NewsletterGateway.DeliveryFailure.class,
                () -> gateway.send("message-1", "reader@example.test", "subject", "text", ""));
    }
}
