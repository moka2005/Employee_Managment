@echo off
setlocal EnableDelayedExpansion
chcp 65001 >nul
title إلغاء تثبيت نظام إدارة الموظفين والمصاريف
color 0C

echo ========================================================
echo   إلغاء تثبيت نظام إدارة الموظفين والمصاريف
echo ========================================================
echo.

set /p confirm="هل أنت متأكد من رغبتك في إلغاء تثبيت البرنامج؟ (y/n): "
if /i "%confirm%" neq "y" if /i "%confirm%" neq "yes" (
    echo تم إلغاء العملية.
    pause
    exit /b 0
)

echo.

REM ---- اكتشاف مسار التثبيت ----
set "TARGET_DIR=%LOCALAPPDATA%\EmployeeManagement"

REM ---- قتل أي نسخة جارية من البرنامج ----
echo [*] جاري إيقاف البرنامج إن كان يعمل...
taskkill /f /im javaw.exe /fi "WINDOWTITLE eq *Employee*" >nul 2>&1
timeout /t 2 /nobreak >nul

REM ---- حذف الاختصارات ----
echo [*] جاري حذف الاختصارات...
if exist "%USERPROFILE%\Desktop\Employee Management.lnk" (
    del /f /q "%USERPROFILE%\Desktop\Employee Management.lnk"
)
if exist "%APPDATA%\Microsoft\Windows\Start Menu\Programs\Employee Management.lnk" (
    del /f /q "%APPDATA%\Microsoft\Windows\Start Menu\Programs\Employee Management.lnk"
)
if exist "%APPDATA%\Microsoft\Windows\Start Menu\Programs\Employee Management" (
    rmdir /s /q "%APPDATA%\Microsoft\Windows\Start Menu\Programs\Employee Management"
)

REM ---- حذف مفتاح Registry ----
echo [*] جاري إزالة البيانات من لوحة التحكم...
reg delete "HKCU\Software\Microsoft\Windows\CurrentVersion\Uninstall\EmployeeManagement" /f >nul 2>&1

REM ---- حذف مجلد التطبيق بعد خروجنا منه ----
echo [*] جاري حذف ملفات البرنامج من: !TARGET_DIR!
if not exist "!TARGET_DIR!" (
    echo [!] مجلد البرنامج غير موجود، ربما تم حذفه مسبقاً.
    goto :done
)

REM نستخدم PowerShell لحذف المجلد بشكل موثوق حتى لو كانت الملفات مقفلة
powershell -NoProfile -NonInteractive -Command ^
    "Start-Sleep -Seconds 1; Remove-Item -Path '!TARGET_DIR!' -Recurse -Force -ErrorAction SilentlyContinue" >nul 2>&1

REM تحقق هل نجح الحذف
if exist "!TARGET_DIR!" (
    REM محاولة ثانية بـ cmd في الخلفية بعد ثانيتين
    start "" /b cmd /c "timeout /t 3 >nul & rmdir /s /q ""!TARGET_DIR!"""
    echo [!] سيتم إتمام الحذف بعد لحظات. قد تحتاج لإعادة التشغيل.
) else (
    echo [OK] تم حذف الملفات بنجاح.
)

:done
echo.
echo ========================================================
echo [OK] تم إلغاء تثبيت البرنامج بنجاح.
echo ========================================================
echo.
pause
