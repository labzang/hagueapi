package com.labzang.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 설정
 * Upstash Redis 연결 및 RedisTemplate 설정
 * 
 * Upstash Redis는 TLS/SSL 연결이 필수입니다.
 */
@Configuration
public class RedisConfig {

    @Value("${UPSTASH_REDIS_HOST:localhost}")
    private String host;

    @Value("${UPSTASH_REDIS_PORT:6379}")
    private int port;

    @Value("${UPSTASH_REDIS_PASSWORD:}")
    private String password;

    @Value("${spring.data.redis.ssl.enabled:true}")
    private boolean sslEnabled;

    @Value("${spring.data.redis.timeout:2000ms}")
    private String timeout;

    /**
     * RedisConnectionFactory 빈 생성
     * Upstash Redis는 TLS/SSL 연결을 사용합니다.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(password);

        LettuceClientConfiguration clientConfig;
        if (sslEnabled) {
            clientConfig = LettuceClientConfiguration.builder()
                    .useSsl()
                    .and()
                    .commandTimeout(Duration.ofMillis(parseDuration(timeout)))
                    .build();
        } else {
            clientConfig = LettuceClientConfiguration.builder()
                    .commandTimeout(Duration.ofMillis(parseDuration(timeout)))
                    .build();
        }

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
        factory.afterPropertiesSet();

        // 연결 정보 로깅
        if (sslEnabled) {
            System.out.println("✅ Upstash Redis SSL 연결 설정: " + host + ":" + port);
        } else {
            System.out.println("⚠️ Upstash Redis 일반 연결 설정: " + host + ":" + port);
        }

        return factory;
    }

    /**
     * RedisTemplate 빈 생성
     * Key는 String으로, Value는 JSON으로 직렬화합니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value 직렬화 설정 (JSON)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();

        // 연결 테스트
        try {
            template.opsForValue().set("connection:test", "ok", 10, java.util.concurrent.TimeUnit.SECONDS);
            System.out.println("✅ Redis 연결 성공 및 RedisTemplate 초기화 완료");
        } catch (Exception e) {
            System.err.println("❌ Redis 연결 실패: " + e.getMessage());
            e.printStackTrace();
        }

        return template;
    }

    /**
     * Duration 문자열을 밀리초로 변환
     * 예: "2000ms" -> 2000, "2s" -> 2000
     */
    private long parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 2000; // 기본값 2초
        }

        String trimmed = duration.trim();
        if (trimmed.endsWith("ms")) {
            return Long.parseLong(trimmed.substring(0, trimmed.length() - 2));
        } else if (trimmed.endsWith("s")) {
            return Long.parseLong(trimmed.substring(0, trimmed.length() - 1)) * 1000;
        } else {
            return Long.parseLong(trimmed);
        }
    }
}
