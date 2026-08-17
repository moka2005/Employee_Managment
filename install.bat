@echo off
chcp 65001 > nul
echo =========================================================
echo    تثبيت نظام إدارة الموظفين والمصاريف لنظام Windows
echo =========================================================

REM 1. Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] لم يتم العثور على Java في نظام Windows.
    echo يرجى تحميل وتثبيت جافا من الموقع الرسمي:
    echo https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo [OK] تم العثور على Java في جهازك.

REM 2. Set Directory paths
set "TARGET_DIR=%LOCALAPPDATA%\EmployeeManagement"
if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"
if not exist "%TARGET_DIR%\lib" mkdir "%TARGET_DIR%\lib"
if not exist "%TARGET_DIR%\icons" mkdir "%TARGET_DIR%\icons"

REM 3. Copy Application Files
echo [*] جاري نسخ ملفات البرنامج إلى: %TARGET_DIR%
copy /Y "dist\Employee_Managment.jar" "%TARGET_DIR%\" >nul
copy /Y "dist\lib\*.jar" "%TARGET_DIR%\lib\" >nul
if exist "src\resources\icons" (
    xcopy /E /I /Y "src\resources\icons\*" "%TARGET_DIR%\icons\" >nul
) else if exist "icons" (
    xcopy /E /I /Y "icons\*" "%TARGET_DIR%\icons\" >nul
)
if exist "db.properties" copy /Y "db.properties" "%TARGET_DIR%\" >nul
if exist "theme.properties" copy /Y "theme.properties" "%TARGET_DIR%\" >nul

REM 4. Create Desktop Shortcut via VBScript
set "SHORTCUT_VBS=%TEMP%\CreateShortcut.vbs"
set "DESKTOP_DIR=%USERPROFILE%\Desktop"

echo Set oWS = WScript.CreateObject("WScript.Shell") > "%SHORTCUT_VBS%"
echo sLinkFile = "%DESKTOP_DIR%\إدارة الموظفين والمصاريف.lnk" >> "%SHORTCUT_VBS%"
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> "%SHORTCUT_VBS%"
echo oLink.TargetPath = "javaw.exe" >> "%SHORTCUT_VBS%"
echo oLink.Arguments = "-jar """ & "%TARGET_DIR%\Employee_Managment.jar""" >> "%SHORTCUT_VBS%"
echo oLink.WorkingDirectory = "%TARGET_DIR%" >> "%SHORTCUT_VBS%"
echo oLink.Description = "نظام إدارة الموظفين والمصاريف" >> "%SHORTCUT_VBS%"
if exist "%TARGET_DIR%\icons\profil.ico" (
    echo oLink.IconLocation = "%TARGET_DIR%\icons\profil.ico, 0" >> "%SHORTCUT_VBS%"
)
echo oLink.Save >> "%SHORTCUT_VBS%"

cscript /nologo "%SHORTCUT_VBS%"
del "%SHORTCUT_VBS%"

echo =========================================================
echo [OK] تم تثبيت البرنامج وإنشاء أيقونة الاختصار على سطح المكتب بنجاح!
echo يمكنك الآن تشغيل البرنامج مباشرة بالنقر على أيقونة سطح المكتب.
echo =========================================================
pause
