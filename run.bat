@echo off
REM =============================================================================
REM Labzang API Service 실행 스크립트 (Windows)
REM =============================================================================

echo 🚀 Labzang API Service 시작 중...
echo 포트: 8080
echo URL: http://localhost:8080
echo.

REM 현재 디렉토리 확인
if not exist "gradlew.bat" (
    echo ❌ 오류: api.labzang.com 디렉토리에서 실행해주세요.
    pause
    exit /b 1
)

REM Spring Boot 애플리케이션 실행
echo 📦 Gradle로 애플리케이션 실행 중...
gradlew.bat bootRun

echo.
echo ✅ 애플리케이션이 종료되었습니다.
pause
