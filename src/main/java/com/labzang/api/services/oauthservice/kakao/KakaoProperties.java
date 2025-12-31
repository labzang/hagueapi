package com.labzang.api.services.oauthservice.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 카카오 OAuth 설정 프로퍼티
 */
@Data
@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {
    
    private String restApiKey;
    private String redirectUri;
    private String clientSecret;
    
    // 카카오 API URL들
    private String authUrl = "https://kauth.kakao.com/oauth/authorize";
    private String tokenUrl = "https://kauth.kakao.com/oauth/token";
    private String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
    
    public String getRestApiKey() {
        return restApiKey;
    }
    
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public String getAuthUrl() {
        return authUrl;
    }
    
    public String getTokenUrl() {
        return tokenUrl;
    }
    
    public String getUserInfoUrl() {
        return userInfoUrl;
    }
}