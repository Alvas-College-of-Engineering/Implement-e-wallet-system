@echo off
echo ================================================
echo   E-Wallet - Starting Application...
echo ================================================

:: Step 1 - Start MySQL
echo [1/3] Starting MySQL...
net start MySQL84 >nul 2>&1
if %ERRORLEVEL% == 0 (
    echo [OK] MySQL started.
) else (
    echo [INFO] MySQL already running or started.
)

:: Step 2 - Start Tomcat
echo [2/3] Starting Tomcat...
set JAVA_HOME=D:\Program Files\RedHat\java-17-openjdk-17.0.19.0.10-1
set CATALINA_HOME=D:\xampp\tomcat
call "%CATALINA_HOME%\bin\catalina.bat" start >nul 2>&1
echo [OK] Tomcat starting...

:: Step 3 - Wait for Tomcat to be ready then open browser
echo [3/3] Waiting for server to be ready...
:WAIT
ping -n 3 127.0.0.1 >nul
powershell -Command "try { (New-Object Net.WebClient).DownloadString('http://localhost:8080/ewallet/') | Out-Null; exit 0 } catch { exit 1 }" >nul 2>&1
if %ERRORLEVEL% NEQ 0 goto WAIT

echo [OK] Server is ready!
echo.
echo ================================================
echo   Opening: http://localhost:8080/ewallet/
echo ================================================
start "" "http://localhost:8080/ewallet/"
