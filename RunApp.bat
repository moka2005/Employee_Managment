@echo off
chcp 65001 > nul
cd /d "%~dp0"

if exist "Employee_Managment.jar" (
    start javaw -jar "Employee_Managment.jar"
) else if exist "dist\Employee_Managment.jar" (
    start javaw -jar "dist\Employee_Managment.jar"
) else (
    echo [!] لم يتم العثور على ملف Employee_Managment.jar
    pause
)
