package com.labzang.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Labzang API - 모놀리식 Spring Boot 애플리케이션
 * OAuth, User, Admin, Gateway 서비스를 통합한 단일 애플리케이션
 */
@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
}
