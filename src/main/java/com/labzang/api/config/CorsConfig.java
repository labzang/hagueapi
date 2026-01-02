package com.labzang.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * WebMVC CORS 설정
 * WebFlux 대신 WebMVC를 사용하는 모놀리식 구조
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 환경 변수에서 추가 허용 오리진 가져오기 (쉼표로 구분)
        String additionalOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        
        if (additionalOrigins != null && !additionalOrigins.isEmpty()) {
            String[] origins = additionalOrigins.split(",");
            registry.addMapping("/**")
                    .allowedOrigins(origins)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        } else {
            // 기본값: FRONTEND_URL만 사용
            registry.addMapping("/**")
                    .allowedOrigins(frontendUrl)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 환경 변수에서 추가 허용 오리진 가져오기 (쉼표로 구분)
        String additionalOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        
        if (additionalOrigins != null && !additionalOrigins.isEmpty()) {
            String[] origins = additionalOrigins.split(",");
            configuration.setAllowedOrigins(Arrays.asList(origins));
        } else {
            // 기본값: FRONTEND_URL만 사용
            configuration.setAllowedOrigins(Arrays.asList(frontendUrl));
        }
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
