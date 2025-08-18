package com.example.cloudfour.authservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class BlacklistRedisConfig {
    @Bean("blacklistConnectionFactory")
    public LettuceConnectionFactory blacklistCF(
            @org.springframework.beans.factory.annotation.Value("${redis.blacklist.host}") String host,
            @org.springframework.beans.factory.annotation.Value("${redis.blacklist.port}") int port,
            @org.springframework.beans.factory.annotation.Value("${redis.blacklist.database:0}") int db) {
        var cf = new LettuceConnectionFactory(host, port);
        cf.setDatabase(db);
        cf.afterPropertiesSet();
        return cf;
    }

    @Bean("blacklistStringRedisTemplate")
    public StringRedisTemplate blacklistTemplate(
            @Qualifier("blacklistConnectionFactory") RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }
}