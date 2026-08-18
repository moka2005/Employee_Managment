import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame {
    public static JFrame frame;
    private JTextField userField;
    private JPasswordField passField;
    private JCheckBox passShowCheck;
    private JButton loginBtn;
    private JButton exitBtn;
    private JButton settingsBtn;
    private JButton themeToggleBtn;

    public LoginFrame(int width, int height) {
        frame = new JFrame("تسجيل الدخول - نظام إدارة الموظفين والمصاريف");
        frame.setSize(width > 0 ? width : 1000, height > 0 ? height : 650);
        frame.setMinimumSize(new Dimension(850, 550));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        buildUI();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void buildUI() {
        frame.getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.getBgMain());

        // Top Bar inside Login (Theme Toggle in Corner)
        JPanel topToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        topToolBar.setOpaque(false);

        themeToggleBtn = UITheme.createSecondaryButton(
            ThemeManager.isDarkMode() ? "الوضع الفاتح" : "الوضع الداكن",
            IconHelper.getIcon(ThemeManager.isDarkMode() ? "sun.png" : "moon.png", 18, 18)
        );
        themeToggleBtn.addActionListener(e -> {
            ThemeManager.toggleTheme();
            UITheme.applyThemeToUIManager();
            buildUI();
        });

        topToolBar.add(themeToggleBtn);
        mainPanel.add(topToolBar, BorderLayout.NORTH);

        // Center Login Card Container
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.getBgCard());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(UITheme.getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 45, 30, 45));
        card.setPreferredSize(new Dimension(470, 520));

        ImageIcon appIcon = IconHelper.getIcon("profil.png", 64, 64);
        JLabel iconLabel = new JLabel(appIcon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("تسجيل الدخول");
        titleLabel.setFont(UITheme.FONT_HERO);
        titleLabel.setForeground(UITheme.getTextPrimary());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("نظام إدارة الموظفين، الحضور والمصاريف");
        subTitleLabel.setFont(UITheme.FONT_REGULAR);
        subTitleLabel.setForeground(UITheme.getTextSecondary());
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Field
        JLabel userLabel = UITheme.createFieldLabel("اسم المستخدم");
        userLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        userField = UITheme.createTextField(20);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Password Field
        JLabel passLabel = UITheme.createFieldLabel("كلمة المرور");
        passLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        passField = UITheme.createPasswordField(20);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Show Password Checkbox
        passShowCheck = new JCheckBox("إظهار كلمة المرور");
        passShowCheck.setFont(UITheme.FONT_SMALL_BOLD);
        passShowCheck.setForeground(UITheme.getTextSecondary());
        passShowCheck.setOpaque(false);
        passShowCheck.setFocusPainted(false);
        passShowCheck.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        passShowCheck.setAlignmentX(Component.RIGHT_ALIGNMENT);
        passShowCheck.addActionListener(e -> {
            if (passShowCheck.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });

        // Buttons
        loginBtn = UITheme.createPrimaryButton("دخول للنظام", IconHelper.getIcon("Frame (1).png", 18, 18));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        exitBtn = UITheme.createSecondaryButton("خروج", IconHelper.getIcon("logout.png", 16, 16));
        exitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        settingsBtn = new JButton("إعدادات قاعدة البيانات", IconHelper.getIcon("settings.png", 16, 16));
        settingsBtn.setHorizontalTextPosition(SwingConstants.LEFT);
        settingsBtn.setFont(UITheme.FONT_SMALL);
        settingsBtn.setForeground(UITheme.PRIMARY);
        settingsBtn.setContentAreaFilled(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Actions
        loginBtn.addActionListener(e -> performLogin());
        passField.addActionListener(e -> performLogin());
        userField.addActionListener(e -> passField.requestFocusInWindow());
        exitBtn.addActionListener(e -> System.exit(0));
        settingsBtn.addActionListener(e -> showDBSettingsDialog());

        // Assemble Card
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(subTitleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 22)));

        JPanel userBox = new JPanel();
        userBox.setLayout(new BoxLayout(userBox, BoxLayout.Y_AXIS));
        userBox.setOpaque(false);
        userBox.add(userLabel);
        userBox.add(Box.createRigidArea(new Dimension(0, 4)));
        userBox.add(userField);
        card.add(userBox);

        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel passBox = new JPanel();
        passBox.setLayout(new BoxLayout(passBox, BoxLayout.Y_AXIS));
        passBox.setOpaque(false);
        passBox.add(passLabel);
        passBox.add(Box.createRigidArea(new Dimension(0, 4)));
        passBox.add(passField);
        card.add(passBox);

        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(passShowCheck);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(exitBtn);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(settingsBtn);

        centerWrapper.add(card);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.getRootPane().setDefaultButton(loginBtn);
        frame.revalidate();
        frame.repaint();
    }

    private void performLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            UITheme.showThemedMessage(frame, "الرجاء إدخال اسم المستخدم وكلمة المرور", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT id, FirstN, pass, role FROM users WHERE FirstN = ? AND pass = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String userRole = rs.getString("role");
                    if (userRole == null || userRole.trim().isEmpty()) {
                        userRole = "admin".equalsIgnoreCase(username) ? "ADMIN" : "USER";
                    }

                    // Set session
                    SessionManager.setSession(userId, username, userRole);

                    // Log activity
                    ActivityLogger.log("تسجيل دخول", "قام المستخدم (" + username + " - " + SessionManager.getRoleDisplay() + ") بتسجيل الدخول للنظام");

                    frame.dispose();
                    new MainDashboard(frame.getWidth(), frame.getHeight(), userId);
                } else {
                    UITheme.showThemedMessage(frame, "اسم المستخدم أو كلمة المرور غير صحيحة!", "خطأ في تسجيل الدخول", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            UITheme.showThemedMessage(frame, "تعذر الاتصال بقاعدة البيانات!\n" + ex.getMessage() + "\n\nيمكنك تعديل إعدادات الاتصال عبر زر الإعدادات أسفل الشاشة.", "خطأ في الاتصال", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDBSettingsDialog() {
        JDialog dialog = new JDialog(frame, "إعدادات محرك قاعدة البيانات (Hybrid DB Engine)", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(UITheme.getBgCard());

        JPanel mainBox = new JPanel(new BorderLayout(10, 15));
        mainBox.setBackground(UITheme.getBgCard());
        mainBox.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Engine Selector Radio Buttons
        JPanel engineCard = new JPanel(new GridLayout(2, 1, 6, 6));
        engineCard.setOpaque(false);
        engineCard.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        engineCard.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UITheme.getBorderColor(), 1, true),
            "اختر نوع محرك قاعدة البيانات:",
            SwingConstants.RIGHT,
            0,
            UITheme.FONT_BOLD,
            UITheme.getTextPrimary()
        ));

        JRadioButton sqliteRadio = UITheme.createRadioButton("قاعدة بيانات مدمجة (SQLite) - تعمل على أي جهاز فوراً");
        JRadioButton postgresRadio = UITheme.createRadioButton("خادم شبكة (PostgreSQL Server) - اتصال شبكي/سحابي");
        ButtonGroup bg = new ButtonGroup();
        bg.add(sqliteRadio);
        bg.add(postgresRadio);

        if (DBConfig.isSqlite()) {
            sqliteRadio.setSelected(true);
        } else {
            postgresRadio.setSelected(true);
        }

        engineCard.add(sqliteRadio);
        engineCard.add(postgresRadio);
        mainBox.add(engineCard, BorderLayout.NORTH);

        // Parameters Form
        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.setBackground(UITheme.getBgCard());
        p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JTextField sqlitePathField = UITheme.createTextField(10);
        sqlitePathField.setText(DBConfig.getSqlitePath());

        JTextField hostField = UITheme.createTextField(10);
        hostField.setText(DBConfig.getHost());

        JTextField portField = UITheme.createTextField(10);
        portField.setText(DBConfig.getPort());

        JTextField nameField = UITheme.createTextField(10);
        nameField.setText(DBConfig.getDbName());

        JTextField uField = UITheme.createTextField(10);
        uField.setText(DBConfig.getUser());

        JPasswordField pField = UITheme.createPasswordField(10);
        pField.setText(DBConfig.getPassword());

        p.add(UITheme.createFieldLabel("مسار ملف SQLite:"));
        p.add(sqlitePathField);
        p.add(UITheme.createFieldLabel("خادم PostgreSQL (Host):"));
        p.add(hostField);
        p.add(UITheme.createFieldLabel("المنفذ (Port):"));
        p.add(portField);
        p.add(UITheme.createFieldLabel("اسم القاعدة (Database):"));
        p.add(nameField);
        p.add(UITheme.createFieldLabel("اسم المستخدم (User):"));
        p.add(uField);
        p.add(UITheme.createFieldLabel("كلمة المرور (Password):"));
        p.add(pField);

        Runnable updateFieldsState = () -> {
            boolean isPg = postgresRadio.isSelected();
            sqlitePathField.setEnabled(!isPg);
            hostField.setEnabled(isPg);
            portField.setEnabled(isPg);
            nameField.setEnabled(isPg);
            uField.setEnabled(isPg);
            pField.setEnabled(isPg);
        };
        sqliteRadio.addActionListener(e -> updateFieldsState.run());
        postgresRadio.addActionListener(e -> updateFieldsState.run());
        updateFieldsState.run();

        mainBox.add(p, BorderLayout.CENTER);

        // Bottom Actions
        JButton testBtn = UITheme.createSecondaryButton("اختبار الاتصال", null);
        testBtn.addActionListener(e -> {
            if (sqliteRadio.isSelected()) {
                DBConfig.updateSqlite(sqlitePathField.getText().trim());
            } else {
                DBConfig.updatePostgres(hostField.getText(), portField.getText(), nameField.getText(), uField.getText(), new String(pField.getPassword()));
            }
            if (DBManager.testConnection()) {
                UITheme.showThemedMessage(dialog, "تم الاتصال بنجاح بمحرك: " + DBConfig.getDbType(), "نجاح الاتصال", JOptionPane.INFORMATION_MESSAGE);
            } else {
                UITheme.showThemedMessage(dialog, "فشل الاتصال بقاعدة البيانات!", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton saveBtn = UITheme.createPrimaryButton("حفظ وتطبيق", null);
        saveBtn.addActionListener(e -> {
            if (sqliteRadio.isSelected()) {
                DBConfig.updateSqlite(sqlitePathField.getText().trim());
            } else {
                DBConfig.updatePostgres(hostField.getText(), portField.getText(), nameField.getText(), uField.getText(), new String(pField.getPassword()));
            }
            DBManager.initializeDatabase();
            dialog.dispose();
            UITheme.showThemedMessage(frame, "تم حفظ الإعدادات وتهيئة قاعدة البيانات بنجاح!", "تم بنجاح", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(UITheme.getBgCard());
        btnPanel.add(testBtn);
        btnPanel.add(saveBtn);

        dialog.add(mainBox, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
