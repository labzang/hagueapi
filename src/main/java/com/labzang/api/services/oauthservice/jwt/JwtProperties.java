package com.labzang.api.services.oauthservice.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private Long expiration = 86400000L; // 기본 24시간
    // 깃허브액션을 실행하기 위한 변화 15:48
}

