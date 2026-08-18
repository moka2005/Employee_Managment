import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class SettingsPanel {
    public JPanel frame;
    public static CardLayout cardLayout;
    public static JPanel cardPanel;

    private DefaultTableModel logModel;
    private JTable logTable;
    private TableRowSorter<DefaultTableModel> logSorter;
    private JTextField searchField;

    public SettingsPanel(CardLayout cardLayout, JPanel cardPanel) {
        SettingsPanel.cardLayout = cardLayout;
        SettingsPanel.cardPanel = cardPanel;
        frame = new JPanel(new BorderLayout(15, 15));
        frame.setBackground(UITheme.getBgMain());
        frame.setBorder(new EmptyBorder(15, 15, 15, 15));

        buildUI();
    }

    private void buildUI() {
        // TOP Header Card
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("إعدادات النظام والمظهر • سجل النشاطات", SwingConstants.RIGHT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.getTextPrimary());

        JLabel subTitleLabel = new JLabel("التحكم في المظهر (Light / Dark)، تتبع نشاطات المستخدمين، وإعدادات قاعدة البيانات", SwingConstants.RIGHT);
        subTitleLabel.setFont(UITheme.FONT_REGULAR);
        subTitleLabel.setForeground(UITheme.getTextSecondary());

        topCard.add(titleLabel, BorderLayout.NORTH);
        topCard.add(subTitleLabel, BorderLayout.CENTER);
        frame.add(topCard, BorderLayout.NORTH);

        // CENTER: Top settings cards + Bottom Activity Log
        JPanel centerContainer = new JPanel(new BorderLayout(15, 15));
        centerContainer.setOpaque(false);

        // Top Row: 3 Setting Cards
        JPanel topGrid = new JPanel(new BorderLayout(15, 15));
        topGrid.setOpaque(false);

        // 1. Theme Visual Selector Card
        JPanel themeContainerCard = UITheme.createCard();
        themeContainerCard.setLayout(new BorderLayout(10, 10));

        JLabel themeHeader = new JLabel("نمط ومظهر البرنامج (Style / Theme)", SwingConstants.RIGHT);
        themeHeader.setFont(UITheme.FONT_SUBTITLE);
        themeHeader.setForeground(UITheme.PRIMARY);
        themeContainerCard.add(themeHeader, BorderLayout.NORTH);

        JPanel visualCardBox = new JPanel(new GridLayout(1, 2, 15, 0));
        visualCardBox.setOpaque(false);

        JPanel lightCard = createThemeVisualOption("الوضع الفاتح (Default)", false, !ThemeManager.isDarkMode());
        JPanel darkCard = createThemeVisualOption("الوضع الداكن (Dark)", true, ThemeManager.isDarkMode());

        visualCardBox.add(lightCard);
        visualCardBox.add(darkCard);
        themeContainerCard.add(visualCardBox, BorderLayout.CENTER);

        // 2. Right Box (User Info & DB Cards)
        JPanel rightInfoBox = new JPanel(new GridLayout(1, 2, 15, 0));
        rightInfoBox.setOpaque(false);

        // User Card
        JPanel userCard = UITheme.createCard();
        userCard.setLayout(new BorderLayout(10, 10));

        JLabel userTitle = new JLabel("المستخدم المسجل حالياً", SwingConstants.RIGHT);
        userTitle.setFont(UITheme.FONT_SUBTITLE);
        userTitle.setForeground(UITheme.SUCCESS);

        JPanel userInfoBox = new JPanel(new GridLayout(2, 1, 4, 4));
        userInfoBox.setOpaque(false);

        JLabel userNameLbl = new JLabel(SessionManager.getUsername(), IconHelper.getIcon("user.png", 22, 22), SwingConstants.CENTER);
        userNameLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userNameLbl.setForeground(UITheme.getTextPrimary());
        userNameLbl.setHorizontalTextPosition(SwingConstants.LEFT);

        JLabel userRoleLbl = new JLabel("الصلاحية: " + SessionManager.getRoleDisplay(), SwingConstants.CENTER);
        userRoleLbl.setFont(UITheme.FONT_REGULAR);
        userRoleLbl.setForeground(UITheme.getTextSecondary());

        userInfoBox.add(userNameLbl);
        userInfoBox.add(userRoleLbl);

        userCard.add(userTitle, BorderLayout.NORTH);
        userCard.add(userInfoBox, BorderLayout.CENTER);

        // DB Card
        JPanel dbCard = UITheme.createCard();
        dbCard.setLayout(new BorderLayout(10, 10));

        JLabel dbTitle = new JLabel("قاعدة البيانات", SwingConstants.RIGHT);
        dbTitle.setFont(UITheme.FONT_SUBTITLE);
        dbTitle.setForeground(UITheme.PURPLE);

        JLabel dbStatus = new JLabel("الحالة: متصل بنجاح", IconHelper.getIcon("check.png", 16, 16), SwingConstants.CENTER);
        dbStatus.setFont(UITheme.FONT_BOLD);
        dbStatus.setForeground(UITheme.SUCCESS);
        dbStatus.setHorizontalTextPosition(SwingConstants.LEFT);

        JButton dbConfigBtn = UITheme.createSecondaryButton("تعديل إعدادات الاتصال", IconHelper.getIcon("settings.png", 16, 16));
        dbConfigBtn.addActionListener(e -> showDBSettingsDialog());

        dbCard.add(dbTitle, BorderLayout.NORTH);
        dbCard.add(dbStatus, BorderLayout.CENTER);
        dbCard.add(dbConfigBtn, BorderLayout.SOUTH);

        rightInfoBox.add(userCard);
        rightInfoBox.add(dbCard);

        topGrid.add(themeContainerCard, BorderLayout.CENTER);
        topGrid.add(rightInfoBox, BorderLayout.WEST);

        centerContainer.add(topGrid, BorderLayout.NORTH);

        // Bottom: Activity Log Card
        JPanel logCard = UITheme.createCard();
        logCard.setLayout(new BorderLayout(10, 12));

        JPanel logTopBar = new JPanel(new BorderLayout());
        logTopBar.setOpaque(false);

        JLabel logTitle = new JLabel("سجل العمليات والنشاطات (Audit Trail)", SwingConstants.RIGHT);
        logTitle.setFont(UITheme.FONT_SUBTITLE);
        logTitle.setForeground(UITheme.getTextPrimary());

        JPanel logSearchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logSearchBox.setOpaque(false);
        logSearchBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel sIcon = new JLabel("بحث في السجل:", IconHelper.getIcon("search.png", 16, 16), SwingConstants.RIGHT);
        sIcon.setFont(UITheme.FONT_BOLD);
        sIcon.setForeground(UITheme.getTextPrimary());
        sIcon.setHorizontalTextPosition(SwingConstants.LEFT);
        searchField = UITheme.createTextField(16);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
            private void applyFilter() {
                String t = searchField.getText().trim();
                if (logSorter != null) {
                    if (t.isEmpty()) logSorter.setRowFilter(null);
                    else logSorter.setRowFilter(RowFilter.regexFilter("(?i)" + t));
                }
            }
        });

        logSearchBox.add(sIcon);
        logSearchBox.add(searchField);

        logTopBar.add(logTitle, BorderLayout.EAST);
        logTopBar.add(logSearchBox, BorderLayout.WEST);
        logCard.add(logTopBar, BorderLayout.NORTH);

        // Log Table
        logModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        logModel.addColumn("رقم السجل");
        logModel.addColumn("المستخدم المسؤول");
        logModel.addColumn("نوع العملية");
        logModel.addColumn("تفاصيل العملية");
        logModel.addColumn("الوقت والتاريخ");

        logTable = new JTable(logModel);
        UITheme.styleTable(logTable);
        logSorter = new TableRowSorter<>(logModel);
        logTable.setRowSorter(logSorter);

        JScrollPane scrollPane = UITheme.createScrollPane(logTable);
        logCard.add(scrollPane, BorderLayout.CENTER);

        // Log Bottom Actions
        JPanel logActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        logActions.setOpaque(false);
        logActions.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton refreshBtn = UITheme.createSecondaryButton("تحديث السجل", IconHelper.getIcon("empty.png", 16, 16));
        refreshBtn.addActionListener(e -> refreshLogs());

        JButton exportBtn = UITheme.createButton("تصدير السجل لـ Excel", IconHelper.getIcon("excel.png", 16, 16), new Color(16, 185, 129), Color.WHITE);
        exportBtn.addActionListener(e -> ExcelExporter.exportTable(logTable, "سجل_نشاطات_النظام.xlsx", "سجل النشاطات"));

        JButton printBtn = UITheme.createButton("طباعة السجل", IconHelper.getIcon("print.png", 16, 16), new Color(99, 102, 241), Color.WHITE);
        printBtn.addActionListener(e -> {
            try {
                logTable.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("سجل نشاطات وعمليات النظام"), new MessageFormat("صفحة {0}"));
            } catch (Exception ex) {
                UITheme.showThemedMessage(frame, "خطأ في الطباعة: " + ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        logActions.add(refreshBtn);
        logActions.add(exportBtn);
        logActions.add(printBtn);
        logCard.add(logActions, BorderLayout.SOUTH);

        centerContainer.add(logCard, BorderLayout.CENTER);
        frame.add(centerContainer, BorderLayout.CENTER);

        refreshLogs();
    }

    private JPanel createThemeVisualOption(String title, boolean isDarkTheme, boolean isSelected) {
        JPanel panel = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(UITheme.getBgCardSecondary());
                g2.fillRoundRect(0, 0, w, h, 14, 14);

                if (isSelected) {
                    g2.setColor(new Color(16, 185, 129));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, 14, 14);
                } else {
                    g2.setColor(UITheme.getBorderColor());
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                }

                int previewW = w - 40;
                int previewH = 55;
                int previewX = (w - previewW) / 2;
                int previewY = 12;

                g2.setColor(isDarkTheme ? new Color(15, 23, 42) : Color.WHITE);
                g2.fillRoundRect(previewX, previewY, previewW, previewH, 8, 8);
                g2.setColor(isDarkTheme ? new Color(51, 65, 85) : new Color(203, 213, 225));
                g2.drawRoundRect(previewX, previewY, previewW, previewH, 8, 8);

                g2.setColor(isDarkTheme ? new Color(30, 41, 59) : new Color(241, 245, 249));
                g2.fillRoundRect(previewX, previewY, previewW, 14, 8, 8);

                g2.setColor(new Color(239, 68, 68));
                g2.fillOval(previewX + 6, previewY + 4, 6, 6);
                g2.setColor(new Color(245, 158, 11));
                g2.fillOval(previewX + 16, previewY + 4, 6, 6);
                g2.setColor(new Color(16, 185, 129));
                g2.fillOval(previewX + 26, previewY + 4, 6, 6);

                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(previewX + 10, previewY + 22, previewW / 2, 8, 4, 4);

                g2.setColor(isDarkTheme ? new Color(51, 65, 85) : new Color(226, 232, 240));
                g2.fillRoundRect(previewX + 10, previewY + 36, previewW - 20, 6, 3, 3);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setPreferredSize(new Dimension(170, 110));
        panel.setBorder(new EmptyBorder(70, 8, 8, 8));

        JLabel titleLbl = new JLabel((isSelected ? "(مفعل) " : "") + title, SwingConstants.CENTER);
        titleLbl.setFont(UITheme.FONT_BOLD);
        titleLbl.setForeground(isSelected ? new Color(16, 185, 129) : UITheme.getTextPrimary());
        panel.add(titleLbl, BorderLayout.SOUTH);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ThemeManager.isDarkMode() != isDarkTheme) {
                    ThemeManager.setDarkMode(isDarkTheme);
                    MainDashboard.reloadTheme("page7");
                }
            }
        });

        return panel;
    }

    public void refreshLogs() {
        String sql = "SELECT log_id, username, action_type, description, log_time FROM activity_log ORDER BY log_id DESC LIMIT 500";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            logModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("log_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("action_type"));
                row.add(rs.getString("description"));
                java.sql.Timestamp ts = rs.getTimestamp("log_time");
                row.add(ts != null ? sdf.format(ts) : "");
                logModel.addRow(row);
            }
        } catch (Exception ex) {
            System.err.println("Error refreshing activity logs: " + ex.getMessage());
        }
    }

    private void showDBSettingsDialog() {
        JDialog dialog = new JDialog((Frame) null, "إعدادات محرك قاعدة البيانات (Hybrid DB Engine)", true);
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
