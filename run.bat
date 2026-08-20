@echo off
echo ==========================================
echo  Emergency Patient Priority Queue System
echo ==========================================
echo.

REM Check if out directory exists; create if needed
if not exist out mkdir out

echo [1/2] Compiling Java sources...
javac -d out src\Patient.java src\HospitalQueue.java src\HospitalGUI.java src\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please check Java installation.
    pause
    exit /b 1
)

echo [2/2] Launching application...
echo.
java -cp out Main
