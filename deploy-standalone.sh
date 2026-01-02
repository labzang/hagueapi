#!/bin/bash

# Labzang API 독립 배포 스크립트 (EC2 전용)

set -e

echo "🚀 Labzang API 독립 배포 시작..."

# 1. 최신 코드 가져오기
echo "📥 최신 코드 가져오기..."
git pull origin main

# 2. 환경 변수 파일 확인
if [ ! -f ".env" ]; then
    echo "❌ .env 파일이 없습니다. 환경 변수를 설정해주세요."
    echo "💡 env.example을 참고하여 .env 파일을 생성하세요:"
    echo "   cp env.example .env"
    echo "   nano .env"
    exit 1
fi

# 3. Docker 이미지 최신 버전 가져오기
echo "🐳 Docker 이미지 업데이트..."
docker-compose -f docker-compose.prod.yml pull

# 4. 기존 컨테이너 중지 및 제거
echo "🛑 기존 서비스 중지..."
docker-compose -f docker-compose.prod.yml down

# 5. 새 컨테이너 시작
echo "▶️ 새 서비스 시작..."
docker-compose -f docker-compose.prod.yml up -d

# 6. 헬스 체크
echo "🔍 서비스 상태 확인..."
sleep 30

if curl -f http://localhost:8080/docs > /dev/null 2>&1; then
    echo "✅ 배포 성공! API가 정상 동작 중입니다."
    echo "🌐 API 문서: http://$(curl -s ifconfig.me):8080/docs"
    echo "🔗 API 베이스 URL: http://$(curl -s ifconfig.me):8080"
else
    echo "❌ 배포 실패! 로그를 확인해주세요."
    docker-compose -f docker-compose.prod.yml logs --tail 50
    exit 1
fi

echo "🎉 독립 배포 완료!"
echo ""
echo "📊 서비스 상태:"
docker-compose -f docker-compose.prod.yml ps
echo ""
echo "📝 유용한 명령어:"
echo "  로그 확인: docker-compose -f docker-compose.prod.yml logs -f"
echo "  서비스 재시작: ./deploy-standalone.sh"
echo "  서비스 중지: docker-compose -f docker-compose.prod.yml down"
