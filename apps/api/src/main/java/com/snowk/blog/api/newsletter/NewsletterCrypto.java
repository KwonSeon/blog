package com.snowk.blog.api.newsletter;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

@Component
public class NewsletterCrypto {
    private final NewsletterSettings settings;
    private final SecureRandom random = new SecureRandom();

    public NewsletterCrypto(NewsletterSettings settings) {
        this.settings = settings;
    }

    public String token() {
        byte[] b = new byte[32];
        random.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    public String hash(String value) {
        try {
            return HexFormat.of()
                    .formatHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Hash unavailable");
        }
    }

    public String hmac(String value) {
        try {
            var m = Mac.getInstance("HmacSHA256");
            m.init(
                    new SecretKeySpec(
                            settings.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(m.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Signature unavailable");
        }
    }

    public void verify(
            String action, String data, String client, long timestamp, String signature) {
        settings.requireEnabled();
        long now = Instant.now().getEpochSecond();
        if (data == null
                || data.length() > 2048
                || client == null
                || !client.matches("[a-f0-9]{64}")
                || signature == null
                || !signature.matches("[a-f0-9]{64}")
                || timestamp < now - 120
                || timestamp > now + 10
                || !MessageDigest.isEqual(
                        HexFormat.of().parseHex(signature),
                        HexFormat.of()
                                .parseHex(
                                        hmac(
                                                action + "\n" + timestamp + "\n" + client + "\n"
                                                        + data))))
            throw new BaseException(CommonErrorStatus.FORBIDDEN);
    }

    private SecretKeySpec key() {
        return new SecretKeySpec(
                HexFormat.of().parseHex(hash(settings.secret() + ":newsletter-payload")), "AES");
    }

    public String seal(String text) {
        try {
            byte[] iv = new byte[12];
            random.nextBytes(iv);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(128, iv));
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            byte[] packed = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, packed, 0, iv.length);
            System.arraycopy(encrypted, 0, packed, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(packed);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Payload encryption unavailable");
        }
    }

    public String open(String sealed) {
        try {
            byte[] packed = Base64.getDecoder().decode(sealed);
            if (packed.length < 28) throw new GeneralSecurityException();
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key(),
                    new GCMParameterSpec(128, Arrays.copyOfRange(packed, 0, 12)));
            return new String(
                    cipher.doFinal(packed, 12, packed.length - 12), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException("Invalid encrypted newsletter payload");
        }
    }
}
