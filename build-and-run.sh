#!/bin/bash

# =============================================================================
# Labzang API Service 빌드 후 실행 스크립트 (Linux/Mac)
# =============================================================================

echo "🔨 Labzang API Service 빌드 및 실행"
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

# 빌드
echo "📦 애플리케이션 빌드 중..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패!"
    exit 1
fi

echo "✅ 빌드 완료!"
echo ""

# JAR 파일 찾기
JAR_FILE=$(find build/libs -name "*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "❌ JAR 파일을 찾을 수 없습니다."
    exit 1
fi

echo "🚀 JAR 파일 실행: $JAR_FILE"
echo "Ctrl+C로 종료할 수 있습니다."
echo ""

# JAR 파일 실행
java -jar "$JAR_FILE"

echo ""
echo "✅ 애플리케이션이 종료되었습니다."
