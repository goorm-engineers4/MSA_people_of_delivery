package com.example.cloudfour.userservice.token;

import com.example.cloudfour.modulecommon.token.BlacklistKeys;
import com.example.cloudfour.modulecommon.token.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTokenBlacklist implements TokenBlacklist {
    private final StringRedisTemplate redis;

    @Override
    public boolean contains(String token) {
        String key = BlacklistKeys.build(token);
        return Boolean.TRUE.equals(redis.hasKey(key));
    }
}
