# Labzang API

Spring Boot 기반의 Labzang API 서버입니다.

## 환경 설정

### 1. 환경변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 다음 환경변수들을 설정해주세요:

```bash
# env.example 파일을 복사해서 .env 파일을 생성하세요
cp env.example .env
```

그리고 `.env` 파일에서 실제 값들로 변경해주세요:

- `SPRING_DATASOURCE_URL`: PostgreSQL 데이터베이스 URL
- `SPRING_DATASOURCE_USERNAME`: 데이터베이스 사용자명
- `SPRING_DATASOURCE_PASSWORD`: 데이터베이스 비밀번호
- `UPSTASH_REDIS_HOST`: Redis 호스트
- `UPSTASH_REDIS_PASSWORD`: Redis 비밀번호
- `JWT_SECRET`: JWT 토큰 시크릿 키
- `GOOGLE_CLIENT_ID`: Google OAuth 클라이언트 ID
- `GOOGLE_CLIENT_SECRET`: Google OAuth 클라이언트 시크릿
- `KAKAO_REST_API_KEY`: Kakao REST API 키

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

## 보안 주의사항

- `.env` 파일은 절대 Git에 커밋하지 마세요
- 모든 민감한 정보는 환경변수로 관리하세요
- 프로덕션 환경에서는 적절한 시크릿 관리 도구를 사용하세요

## API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- Swagger UI: http://localhost:8080/docs
- OpenAPI JSON: http://localhost:8080/v3/api-docs
