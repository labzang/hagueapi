package com.labzang.api.services.oauthservice.kakao;

import com.labzang.api.services.oauthservice.kakao.dto.KakaoTokenResponse;
import com.labzang.api.services.oauthservice.kakao.dto.KakaoUserInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate;

    public KakaoTokenResponse getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.getRestApiKey());
        params.add("redirect_uri", kakaoProperties.getRedirectUri());
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                    kakaoProperties.getTokenUrl(),
                    request,
                    KakaoTokenResponse.class
            );

            log.info("카카오 액세스 토큰 획득 성공");
            return response.getBody();
        } catch (Exception e) {
            log.error("카카오 액세스 토큰 획득 실패: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 액세스 토큰 획득 실패", e);
        }
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    kakaoProperties.getUserInfoUrl(),
                    org.springframework.http.HttpMethod.GET,
                    request,
                    KakaoUserInfo.class
            );

            KakaoUserInfo userInfo = response.getBody();
            log.info("카카오 사용자 정보 획득 성공: {}", userInfo.getId());
            return userInfo;
        } catch (Exception e) {
            log.error("카카오 사용자 정보 획득 실패: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 사용자 정보 획득 실패", e);
        }
    }

    public String getAuthUrl() {
        return kakaoProperties.getAuthUrl() + 
               "?client_id=" + kakaoProperties.getRestApiKey() +
               "&redirect_uri=" + kakaoProperties.getRedirectUri() +
               "&response_type=code";
    }
}
