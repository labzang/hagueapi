# Docker 배포 가이드

## 개요
통합된 Labzang API 서비스를 Docker로 배포하기 위한 가이드입니다.

## 통합된 서비스들
- **Gateway Service**: API 라우팅 및 로드밸런싱
- **OAuth Service**: Google/Kakao OAuth 인증, JWT 토큰 관리
- **User Service**: 사용자 관리
- **Admin Service**: 관리자 기능

## 빌드 및 실행

### 1. 단일 컨테이너 빌드
```bash
# Docker 이미지 빌드
docker build -t labzang-api:latest .

# 컨테이너 실행
docker run -p 8080:8080 labzang-api:latest
```

### 2. Docker Compose로 전체 스택 실행
```bash
# 전체 서비스 시작 (PostgreSQL, Redis 포함)
docker-compose up -d

# 로그 확인
docker-compose logs -f labzang-api

# 서비스 중지
docker-compose down
```

### 3. 개발 환경 (Adminer 포함)
```bash
# 개발 프로파일로 실행 (DB 관리 도구 포함)
docker-compose --profile dev up -d
```

## 환경변수 설정

### 필수 환경변수
```bash
# OAuth 설정
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=http://localhost:8080/api/oauth/google/callback

KAKAO_REST_API_KEY=your-kakao-rest-api-key
KAKAO_REDIRECT_URI=http://localhost:8080/api/oauth/kakao/callback

# JWT 설정
JWT_SECRET=your-super-secret-jwt-key-here
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=2592000000
```

### 데이터베이스 설정
```bash
# PostgreSQL
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/labzang
SPRING_DATASOURCE_USERNAME=labzang
SPRING_DATASOURCE_PASSWORD=labzang123

# Redis
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=redis123
```

## 서비스 엔드포인트

### 헬스체크
- `GET /api/gateway/status` - 서비스 상태 확인

### API 문서
- `http://localhost:8080/swagger-ui.html` - Swagger UI

### 데이터베이스 관리 (개발용)
- `http://localhost:8081` - Adminer (dev 프로파일)

## 볼륨 관리

### 데이터 백업
```bash
# PostgreSQL 백업
docker exec labzang-postgres pg_dump -U labzang labzang > backup.sql

# Redis 백업
docker exec labzang-redis redis-cli --rdb /data/dump.rdb
```

### 데이터 복원
```bash
# PostgreSQL 복원
docker exec -i labzang-postgres psql -U labzang labzang < backup.sql
```

## 프로덕션 배포

### Railway 배포
```bash
# Railway CLI 설치 후
railway login
railway link
railway up
```

### 환경변수 설정 (Railway)
```bash
railway variables set GOOGLE_CLIENT_ID=your-value
railway variables set GOOGLE_CLIENT_SECRET=your-value
railway variables set JWT_SECRET=your-secure-secret
```

## 모니터링

### 로그 확인
```bash
# 실시간 로그
docker-compose logs -f labzang-api

# 특정 시간 로그
docker-compose logs --since 1h labzang-api
```

### 컨테이너 상태 확인
```bash
# 컨테이너 상태
docker-compose ps

# 리소스 사용량
docker stats labzang-api
```

## 트러블슈팅

### 일반적인 문제들

1. **포트 충돌**
   ```bash
   # 포트 사용 확인
   netstat -tulpn | grep :8080
   ```

2. **데이터베이스 연결 실패**
   ```bash
   # PostgreSQL 컨테이너 상태 확인
   docker-compose logs postgres
   ```

3. **Redis 연결 실패**
   ```bash
   # Redis 컨테이너 상태 확인
   docker-compose logs redis
   ```

### 컨테이너 재시작
```bash
# 특정 서비스만 재시작
docker-compose restart labzang-api

# 전체 재시작
docker-compose restart
```

## 보안 고려사항

1. **환경변수 보안**
   - 프로덕션에서는 실제 보안 키 사용
   - `.env` 파일을 버전 관리에 포함하지 않음

2. **네트워크 보안**
   - 필요한 포트만 외부에 노출
   - 내부 통신은 Docker 네트워크 사용

3. **데이터베이스 보안**
   - 강력한 패스워드 사용
   - 정기적인 백업 수행
