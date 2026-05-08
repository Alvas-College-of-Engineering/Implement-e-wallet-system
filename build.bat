@echo off
echo ================================================
echo   E-Wallet Web Project Build Script
echo ================================================

set TOMCAT_HOME=D:\xampp\tomcat
set SERVLET_JAR=%TOMCAT_HOME%\lib\servlet-api.jar
set MYSQL_JAR=WebContent\WEB-INF\lib\mysql-connector-j-8.3.0.jar
set SRC_DIR=src
set OUT_DIR=build\classes
set WEBAPP_DIR=WebContent
set APP_NAME=ewallet

echo [1/4] Cleaning build directory...
if exist %OUT_DIR% rmdir /s /q %OUT_DIR%
mkdir %OUT_DIR%

echo [2/4] Compiling Java sources...
javac -cp ".;%SERVLET_JAR%;%MYSQL_JAR%" ^
      -d %OUT_DIR% ^
      -sourcepath %SRC_DIR% ^
      -encoding UTF-8 ^
      %SRC_DIR%\com\ewallet\model\*.java ^
      %SRC_DIR%\com\ewallet\dao\*.java ^
      %SRC_DIR%\com\ewallet\listener\*.java ^
      %SRC_DIR%\com\ewallet\servlet\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)
echo [OK] Compilation successful.

echo [3/4] Building WAR file...
if exist %APP_NAME%.war del %APP_NAME%.war

:: Copy compiled classes into WebContent/WEB-INF/classes so WAR structure is correct
if exist %WEBAPP_DIR%\WEB-INF\classes rmdir /s /q %WEBAPP_DIR%\WEB-INF\classes
xcopy /E /I /Q %OUT_DIR% %WEBAPP_DIR%\WEB-INF\classes

jar cvf %APP_NAME%.war -C %WEBAPP_DIR% .

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] WAR packaging failed!
    pause
    exit /b 1
)
echo [OK] WAR file created: %APP_NAME%.war

echo [4/4] Deploying to Tomcat...
copy /Y %APP_NAME%.war %TOMCAT_HOME%\webapps\%APP_NAME%.war
echo [OK] Deployed to %TOMCAT_HOME%\webapps\

echo.
echo ================================================
echo   Build complete!
echo   URL: http://localhost:8080/%APP_NAME%/
echo ================================================
pause
