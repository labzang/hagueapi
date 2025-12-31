# 서비스 통합 가이드

## 개요
core.labzang.com의 모든 서비스들(oauthservice, userservice, adminservice)을 api.labzang.com으로 통합하는 과정에서 발생할 수 있는 import 에러와 패키지 충돌을 방지하기 위한 가이드입니다.

## 패키지 구조 재정리

### 기존 구조 (충돌 발생)
```
com.labzang.api.ApiApplication (모든 서비스에서 동일)
com.labzang.api.controller.*
com.labzang.api.service.*
```

### 권장 새 구조 (충돌 방지)
```
api.labzang.com/src/main/java/com/labzang/api/
├── ApiApplication.java                    # 메인 애플리케이션
├── gateway/                              # Gateway 관련
│   └── controller/
│       └── GatewayController.java
├── oauth/                                # OAuth 서비스 통합
│   ├── controller/
│   ├── service/
│   │   ├── GoogleOAuthService.java
│   │   ├── KakaoOAuthService.java
│   │   └── TokenService.java
│   └── config/
├── user/                                 # User 서비스 통합
│   ├── controller/
│   ├── service/
│   └── entity/
└── admin/                                # Admin 서비스 통합
    ├── controller/
    └── service/
```

## 통합된 의존성

### 추가된 의존성들
- `spring-boot-starter-data-jpa` - OAuth 서비스의 PostgreSQL 연동
- `postgresql` - PostgreSQL 드라이버
- `spring-boot-starter-data-redis` - Redis 세션 관리
- JWT 라이브러리들 - 토큰 기반 인증

### 기존 의존성들
- Spring Cloud Gateway
- Spring Boot Web/WebFlux
- Lombok, DevTools, Test

## Import 에러 방지 체크리스트

### 1. 패키지명 중복 확인
- [ ] 모든 서비스의 ApiApplication.java 클래스명 확인
- [ ] Controller 클래스들의 패키지 경로 확인
- [ ] Service 클래스들의 패키지 경로 확인

### 2. 설정 파일 통합
- [ ] application.yaml 설정 통합
- [ ] OAuth 관련 설정 추가
- [ ] 데이터베이스 설정 추가
- [ ] Redis 설정 추가

### 3. 포트 충돌 방지
- [ ] 통합된 서비스는 단일 포트 사용 (기본: 8080)
- [ ] 기존 서비스별 포트 설정 제거

### 4. Bean 중복 방지
- [ ] RestTemplate Bean 중복 확인
- [ ] Configuration 클래스 중복 확인
- [ ] Service Bean 이름 중복 확인

## 마이그레이션 단계

### 1단계: 소스 코드 이동
```bash
# OAuth 서비스 클래스들을 oauth 패키지로 이동
core.labzang.com/oauthservice/src/main/java/com/labzang/api/google/
→ api.labzang.com/src/main/java/com/labzang/api/oauth/google/

core.labzang.com/oauthservice/src/main/java/com/labzang/api/kakao/
→ api.labzang.com/src/main/java/com/labzang/api/oauth/kakao/

core.labzang.com/oauthservice/src/main/java/com/labzang/api/token/
→ api.labzang.com/src/main/java/com/labzang/api/oauth/token/
```

### 2단계: Import 문 수정
- 모든 Java 파일의 import 문을 새로운 패키지 구조에 맞게 수정
- 패키지 선언문 수정

### 3단계: 설정 파일 통합
- application.yaml에 모든 서비스 설정 통합
- 환경별 설정 파일 정리

### 4단계: 테스트 및 검증
- 빌드 에러 확인
- 런타임 에러 확인
- 기능 테스트 수행

## 주의사항

1. **Bean 이름 충돌**: 같은 타입의 Bean이 여러 개 있을 경우 @Qualifier 사용
2. **설정 값 충돌**: 동일한 설정 키가 있는지 확인
3. **포트 설정**: 통합 후 단일 포트로 변경
4. **데이터베이스 스키마**: 각 서비스별 스키마 분리 유지

## 완료 후 정리

통합이 완료되면 다음 디렉토리들을 삭제할 수 있습니다:
- `core.labzang.com/oauthservice/`
- `core.labzang.com/userservice/`
- `core.labzang.com/adminservice/`
- `core.labzang.com/` (전체 디렉토리)
