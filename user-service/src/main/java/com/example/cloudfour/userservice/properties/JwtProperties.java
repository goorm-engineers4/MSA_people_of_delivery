package com.example.cloudfour.userservice.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtProperties {
    private String secret;
    private Long expiration;
    private String refreshSecret;
    private Long refreshExpiration;

    public void setSecret(String secret){ this.secret = secret;}
    public void setExpiration(Long expirateion){ this.expiration = expirateion; }
    public void setRefreshSecret(String refreshSecret){ this.refreshSecret = refreshSecret; }
    public void setRefreshExpiration(Long refreshExpiration){ this.refreshExpiration = refreshExpiration; }
}