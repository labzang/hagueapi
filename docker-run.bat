@echo off
REM =============================================================================
REM Labzang API Service - Docker 실행 스크립트 (Windows)
REM =============================================================================

echo 🐳 Labzang API Service Docker 실행
echo 포트: 8080
echo URL: http://localhost:8080
echo.

REM 현재 디렉토리 확인
if not exist "Dockerfile" (
    echo ❌ 오류: api.labzang.com 디렉토리에서 실행해주세요.
    pause
    exit /b 1
)

REM 기존 컨테이너 정리
echo 🧹 기존 컨테이너 정리 중...
docker stop labzang-api >nul 2>&1
docker rm labzang-api >nul 2>&1

REM Docker 이미지 빌드
echo 🔨 Docker 이미지 빌드 중...
docker build -t labzang-api:latest .

if errorlevel 1 (
    echo ❌ Docker 빌드 실패!
    pause
    exit /b 1
)

echo ✅ Docker 이미지 빌드 완료!
echo.

REM Docker 컨테이너 실행
echo 🚀 Docker 컨테이너 실행 중...
echo 컨테이너명: labzang-api
echo Ctrl+C로 종료할 수 있습니다.
echo.

docker run --name labzang-api -p 8080:8080 --rm labzang-api:latest

echo.
echo ✅ Docker 컨테이너가 종료되었습니다.
pause
