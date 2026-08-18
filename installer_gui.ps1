Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

[System.Windows.Forms.Application]::EnableVisualStyles()

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $scriptDir) { $scriptDir = Get-Location }

# Determine base directory where dist/ is located
$projectRoot = $scriptDir
if (Test-Path "$scriptDir\..\dist\Employee_Managment.jar") {
    $projectRoot = "$scriptDir\.."
}

# Main Form
$form = New-Object System.Windows.Forms.Form
$form.Text = "تثبيت برنامج إدارة الموظفين والمصاريف"
$form.Size = New-Object System.Drawing.Size(520, 480)
$form.StartPosition = "CenterScreen"
$form.FormBorderStyle = "FixedDialog"
$form.MaximizeBox = $false
$form.MinimizeBox = $false
$form.RightToLeft = [System.Windows.Forms.RightToLeft]::Yes
$form.RightToLeftLayout = $true
$form.BackColor = [System.Drawing.Color]::FromArgb(248, 250, 252)

# Form Icon
$icoPath = "$projectRoot\src\resources\icons\app.ico"
if (Test-Path $icoPath) {
    try {
        $form.Icon = New-Object System.Drawing.Icon($icoPath)
    } catch {}
}

# Header Panel
$headerPanel = New-Object System.Windows.Forms.Panel
$headerPanel.Dock = "Top"
$headerPanel.Height = 85
$headerPanel.BackColor = [System.Drawing.Color]::FromArgb(15, 23, 42)

$headerTitle = New-Object System.Windows.Forms.Label
$headerTitle.Text = "نظام إدارة الموظفين والمصاريف"
$headerTitle.Font = New-Object System.Drawing.Font("Segoe UI", 14, [System.Drawing.FontStyle]::Bold)
$headerTitle.ForeColor = [System.Drawing.Color]::White
$headerTitle.Location = New-Object System.Drawing.Point(20, 15)
$headerTitle.Size = New-Object System.Drawing.Size(460, 30)

$headerSub = New-Object System.Windows.Forms.Label
$headerSub.Text = "معالج التثبيت والإعداد التلقائي لنظام Windows"
$headerSub.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$headerSub.ForeColor = [System.Drawing.Color]::FromArgb(148, 163, 184)
$headerSub.Location = New-Object System.Drawing.Point(20, 48)
$headerSub.Size = New-Object System.Drawing.Size(460, 25)

$headerPanel.Controls.Add($headerTitle)
$headerPanel.Controls.Add($headerSub)
$form.Controls.Add($headerPanel)

# Content Container
$lblPath = New-Object System.Windows.Forms.Label
$lblPath.Text = "مجلد التثبيت:"
$lblPath.Font = New-Object System.Drawing.Font("Segoe UI", 9, [System.Drawing.FontStyle]::Bold)
$lblPath.Location = New-Object System.Drawing.Point(25, 105)
$lblPath.Size = New-Object System.Drawing.Size(455, 20)
$form.Controls.Add($lblPath)

$txtPath = New-Object System.Windows.Forms.TextBox
$defaultInstallDir = "$env:LOCALAPPDATA\EmployeeManagement"
$txtPath.Text = $defaultInstallDir
$txtPath.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$txtPath.Location = New-Object System.Drawing.Point(25, 130)
$txtPath.Size = New-Object System.Drawing.Size(370, 26)
$txtPath.RightToLeft = [System.Windows.Forms.RightToLeft]::No
$form.Controls.Add($txtPath)

$btnBrowse = New-Object System.Windows.Forms.Button
$btnBrowse.Text = "تصفح..."
$btnBrowse.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$btnBrowse.Location = New-Object System.Drawing.Point(405, 128)
$btnBrowse.Size = New-Object System.Drawing.Size(75, 30)
$btnBrowse.BackColor = [System.Drawing.Color]::White
$btnBrowse.Add_Click({
    $fbd = New-Object System.Windows.Forms.FolderBrowserDialog
    $fbd.SelectedPath = $txtPath.Text
    if ($fbd.ShowDialog() -eq [System.Windows.Forms.DialogResult]::OK) {
        $txtPath.Text = $fbd.SelectedPath
    }
})
$form.Controls.Add($btnBrowse)

# Options Checkboxes
$chkDesktop = New-Object System.Windows.Forms.CheckBox
$chkDesktop.Text = "إنشاء أيقونة اختصار على سطح المكتب (Desktop)"
$chkDesktop.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$chkDesktop.Checked = $true
$chkDesktop.Location = New-Object System.Drawing.Point(30, 175)
$chkDesktop.Size = New-Object System.Drawing.Size(450, 25)
$form.Controls.Add($chkDesktop)

$chkStartMenu = New-Object System.Windows.Forms.CheckBox
$chkStartMenu.Text = "إضافة البرنامج إلى قائمة ابدأ (Start Menu)"
$chkStartMenu.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$chkStartMenu.Checked = $true
$chkStartMenu.Location = New-Object System.Drawing.Point(30, 205)
$chkStartMenu.Size = New-Object System.Drawing.Size(450, 25)
$form.Controls.Add($chkStartMenu)

