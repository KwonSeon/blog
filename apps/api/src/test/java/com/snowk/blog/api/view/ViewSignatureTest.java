package com.snowk.blog.api.view;
import java.time.*;
import java.util.HexFormat;
import java.nio.charset.StandardCharsets;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import com.snowk.blog.api.global.exception.BaseException;
import static org.junit.jupiter.api.Assertions.*;
class ViewSignatureTest {
    final String secret="test-secret-not-for-runtime-use-123456789",visitor="a".repeat(64);
    String sign(long ts) throws Exception {
        var mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));return HexFormat.of().formatHex(mac.doFinal(("posts:1:"+visitor+":"+ts).getBytes(StandardCharsets.UTF_8)));
    }
    @Test void validSignatureUsesSignedUtcDay() throws Exception {
        long ts=Instant.now().getEpochSecond();assertEquals(Instant.ofEpochSecond(ts).atOffset(ZoneOffset.UTC).toLocalDate(),new ViewSignature(secret).verify("posts",1,visitor,ts,sign(ts)));
    }
    @Test void wrongContentKeyHashOrSignatureFails() throws Exception {
        long ts=Instant.now().getEpochSecond();String signature=sign(ts);var v=new ViewSignature(secret);
        assertThrows(BaseException.class,()->v.verify("projects",1,visitor,ts,signature));assertThrows(BaseException.class,()->v.verify("posts",2,visitor,ts,signature));assertThrows(BaseException.class,()->v.verify("posts",1,"b".repeat(64),ts,signature));assertThrows(BaseException.class,()->new ViewSignature(secret+"x").verify("posts",1,visitor,ts,signature));
    }
    @Test void expiredFutureMalformedAndMissingConfigFailClosed() throws Exception {
        var v=new ViewSignature(secret);long ts=Instant.now().getEpochSecond();
        assertThrows(BaseException.class,()->v.verify("posts",1,visitor,ts-121,sign(ts-121)));assertThrows(BaseException.class,()->v.verify("posts",1,visitor,Long.MAX_VALUE,"a".repeat(64)));assertThrows(BaseException.class,()->v.verify("posts",1,visitor,ts,"invalid"));assertThrows(BaseException.class,()->new ViewSignature("").verify("posts",1,visitor,ts,sign(ts)));
    }
}
