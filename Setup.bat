@echo off
setlocal EnableDelayedExpansion
title Employee Management - Windows Installer
color 0A

echo ========================================================
echo   Employee Management System - Windows Setup
echo   تثبيت نظام إدارة الموظفين والمصاريف لنظام Windows
echo ========================================================
echo.

REM 1. Find Java Runtime
set "JAVA_EXE="
where javaw.exe >nul 2>&1
if !errorlevel! equ 0 (
    set "JAVA_EXE=javaw.exe"
) else (
    where java.exe >nul 2>&1
    if !errorlevel! equ 0 (
        set "JAVA_EXE=java.exe"
    )
)

if not defined JAVA_EXE (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javaw.exe" set "JAVA_EXE=%JAVA_HOME%\bin\javaw.exe"
        if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    )
)

if not defined JAVA_EXE (
    for /d %%D in ("%ProgramFiles%\Java\*") do (
        if exist "%%D\bin\javaw.exe" set "JAVA_EXE=%%D\bin\javaw.exe"
    )
)

if not defined JAVA_EXE (
    for /d %%D in ("%ProgramFiles(x86)%\Java\*") do (
        if exist "%%D\bin\javaw.exe" set "JAVA_EXE=%%D\bin\javaw.exe"
    )
)

if not defined JAVA_EXE (
    for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\*") do (
        if exist "%%D\bin\javaw.exe" set "JAVA_EXE=%%D\bin\javaw.exe"
    )
)

if not defined JAVA_EXE (
    for /d %%D in ("%ProgramFiles%\Microsoft\*") do (
        if exist "%%D\bin\javaw.exe" set "JAVA_EXE=%%D\bin\javaw.exe"
    )
)

if not defined JAVA_EXE (
    echo [ERROR] Java is not installed or not found on this computer.
    echo [خطأ] لم يتم العثور على Java في هذا الجهاز.
    echo.
    echo Please download and install Java from:
    echo https://www.oracle.com/java/technologies/downloads/
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)

echo [OK] Java detected: !JAVA_EXE!
echo.

REM 2. Destination directory
set "TARGET_DIR=%LOCALAPPDATA%\EmployeeManagement"
echo [*] Installing files to: !TARGET_DIR!
if not exist "!TARGET_DIR!" mkdir "!TARGET_DIR!"
if not exist "!TARGET_DIR!\lib" mkdir "!TARGET_DIR!\lib"
if not exist "!TARGET_DIR!\icons" mkdir "!TARGET_DIR!\icons"

REM 3. Copy Jar and Libs
if exist "dist\Employee_Managment.jar" (
    copy /Y "dist\Employee_Managment.jar" "!TARGET_DIR!\" >nul
) else if exist "Employee_Managment.jar" (
    copy /Y "Employee_Managment.jar" "!TARGET_DIR!\" >nul
)

if exist "dist\lib" (
    copy /Y "dist\lib\*.jar" "!TARGET_DIR!\lib\" >nul
) else if exist "lib" (
    copy /Y "lib\*.jar" "!TARGET_DIR!\lib\" >nul
)

if exist "src\resources\icons" (
    xcopy /E /I /Y "src\resources\icons\*" "!TARGET_DIR!\icons\" >nul
)

if exist "db.properties" copy /Y "db.properties" "!TARGET_DIR!\" >nul
if exist "theme.properties" copy /Y "theme.properties" "!TARGET_DIR!\" >nul
if exist "RunApp.bat" copy /Y "RunApp.bat" "!TARGET_DIR!\" >nul
if exist "uninstall.bat" copy /Y "uninstall.bat" "!TARGET_DIR!\" >nul

echo [*] Files copied successfully.
echo.

REM 4. Create Desktop & Start Menu Shortcuts via VBScript
set "VBS_FILE=%TEMP%\CreateShortcut_%RANDOM%.vbs"
(
echo Set ws = CreateObject("WScript.Shell"^)
echo Set link = ws.CreateShortcut(ws.SpecialFolders("Desktop"^) ^& "\Employee Management.lnk"^)
echo link.TargetPath = "!JAVA_EXE!"
echo link.Arguments = "-jar """ ^& "!TARGET_DIR!\Employee_Managment.jar"""
echo link.WorkingDirectory = "!TARGET_DIR!"
echo link.Description = "Employee Management System"
echo If ws.Environment("Process"^).Item("SystemRoot"^) ^<^> "" Then
echo     link.IconLocation = "!TARGET_DIR!\icons\app.ico, 0"
echo End If
echo link.Save
echo Set link2 = ws.CreateShortcut(ws.SpecialFolders("Programs"^) ^& "\Employee Management.lnk"^)
echo link2.TargetPath = "!JAVA_EXE!"
echo link2.Arguments = "-jar """ ^& "!TARGET_DIR!\Employee_Managment.jar"""
echo link2.WorkingDirectory = "!TARGET_DIR!"
echo link2.Description = "Employee Management System"
echo link2.IconLocation = "!TARGET_DIR!\icons\app.ico, 0"
echo link2.Save
) > "%VBS_FILE%"

cscript //nologo "%VBS_FILE%"
if exist "%VBS_FILE%" del "%VBS_FILE%"

REM 5. Register in Windows Control Panel (Programs & Features)
echo [*] Registering in Windows Control Panel (لوحة التحكم)...
set "REG_KEY=HKCU\Software\Microsoft\Windows\CurrentVersion\Uninstall\EmployeeManagement"
reg add "!REG_KEY!" /v "DisplayName" /t REG_SZ /d "نظام إدارة الموظفين والمصاريف (Employee Management)" /f >nul 2>&1
reg add "!REG_KEY!" /v "DisplayIcon" /t REG_SZ /d "!TARGET_DIR!\icons\app.ico" /f >nul 2>&1
reg add "!REG_KEY!" /v "DisplayVersion" /t REG_SZ /d "2.0.0" /f >nul 2>&1
reg add "!REG_KEY!" /v "Publisher" /t REG_SZ /d "Employee Management System" /f >nul 2>&1
reg add "!REG_KEY!" /v "InstallLocation" /t REG_SZ /d "!TARGET_DIR!" /f >nul 2>&1
reg add "!REG_KEY!" /v "UninstallString" /t REG_SZ /d "\"!TARGET_DIR!\uninstall.bat\"" /f >nul 2>&1
reg add "!REG_KEY!" /v "NoModify" /t REG_DWORD /d 1 /f >nul 2>&1
reg add "!REG_KEY!" /v "NoRepair" /t REG_DWORD /d 1 /f >nul 2>&1

echo.
echo ========================================================
echo [SUCCESS] Installation Completed Successfully!
echo [تم بنجاح] تم تثبيت البرنامج وتسجيله في لوحة التحكم وإنشاء أيقونة سطح المكتب بنجاح!
echo ========================================================
echo.
echo Press any key to exit...
pause >nul
