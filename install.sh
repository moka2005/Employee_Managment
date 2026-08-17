#!/bin/bash
echo "========================================================="
echo "   تثبيت نظام إدارة الموظفين والمصاريف (Employee Management)   "
echo "========================================================="

# 1. Check Java installation
if ! command -v java &> /dev/null; then
    echo "⚠️ لم يتم العثور على Java في النظام. يرجى تثبيت Java أولاً:"
    echo "   sudo apt update && sudo apt install -y default-jre"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo "✅ تم العثور على جافا: $JAVA_VERSION"

# 2. Target Installation Directory
INSTALL_DIR="$HOME/.local/share/EmployeeManagement"
mkdir -p "$INSTALL_DIR/lib"
mkdir -p "$INSTALL_DIR/icons"

# 3. Copy Application Files
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo "📦 جاري نسخ ملفات البرنامج إلى: $INSTALL_DIR ..."

cp -r "$SCRIPT_DIR/dist/Employee_Managment.jar" "$INSTALL_DIR/"
cp -r "$SCRIPT_DIR/dist/lib/"* "$INSTALL_DIR/lib/"
if [ -d "$SCRIPT_DIR/src/resources/icons" ]; then
    cp -r "$SCRIPT_DIR/src/resources/icons/"* "$INSTALL_DIR/icons/"
elif [ -d "$SCRIPT_DIR/icons" ]; then
    cp -r "$SCRIPT_DIR/icons/"* "$INSTALL_DIR/icons/"
fi
if [ -f "$SCRIPT_DIR/db.properties" ]; then
    cp "$SCRIPT_DIR/db.properties" "$INSTALL_DIR/"
fi
if [ -f "$SCRIPT_DIR/theme.properties" ]; then
    cp "$SCRIPT_DIR/theme.properties" "$INSTALL_DIR/"
fi

# 4. Create Desktop Entry in ~/.local/share/applications
DESKTOP_FILE="$HOME/.local/share/applications/employee_management.desktop"
mkdir -p "$HOME/.local/share/applications"

cat <<EOF > "$DESKTOP_FILE"
[Desktop Entry]
Version=2.0
Type=Application
Name=إدارة الموظفين والمصاريف
GenericName=Employee & Expense Management System
Comment=نظام شامل لإدارة الموظفين، الحضور، الرواتب والمصاريف
Exec=java -jar "$INSTALL_DIR/Employee_Managment.jar"
Icon=$INSTALL_DIR/icons/profil.png
Terminal=false
Categories=Office;Finance;Java;
StartupNotify=true
EOF

chmod +x "$DESKTOP_FILE"

# 5. Create Desktop shortcut if ~/Desktop exists
if [ -d "$HOME/Desktop" ]; then
    cp "$DESKTOP_FILE" "$HOME/Desktop/"
    chmod +x "$HOME/Desktop/employee_management.desktop" 2>/dev/null || true
fi

echo ""
echo "========================================================="
echo "🎉 تم تثبيت البرنامج بنجاح على جهازك!"
echo "📍 يمكنك الآن تشغيل البرنامج مباشرة من قائمة التطبيقات (Applications Menu)"
echo "   أو من خلال سطح المكتب (Desktop)."
echo "========================================================="
