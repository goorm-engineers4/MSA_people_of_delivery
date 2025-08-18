package com.example.cloudfour.modulecommon.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class BlacklistKeys {
    private BlacklistKeys() {}

    private static final String PREFIX = "auth:blacklist:";

    public static String build(String token) {
        return PREFIX + sha256Url(token);
    }

    private static String sha256Url(String s) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(d);
        } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
}
