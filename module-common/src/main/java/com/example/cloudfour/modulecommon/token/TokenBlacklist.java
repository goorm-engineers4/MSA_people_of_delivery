package com.example.cloudfour.modulecommon.token;

public interface TokenBlacklist {
    boolean contains(String token);
}