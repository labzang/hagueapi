@echo off
REM =============================================================================
REM Labzang API Service 빌드 후 실행 스크립트 (Windows)
REM =============================================================================

echo 🔨 Labzang API Service 빌드 및 실행
echo 포트: 8080
echo URL: http://localhost:8080
echo.

REM 현재 디렉토리 확인
if not exist "gradlew.bat" (
    echo ❌ 오류: api.labzang.com 디렉토리에서 실행해주세요.
    pause
    exit /b 1
)

REM 빌드
echo 📦 애플리케이션 빌드 중...
gradlew.bat clean build -x test

if errorlevel 1 (
    echo ❌ 빌드 실패!
    pause
    exit /b 1
)

echo ✅ 빌드 완료!
echo.

REM JAR 파일 찾기 및 실행
for %%f in (build\libs\*.jar) do (
    echo 🚀 JAR 파일 실행: %%f
    echo Ctrl+C로 종료할 수 있습니다.
    echo.
    java -jar "%%f"
    goto :end
)

echo ❌ JAR 파일을 찾을 수 없습니다.
pause
exit /b 1

:end
echo.
echo ✅ 애플리케이션이 종료되었습니다.
pause
