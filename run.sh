#!/bin/bash

# =============================================================================
# Labzang API Service 실행 스크립트 (Linux/Mac)
# =============================================================================

echo "🚀 Labzang API Service 시작 중..."
echo "포트: 8080"
echo "URL: http://localhost:8080"
echo ""

# 현재 디렉토리 확인
if [ ! -f "gradlew" ]; then
    echo "❌ 오류: api.labzang.com 디렉토리에서 실행해주세요."
    exit 1
fi

# Gradle 실행 권한 부여
chmod +x gradlew

# Spring Boot 애플리케이션 실행
echo "📦 Gradle로 애플리케이션 실행 중..."
./gradlew bootRun

echo ""
echo "✅ 애플리케이션이 종료되었습니다."