$chkLaunch = New-Object System.Windows.Forms.CheckBox
$chkLaunch.Text = "تشغيل البرنامج فور اكتمال التثبيت"
$chkLaunch.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$chkLaunch.Checked = $true
$chkLaunch.Location = New-Object System.Drawing.Point(30, 235)
$chkLaunch.Size = New-Object System.Drawing.Size(450, 25)
$form.Controls.Add($chkLaunch)

# Progress Bar & Status
$progressBar = New-Object System.Windows.Forms.ProgressBar
$progressBar.Location = New-Object System.Drawing.Point(25, 280)
$progressBar.Size = New-Object System.Drawing.Size(455, 20)
$progressBar.Visible = $false
$form.Controls.Add($progressBar)

$lblStatus = New-Object System.Windows.Forms.Label
$lblStatus.Text = "جاهز لبدء التثبيت."
$lblStatus.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$lblStatus.ForeColor = [System.Drawing.Color]::FromArgb(71, 85, 105)
$lblStatus.Location = New-Object System.Drawing.Point(25, 310)
$lblStatus.Size = New-Object System.Drawing.Size(455, 45)
$form.Controls.Add($lblStatus)

# Bottom Buttons
$btnInstall = New-Object System.Windows.Forms.Button
$btnInstall.Text = "تثبيت الآن"
$btnInstall.Font = New-Object System.Drawing.Font("Segoe UI", 10, [System.Drawing.FontStyle]::Bold)
$btnInstall.BackColor = [System.Drawing.Color]::FromArgb(37, 99, 235)
$btnInstall.ForeColor = [System.Drawing.Color]::White
$btnInstall.FlatStyle = [System.Windows.Forms.FlatStyle]::Flat
$btnInstall.FlatAppearance.BorderSize = 0
$btnInstall.Location = New-Object System.Drawing.Point(365, 380)
$btnInstall.Size = New-Object System.Drawing.Size(115, 38)
$btnInstall.Cursor = [System.Windows.Forms.Cursors]::Hand

$btnCancel = New-Object System.Windows.Forms.Button
$btnCancel.Text = "إلغاء"
$btnCancel.Font = New-Object System.Drawing.Font("Segoe UI", 9)
$btnCancel.BackColor = [System.Drawing.Color]::FromArgb(226, 232, 240)
$btnCancel.FlatStyle = [System.Windows.Forms.FlatStyle]::Flat
$btnCancel.FlatAppearance.BorderSize = 0
$btnCancel.Location = New-Object System.Drawing.Point(270, 380)
$btnCancel.Size = New-Object System.Drawing.Size(85, 38)
$btnCancel.Cursor = [System.Windows.Forms.Cursors]::Hand
$btnCancel.Add_Click({ $form.Close() })

$form.Controls.Add($btnInstall)
$form.Controls.Add($btnCancel)

# Function: Locate Java Runtime
function Get-JavaExecutable {
    # 1. Try PATH
    $cmd = Get-Command "javaw.exe" -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }

    # 2. Try JAVA_HOME
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javaw.exe")) {
        return "$env:JAVA_HOME\bin\javaw.exe"
    }

    # 3. Search Program Files
    $candidates = @(
        "$env:ProgramFiles\Java",
        "${env:ProgramFiles(x86)}\Java",
        "$env:ProgramFiles\Eclipse Adoptium",
        "$env:ProgramFiles\Microsoft",
        "$env:ProgramFiles\Amazon Corretto",
        "$env:ProgramFiles\BellSoft",
        "$env:ProgramFiles\Zulu"
    )
    foreach ($c in $candidates) {
        if (Test-Path $c) {
            $found = Get-ChildItem -Path $c -Filter "javaw.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
            if ($found) { return $found.FullName }
        }
    }

    return "javaw.exe" # Fallback
}

