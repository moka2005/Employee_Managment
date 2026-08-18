; Inno Setup Script for Employee Management System
; To build: Download Inno Setup from https://jrsoftware.org/isinfo.php
; Open this file in Inno Setup Compiler and click Build > Compile

#define MyAppName "نظام إدارة الموظفين والمصاريف"
#define MyAppNameEn "Employee Management System"
#define MyAppVersion "2.0.0"
#define MyAppPublisher "Employee Management"
#define MyAppExeName "Employee_Managment.jar"

[Setup]
AppId={{D37E84C1-8A99-4A9B-93E1-BE8F7A189201}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
; يثبّت في %LocalAppData%\EmployeeManagement بدون صلاحية مدير
DefaultDirName={localappdata}\EmployeeManagement
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
; لا يحتاج صلاحية Admin
PrivilegesRequired=lowest
OutputDir=Output
OutputBaseFilename=EmployeeManagement_Setup_v2
SetupIconFile=src\resources\icons\app.ico
Compression=lzma2/max
SolidCompression=yes
WizardStyle=modern
DisableProgramGroupPage=yes
; تفعيل الإلغاء التلقائي من لوحة التحكم عبر Inno Setup Uninstaller الأصلي
Uninstallable=yes
CreateUninstallRegKey=yes
UninstallDisplayIcon={app}\icons\app.ico
UninstallDisplayName={#MyAppName}

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "إنشاء اختصار على سطح المكتب"; GroupDescription: "خيارات إضافية:"; Flags: unchecked

[Files]
Source: "dist\Employee_Managment.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "src\resources\icons\*"; DestDir: "{app}\icons"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "db.properties"; DestDir: "{app}"; Flags: ignoreversion
Source: "theme.properties"; DestDir: "{app}"; Flags: ignoreversion
Source: "RunApp.bat"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "javaw.exe"; Parameters: "-jar ""{app}\Employee_Managment.jar"""; WorkingDir: "{app}"; IconFilename: "{app}\icons\app.ico"
Name: "{group}\{cm:UninstallProgram,{#MyAppNameEn}}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "javaw.exe"; Parameters: "-jar ""{app}\Employee_Managment.jar"""; WorkingDir: "{app}"; IconFilename: "{app}\icons\app.ico"; Tasks: desktopicon

[Run]
; تشغيل البرنامج بعد التثبيت مباشرة
Filename: "javaw.exe"; Parameters: "-jar ""{app}\Employee_Managment.jar"""; WorkingDir: "{app}"; Description: "تشغيل البرنامج الآن"; Flags: nowait postinstall skipifsilent
