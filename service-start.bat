@echo off
chcp 65001 >nul
echo ========================================
echo   校友直聘 - 服务启动脚本
echo ========================================
echo.

:: 设置颜色
color 0A

:: 定义路径
set REDIS_PATH=D:\Dev\Redis-x64-5.0.14.1\redis-server.exe
set MINIO_PATH=D:\Users\chinese\Downloads\minio.exe
set MINIO_DATA_DIR=E:/data

:: 检查 Redis 是否存在
if not exist "%REDIS_PATH%" (
    color 0C
    echo [错误] Redis 可执行文件不存在: %REDIS_PATH%
    pause
    exit /b 1
)

:: 检查 MinIO 是否存在
if not exist "%MINIO_PATH%" (
    color 0C
    echo [错误] MinIO 可执行文件不存在: %MINIO_PATH%
    pause
    exit /b 1
)

:: 检查 MinIO 数据目录是否存在，不存在则创建
if not exist "%MINIO_DATA_DIR%" (
    echo [提示] 创建 MinIO 数据目录: %MINIO_DATA_DIR%
    mkdir "%MINIO_DATA_DIR%"
)

echo [1/3] 正在启动 Redis...
echo.

:: 启动 Redis（新窗口）
start "Redis Server" cmd /k "%REDIS_PATH%"

:: 等待 Redis 启动
timeout /t 2 /nobreak >nul

echo [✓] Redis 启动成功
echo.
echo [2/3] 正在启动 MinIO...
echo.

:: 启动 MinIO（新窗口）
start "MinIO Server" cmd /k "%MINIO_PATH% server %MINIO_DATA_DIR%"

:: 等待 MinIO 启动
timeout /t 3 /nobreak >nul

echo [✓] MinIO 启动成功
echo.
echo ========================================
echo [3/3] 所有服务已启动！
echo ========================================
echo.
echo 服务信息：
echo   - Redis:  localhost:6379
echo   - MinIO:  http://localhost:9000
echo   - MinIO Console: http://localhost:9001
echo.
echo 访问地址：
echo   - MinIO Console: http://localhost:9001
echo     默认账号: minioadmin
echo     默认密码: minioadmin
echo.
echo ========================================
echo.
echo 提示：关闭窗口即可停止对应服务
echo.
pause
