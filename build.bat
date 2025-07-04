@echo off
setlocal

set PROJECT_DIR=%~dp0
set SRC_DIR=%PROJECT_DIR%src
set BIN_DIR=%PROJECT_DIR%bin
set LIB_DIR=%PROJECT_DIR%lib
set JAR_NAME=tsa-security.jar
set MAIN_CLASS=com.cameramanager.CameraManager

set JAR_CMD="C:\Program Files\Java\jdk-24\bin\jar.exe"

echo Cleaning bin directory...
if exist "%BIN_DIR%" rd /s /q "%BIN_DIR%"
mkdir "%BIN_DIR%"

echo Compiling Java sources...
javac -d "%BIN_DIR%" -cp "%LIB_DIR%\*;%BIN_DIR%" "%SRC_DIR%\com\cameramanager\*.java"

if %errorlevel% neq 0 (
    echo Compilation failed!
    goto :eof
)

echo Creating manifest file...
echo Main-Class: %MAIN_CLASS%> "%BIN_DIR%\MANIFEST.MF"

echo Creating runnable JAR...
%JAR_CMD% -cvfm "%PROJECT_DIR%%JAR_NAME%" "%BIN_DIR%\MANIFEST.MF" -C "%BIN_DIR%\." . -C "%LIB_DIR%\." .

if %errorlevel% neq 0 (
    echo JAR creation failed!
    goto :eof
)

echo Build successful! JAR created at %PROJECT_DIR%%JAR_NAME%
endlocal

