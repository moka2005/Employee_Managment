@echo off
chcp 65001 > nul
title تثبيت نظام إدارة الموظفين والمصاريف
color 0B

echo =========================================================
echo    تثبيت نظام إدارة الموظفين والمصاريف لنظام Windows
echo =========================================================
echo.

REM 1. Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] لم يتم العثور على Java في نظام Windows.
    echo يرجى تحميل وتثبيت جافا من الرابط التالي:
    echo https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

echo [OK] تم التحقق من وجود Java في جهازك بنجاح.
echo.

REM 2. Set Installation Directory
set "TARGET_DIR=%LOCALAPPDATA%\EmployeeManagement"
echo [*] جاري تهيئة مجلد التثبيت في: %TARGET_DIR%
if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"
if not exist "%TARGET_DIR%\lib" mkdir "%TARGET_DIR%\lib"
if not exist "%TARGET_DIR%\icons" mkdir "%TARGET_DIR%\icons"

REM 3. Copy Application Files
echo [*] جاري نسخ ملفات البرنامج والمكتبات والأيقونات...
copy /Y "dist\Employee_Managment.jar" "%TARGET_DIR%\" >nul
copy /Y "dist\lib\*.jar" "%TARGET_DIR%\lib\" >nul
if exist "src\resources\icons" (
    xcopy /E /I /Y "src\resources\icons\*" "%TARGET_DIR%\icons\" >nul
)
if exist "db.properties" copy /Y "db.properties" "%TARGET_DIR%\" >nul
if exist "theme.properties" copy /Y "theme.properties" "%TARGET_DIR%\" >nul
if exist "RunApp.bat" copy /Y "RunApp.bat" "%TARGET_DIR%\" >nul

REM 4. Create Desktop & Start Menu Shortcuts with Branded Icon via VBScript
set "SHORTCUT_VBS=%TEMP%\CreateShortcut.vbs"
set "DESKTOP_DIR=%USERPROFILE%\Desktop"
set "START_MENU=%APPDATA%\Microsoft\Windows\Start Menu\Programs"

echo [*] جاري إنشاء أيقونة الاختصار على سطح المكتب وقائمة ابدأ...

(
    echo Set oWS = WScript.CreateObject("WScript.Shell"^)
    echo sLinkFile = "%DESKTOP_DIR%\إدارة الموظفين والمصاريف.lnk"
    echo Set oLink = oWS.CreateShortcut(sLinkFile^)
    echo oLink.TargetPath = "javaw.exe"
    echo oLink.Arguments = "-jar """ ^& "%TARGET_DIR%\Employee_Managment.jar"""
    echo oLink.WorkingDirectory = "%TARGET_DIR%"
    echo oLink.Description = "نظام إدارة الموظفين والمصاريف"
    echo If oWS.Environment("Process"^).Item("SystemRoot"^) ^<^> "" Then
    echo     oLink.IconLocation = "%TARGET_DIR%\icons\app.ico, 0"
    echo End If
    echo oLink.Save
    
    echo sLinkFile2 = "%START_MENU%\إدارة الموظفين والمصاريف.lnk"
    echo Set oLink2 = oWS.CreateShortcut(sLinkFile2^)
    echo oLink2.TargetPath = "javaw.exe"
    echo oLink2.Arguments = "-jar """ ^& "%TARGET_DIR%\Employee_Managment.jar"""
    echo oLink2.WorkingDirectory = "%TARGET_DIR%"
    echo oLink2.Description = "نظام إدارة الموظفين والمصاريف"
    echo oLink2.IconLocation = "%TARGET_DIR%\icons\app.ico, 0"
    echo oLink2.Save
) > "%SHORTCUT_VBS%"

cscript /nologo "%SHORTCUT_VBS%" >nul 2>&1
if exist "%SHORTCUT_VBS%" del "%SHORTCUT_VBS%"

echo.
echo =========================================================
echo 🎉 تم تثبيت البرنامج وإنشاء أيقونة الاختصار بنجاح!
echo 📍 الأيقونة موجودة الآن على سطح المكتب (Desktop)
echo 📍 وموجودة أيضاً في قائمة ابدأ (Start Menu)
echo =========================================================
echo.
pause
