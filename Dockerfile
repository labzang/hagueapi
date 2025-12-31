# =============================================================================
# Labzang API Service - 통합 Dockerfile
# OAuth, User, Admin, Gateway 서비스를 모두 포함하는 단일 애플리케이션
# =============================================================================

# 1단계: 빌드 환경
FROM eclipse-temurin:21-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 래퍼와 빌드 파일들 복사
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# 소스 코드 복사
COPY src/ src/

# 실행 권한 부여 및 빌드 (테스트 제외)
RUN chmod +x gradlew && \
    ./gradlew clean build -x test --no-daemon

# 2단계: 실행 환경
FROM eclipse-temurin:21-jre

# 메타데이터 설정
LABEL maintainer="Labzang Team"
LABEL description="Labzang API Service - Integrated OAuth, User, Admin, Gateway"
LABEL version="1.0.0"

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 패키지 설치 (curl for health checks)
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean

# 애플리케이션 사용자 생성 (보안 강화)
RUN groupadd -r labzang && useradd -r -g labzang labzang

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 파일 권한 설정
RUN chown labzang:labzang app.jar

# 사용자 전환
USER labzang

# 포트 노출 (통합된 서비스는 단일 포트 사용)
EXPOSE 8080

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/gateway/status || exit 1

# JVM 옵션 설정 (메모리 최적화)
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]