# Install Execution Action
$btnInstall.Add_Click({
    $targetDir = $txtPath.Text.Trim()
    if ([string]::IsNullOrWhiteSpace($targetDir)) {
        [System.Windows.Forms.MessageBox]::Show("يرجى تحديد مسار مجلد التثبيت.", "تنبيه", [System.Windows.Forms.MessageBoxButtons]::OK, [System.Windows.Forms.MessageBoxIcon]::Warning)
        return
    }

    $btnInstall.Enabled = $false
    $btnCancel.Enabled = $false
    $btnBrowse.Enabled = $false
    $txtPath.Enabled = $false
    $chkDesktop.Enabled = $false
    $chkStartMenu.Enabled = $false
    $chkLaunch.Enabled = $false
    $progressBar.Visible = $true
    $progressBar.Value = 10

    $lblStatus.Text = "جاري التحقق من بيئة Java..."
    $form.Refresh()

    $javawPath = Get-JavaExecutable

    $lblStatus.Text = "جاري إنشاء المجلدات ونسخ الملفات..."
    $progressBar.Value = 30
    $form.Refresh()

    try {
        if (-not (Test-Path $targetDir)) { New-Item -ItemType Directory -Path $targetDir -Force | Out-Null }
        if (-not (Test-Path "$targetDir\lib")) { New-Item -ItemType Directory -Path "$targetDir\lib" -Force | Out-Null }
        if (-not (Test-Path "$targetDir\icons")) { New-Item -ItemType Directory -Path "$targetDir\icons" -Force | Out-Null }

        # Copy JAR and Libs
        $sourceJar = "$projectRoot\dist\Employee_Managment.jar"
        if (-not (Test-Path $sourceJar)) {
            $sourceJar = "$projectRoot\Employee_Managment.jar"
        }
        Copy-Item -Path $sourceJar -Destination "$targetDir\Employee_Managment.jar" -Force

        $sourceLib = "$projectRoot\dist\lib"
        if (-not (Test-Path $sourceLib)) { $sourceLib = "$projectRoot\lib" }
        if (Test-Path $sourceLib) {
            Copy-Item -Path "$sourceLib\*" -Destination "$targetDir\lib\" -Recurse -Force
        }

        # Copy Icons
        $sourceIcons = "$projectRoot\src\resources\icons"
        if (Test-Path $sourceIcons) {
            Copy-Item -Path "$sourceIcons\*" -Destination "$targetDir\icons\" -Recurse -Force
        }

        # Copy Configs
        if (Test-Path "$projectRoot\db.properties") {
            Copy-Item -Path "$projectRoot\db.properties" -Destination "$targetDir\" -Force
        }
        if (Test-Path "$projectRoot\theme.properties") {
            Copy-Item -Path "$projectRoot\theme.properties" -Destination "$targetDir\" -Force
        }
        if (Test-Path "$projectRoot\RunApp.bat") {
            Copy-Item -Path "$projectRoot\RunApp.bat" -Destination "$targetDir\" -Force
        }

        $progressBar.Value = 70
        $lblStatus.Text = "جاري إنشاء الأيقونات والاختصارات..."
        $form.Refresh()

        $wshShell = New-Object -ComObject WScript.Shell
        $targetIco = "$targetDir\icons\app.ico"

        # Desktop Shortcut
        if ($chkDesktop.Checked) {
            $desktopPath = [System.Environment]::GetFolderPath('Desktop')
            $shortcutPath = Join-Path $desktopPath "إدارة الموظفين والمصاريف.lnk"
            $shortcut = $wshShell.CreateShortcut($shortcutPath)
            $shortcut.TargetPath = $javawPath
            $shortcut.Arguments = "-jar `"$targetDir\Employee_Managment.jar`""
            $shortcut.WorkingDirectory = $targetDir
            $shortcut.Description = "نظام إدارة الموظفين والمصاريف"
            if (Test-Path $targetIco) {
                $shortcut.IconLocation = "$targetIco, 0"
            }
            $shortcut.Save()
        }

        # Start Menu Shortcut
        if ($chkStartMenu.Checked) {
            $startMenuPath = [System.Environment]::GetFolderPath('Programs')
            $shortcutPath2 = Join-Path $startMenuPath "إدارة الموظفين والمصاريف.lnk"
            $shortcut2 = $wshShell.CreateShortcut($shortcutPath2)
            $shortcut2.TargetPath = $javawPath
            $shortcut2.Arguments = "-jar `"$targetDir\Employee_Managment.jar`""
            $shortcut2.WorkingDirectory = $targetDir
            $shortcut2.Description = "نظام إدارة الموظفين والمصاريف"
            if (Test-Path $targetIco) {
                $shortcut2.IconLocation = "$targetIco, 0"
            }
            $shortcut2.Save()
        }

        $progressBar.Value = 100
        $lblStatus.Text = "✅ اكتمل التثبيت بنجاح!"
        $form.Refresh()

        # Launch if selected
        if ($chkLaunch.Checked) {
            Start-Process -FilePath $javawPath -ArgumentList "-jar `"$targetDir\Employee_Managment.jar`"" -WorkingDirectory $targetDir
        }

        [System.Windows.Forms.MessageBox]::Show("تم تثبيت البرنامج وإنشاء الأيقونة على سطح المكتب بنجاح!", "نجاح التثبيت", [System.Windows.Forms.MessageBoxButtons]::OK, [System.Windows.Forms.MessageBoxIcon]::Information)
        $form.Close()

    } catch {
        [System.Windows.Forms.MessageBox]::Show("حدث خطأ أثناء التثبيت:`n$($_.Exception.Message)", "خطأ في التثبيت", [System.Windows.Forms.MessageBoxButtons]::OK, [System.Windows.Forms.MessageBoxIcon]::Error)
        $btnInstall.Enabled = $true
        $btnCancel.Enabled = $true
    }
})

# Show Form
[void]$form.ShowDialog()
