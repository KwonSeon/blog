package com.snowk.blog.api.notification.telegram;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

class TelegramGatewayTest {
    @Test
    void validatesSettingsAndPinsRecipientChanges() {
        var a = TelegramDeliveryTest.settings();
        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramSettings(true, a.token, "20:-123", "https://blog.test"));
        assertTrue(a.configured);
        assertTrue(a.matches(a.fingerprint));
        assertNotEquals(
                a.fingerprint,
                new TelegramSettings(true, a.token, "20:7654321", "https://blog.test").fingerprint);
        for (String site :
                new String[] {
                    "http://blog.test",
                    "https://user:pass@blog.test",
                    "https://blog.test/path",
                    "https://blog.test?q=1",
                    "https://blog.test#fragment",
                    "https://bad host"
                })
            assertThrows(
                    IllegalArgumentException.class,
                    () -> new TelegramSettings(true, a.token, "20:1234567", site));
        assertThrows(
                IllegalArgumentException.class,
                () -> new TelegramSettings(true, a.token, "20:1,20:2", "https://blog.test"));
        assertThrows(IllegalArgumentException.class, () -> new TelegramSettings(true, "", "", ""));
    }

    @SuppressWarnings("unchecked")
    @Test
    void sendsOnlyGenericMessageWithoutPaidBroadcast() throws Exception {
        var client = mock(HttpClient.class);
        var response = (HttpResponse<String>) mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"ok\":true}");
        when(client.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);
        assertEquals(
                "SENT",
                new TelegramGateway(TelegramDeliveryTest.settings(), client).send("20").state());
        var captor = org.mockito.ArgumentCaptor.forClass(HttpRequest.class);
        verify(client).send(captor.capture(), any());
        var request = captor.getValue();
        assertEquals("api.telegram.org", request.uri().getHost());
        assertEquals(8, request.timeout().orElseThrow().toSeconds());
        var bytes = new java.io.ByteArrayOutputStream();
        var done = new CompletableFuture<Void>();
        request.bodyPublisher()
                .orElseThrow()
                .subscribe(
                        new Flow.Subscriber<ByteBuffer>() {
                            public void onSubscribe(Flow.Subscription s) {
                                s.request(Long.MAX_VALUE);
                            }

                            public void onNext(ByteBuffer b) {
                                byte[] part = new byte[b.remaining()];
                                b.get(part);
                                bytes.writeBytes(part);
                            }

                            public void onError(Throwable t) {
                                done.completeExceptionally(t);
                            }

                            public void onComplete() {
                                done.complete(null);
                            }
                        });
        done.get(2, TimeUnit.SECONDS);
        var body =
                new com.fasterxml.jackson.databind.ObjectMapper()
                        .readTree(bytes.toString(StandardCharsets.UTF_8));
        assertFalse(body.get("allow_paid_broadcast").asBoolean());
        assertEquals(
                "블로그에 새 알림이 있습니다.\nhttps://blog.test/account/notifications",
                body.get("text").asText());
        assertTrue(body.get("link_preview_options").get("is_disabled").asBoolean());
    }

    @SuppressWarnings("unchecked")
    @Test
    void classifiesRateLimitPermanentAndAmbiguousFailures() throws Exception {
        var client = mock(HttpClient.class);
        var response = (HttpResponse<String>) mock(HttpResponse.class);
        when(client.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);
        var gateway = new TelegramGateway(TelegramDeliveryTest.settings(), client);
        when(response.statusCode()).thenReturn(429);
        when(response.body()).thenReturn("{\"ok\":false,\"parameters\":{\"retry_after\":99999}}");
        assertEquals(3600, gateway.send("20").retrySeconds());
        when(response.statusCode()).thenReturn(403);
        when(response.body()).thenReturn("{\"ok\":false}");
        assertEquals("FAILED", gateway.send("20").state());
        when(response.statusCode()).thenReturn(500);
        assertEquals("UNKNOWN", gateway.send("20").state());
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("not json");
        assertEquals("UNKNOWN", gateway.send("20").state());
        when(client.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("provider URL may contain a secret"));
        assertEquals("UNKNOWN", gateway.send("20").state());
        assertEquals("SKIPPED", gateway.send("999").state());
    }
}
