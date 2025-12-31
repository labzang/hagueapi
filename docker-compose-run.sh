#!/bin/bash

# =============================================================================
# Labzang API Service - Docker Compose 실행 스크립트 (Linux/Mac)
# =============================================================================

echo "🐳 Labzang API Service Docker Compose 실행"
echo "포트: 8080"
echo "URL: http://localhost:8080"
echo ""

# 현재 디렉토리 확인
if [ ! -f "docker-compose.simple.yml" ]; then
    echo "❌ 오류: api.labzang.com 디렉토리에서 실행해주세요."
    exit 1
fi

# 기존 컨테이너 정리
echo "🧹 기존 컨테이너 정리 중..."
docker-compose -f docker-compose.simple.yml down 2>/dev/null || true

# Docker Compose로 실행
echo "🚀 Docker Compose로 서비스 시작 중..."
echo "Ctrl+C로 종료할 수 있습니다."
echo ""

# 포그라운드에서 실행 (로그 확인 가능)
docker-compose -f docker-compose.simple.yml up --build

echo ""
echo "✅ Docker Compose 서비스가 종료되었습니다."
