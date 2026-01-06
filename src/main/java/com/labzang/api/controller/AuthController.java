package com.labzang.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.labzang.api.services.oauthservice.google.GoogleOAuthService;
import com.labzang.api.services.oauthservice.jwt.JwtTokenProvider;
import com.labzang.api.services.oauthservice.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증", description = "소셜 로그인 인증 API")
public class AuthController {

    @Autowired(required = false)
    private GoogleOAuthService googleOAuthService;

    @Autowired(required = false)
    private TokenService tokenService;

    @Autowired(required = false)
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 구글 로그인 인증 URL 생성
     * POST /api/auth/google/auth-url
     * 
     * 프론트엔드에서 CLIENT ID를 노출하지 않고 인증 URL을 가져올 수 있도록 함
     * 
     * @return 구글 OAuth 인증 URL
     */
    @PostMapping("/google/auth-url")
    @Operation(summary = "구글 로그인 인증 URL 생성", description = "구글 OAuth 인증을 위한 URL을 생성하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 URL 생성 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Map<String, Object>> getGoogleAuthUrl() {
        try {
            // 환경 변수에서 구글 OAuth 설정 가져오기
            String clientId = System.getenv("GOOGLE_CLIENT_ID");
            String redirectUri = System.getenv("GOOGLE_REDIRECT_URI");

            // 환경 변수 확인
            if (clientId == null || clientId.isEmpty()) {
                System.err.println("GOOGLE_CLIENT_ID 환경 변수가 설정되지 않았습니다.");
                return ResponseEntity.status(500).body(Map.of(
                        "success", false,
                        "error", "GOOGLE_CLIENT_ID가 설정되지 않았습니다."));
            }

            if (redirectUri == null || redirectUri.isEmpty()) {
                System.err.println("GOOGLE_REDIRECT_URI 환경 변수가 설정되지 않았습니다.");
                return ResponseEntity.status(500).body(Map.of(
                        "success", false,
                        "error", "GOOGLE_REDIRECT_URI가 설정되지 않았습니다."));
            }

            // CSRF 방지를 위한 state 생성
            String state = UUID.randomUUID().toString();

            // 구글 OAuth 인증 URL 생성
            String authUrl = String.format(
                    "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=%s&redirect_uri=%s&scope=openid%%20profile%%20email&state=%s",
                    clientId,
                    URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                    state);

            System.out.println("=== 구글 인증 URL 생성 ===");
            System.out.println("Client ID: " + clientId);
            System.out.println("Redirect URI: " + redirectUri);
            System.out.println("State: " + state);
            System.out.println("🍪🍪🍪Auth URL: " + authUrl);
            System.out.println("==========================");

            // 토큰 정보 출력 (현재 단계에서는 아직 생성되지 않음)
            System.out.println("=== 토큰 정보 ===");
            System.out.println("Access Token: [인증 URL 생성 단계에서는 아직 생성되지 않음]");
            System.out.println("Refresh Token: [인증 URL 생성 단계에서는 아직 생성되지 않음]");
            System.out.println("⚠️ 토큰은 구글 콜백 처리 후 생성됩니다.");
            System.out.println("==========================");

            // 성공 응답 반환
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "auth_url", authUrl));

        } catch (Exception e) {
            System.err.println("구글 인증 URL 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            // 에러 응답 반환
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "인증 URL 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 구글 콜백 처리 및 토큰 생성
     * POST /api/auth/google/callback
     * 
     * Authorization Code를 받아서 토큰을 생성하고 출력
     * 
     * @param code  Authorization Code
     * @param state CSRF 방지용 state
     * @return 토큰 정보
     */
    @PostMapping("/google/callback")
    @Operation(summary = "구글 콜백 처리", description = "구글 OAuth 콜백을 처리하고 토큰을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Map<String, Object>> handleGoogleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state) {

        System.out.println("=== 구글 콜백 처리 시작 ===");
        System.out.println("Code: " + code);
        System.out.println("State: " + state);

        if (code == null || code.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "error", "Authorization Code가 필요합니다."));
        }

        try {
            if (googleOAuthService == null || tokenService == null || jwtTokenProvider == null) {
                System.err.println("필수 서비스가 주입되지 않았습니다.");
                return ResponseEntity.status(500).body(Map.of(
                        "success", false,
                        "error", "서비스 초기화 오류"));
            }

            // 1. Authorization Code를 Access Token으로 교환
            Map<String, Object> tokenResponse = googleOAuthService.getAccessToken(code);
            String googleAccessToken = (String) tokenResponse.get("access_token");
            String googleRefreshToken = (String) tokenResponse.get("refresh_token");

            if (googleAccessToken == null) {
                throw new RuntimeException("구글 Access Token을 받을 수 없습니다.");
            }

            // 2. Access Token으로 사용자 정보 조회
            Map<String, Object> userInfo = googleOAuthService.getUserInfo(googleAccessToken);
            Map<String, Object> extractedUserInfo = googleOAuthService.extractUserInfo(userInfo);

            // 3. 사용자 ID 추출
            String userId = (String) extractedUserInfo.get("google_id");

            // 4. JWT 토큰 생성
            String jwtAccessToken = jwtTokenProvider.generateAccessToken(userId, "google", extractedUserInfo);
            String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(userId, "google");

            // 5. 토큰 정보 출력
            System.out.println("=== 토큰 정보 ===");
            System.out.println("Access Token: " + (jwtAccessToken != null
                    ? jwtAccessToken.substring(0, Math.min(jwtAccessToken.length(), 50)) + "..."
                    : "null"));
            System.out.println("Refresh Token: " + (jwtRefreshToken != null
                    ? jwtRefreshToken.substring(0, Math.min(jwtRefreshToken.length(), 50)) + "..."
                    : "null"));
            System.out.println("Google Access Token: " + (googleAccessToken != null
                    ? googleAccessToken.substring(0, Math.min(googleAccessToken.length(), 50)) + "..."
                    : "null"));
            System.out.println("Google Refresh Token: " + (googleRefreshToken != null
                    ? googleRefreshToken.substring(0, Math.min(googleRefreshToken.length(), 50)) + "..."
                    : "null"));
            System.out.println("User ID: " + userId);
            System.out.println("==========================");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "access_token", jwtAccessToken,
                    "refresh_token", jwtRefreshToken != null ? jwtRefreshToken : "",
                    "user_id", userId));

        } catch (Exception e) {
            System.err.println("구글 콜백 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "콜백 처리에 실패했습니다: " + e.getMessage()));
        }
    }
}
