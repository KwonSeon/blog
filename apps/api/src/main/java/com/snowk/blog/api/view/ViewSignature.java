package com.snowk.blog.api.view;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import org.springframework.stereotype.Component;


@Component
public class ViewSignature {
    private final String secret;
    public ViewSignature(@Value("${BLOG_VIEW_SIGNING_SECRET:}") String secret) { this.secret=secret; }
    public LocalDate verify(String kind,long id,String visitor,long timestamp,String signature) {
        if(secret.length()<32) throw new BaseException(CommonErrorStatus.SERVICE_UNAVAILABLE);
        if(visitor==null || !visitor.matches("[a-f0-9]{64}") || signature==null || !signature.matches("[a-f0-9]{64}")
            || timestamp < Instant.now().getEpochSecond()-120 || timestamp > Instant.now().getEpochSecond()+10)
            throw new BaseException(CommonErrorStatus.FORBIDDEN);
        try {
            var mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
            byte[] expected=mac.doFinal((kind+":"+id+":"+visitor+":"+timestamp).getBytes(StandardCharsets.UTF_8));
            if(!MessageDigest.isEqual(expected,HexFormat.of().parseHex(signature))) throw new BaseException(CommonErrorStatus.FORBIDDEN);
        } catch(java.security.GeneralSecurityException e) { throw new IllegalStateException("View signature unavailable"); }
        // Match the signed UTC day even during a midnight request boundary.
        return Instant.ofEpochSecond(timestamp).atOffset(ZoneOffset.UTC).toLocalDate();
    }
}
