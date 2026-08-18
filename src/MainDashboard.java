import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainDashboard {
    public static JFrame frame2;
    public static CardLayout cardLayout;
    public static JPanel cardPanel;
    public static int currentUserId;
    public static String activePage = "page1";

    // Sub-panels
    private static EmployeePanel employeePanel;
    private static AttendancePanel attendancePanel;
    private static WageFlowPanel wageFlowPanel;
    private static ExpensesPanel expensesPanel;
    private static UserAccountsPanel userPanel;
    private static SettingsPanel settingsPanel;
    private static JPanel homePanel;

    // KPI Labels
    private static JLabel totalEmpLabel;
    private static JLabel activeEmpLabel;
    private static JLabel todayAbsenceLabel;
    private static JLabel monthlyExpenseLabel;

    public MainDashboard(int width, int height, int userId) {
        currentUserId = userId;
        frame2 = new JFrame("نظام إدارة الموظفين والمصاريف • " + SessionManager.getUsername());
        frame2.setSize(width > 0 ? width : 1280, height > 0 ? height : 720);
        frame2.setMinimumSize(new Dimension(950, 580));
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setLayout(new BorderLayout());

        // Maximize window by default so it fits cleanly on 1366x768 and other screens
        frame2.setExtendedState(JFrame.MAXIMIZED_BOTH);

        buildMainUI("page1");

        frame2.setLocationRelativeTo(null);
        frame2.setVisible(true);
    }

    public static void buildMainUI(String initialPage) {
        UITheme.applyThemeToUIManager();
        frame2.getContentPane().removeAll();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize Screen Panels
        homePanel = createHomePanel();
        userPanel = new UserAccountsPanel(cardLayout, cardPanel);
        employeePanel = new EmployeePanel(cardLayout, cardPanel);
        attendancePanel = new AttendancePanel(cardLayout, cardPanel);
        wageFlowPanel = new WageFlowPanel(cardLayout, cardPanel);
        expensesPanel = new ExpensesPanel(cardLayout, cardPanel);
        settingsPanel = new SettingsPanel(cardLayout, cardPanel);

        // Register Pages in CardLayout
        cardPanel.add(homePanel, "page1");
        cardPanel.add(userPanel.frame, "page2");
        cardPanel.add(employeePanel.frame, "page3");
        cardPanel.add(attendancePanel.frame, "page4");
        cardPanel.add(wageFlowPanel.frame, "page5");
        cardPanel.add(expensesPanel.frame, "page6");
        cardPanel.add(settingsPanel.frame, "page7");

        // Main Layout Structure
        JPanel topBar = createTopBar();
        JPanel sideBar = createSideBar();

        frame2.add(topBar, BorderLayout.NORTH);
        frame2.add(sideBar, BorderLayout.EAST);
        frame2.add(cardPanel, BorderLayout.CENTER);

        frame2.revalidate();
        frame2.repaint();

        refreshDashboardKPIs();
        activePage = initialPage != null ? initialPage : "page1";
        cardLayout.show(cardPanel, activePage);
    }

    public static void reloadTheme(String targetPage) {
        if (frame2 == null) return;
        buildMainUI(targetPage != null ? targetPage : activePage);
    }

    private static JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.getBgTopBar());
        topBar.setPreferredSize(new Dimension(0, 52));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.getBorderColor()),
            new EmptyBorder(6, 15, 6, 15)
        ));

        // System Title + Logged In User Chip (Right)
        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBox.setOpaque(false);
        rightBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel titleLabel = new JLabel("نظام إدارة الموظفين والمصاريف");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.getTextPrimary());

        // Current User Badge Chip
        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        userBadge.setBackground(SessionManager.isAdmin() ? UITheme.PRIMARY : UITheme.getBgCardSecondary());
        userBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.getBorderColor(), 1, true),
            new EmptyBorder(2, 6, 2, 6)
        ));

        JLabel userIcon = new JLabel(IconHelper.getIcon("user.png", 14, 14));
        JLabel userName = new JLabel(SessionManager.getUsername() + " (" + SessionManager.getRoleDisplay() + ")");
        userName.setFont(UITheme.FONT_BOLD);
        userName.setForeground(SessionManager.isAdmin() ? Color.WHITE : UITheme.getTextPrimary());

        userBadge.add(userIcon);
        userBadge.add(userName);

        rightBox.add(titleLabel);
        rightBox.add(userBadge);

        // Date and Time + Logout (Left)
        Locale arabicLocale = new Locale("ar");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE ، d MMMM yyyy", arabicLocale);
        JLabel dateLabel = new JLabel(sdf.format(new Date()));
        dateLabel.setFont(UITheme.FONT_REGULAR);
        dateLabel.setForeground(UITheme.getTextSecondary());

        JPanel leftBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftBox.setOpaque(false);

        JButton logoutBtn = UITheme.createDangerButton("تسجيل الخروج", IconHelper.getIcon("logout.png", 14, 14));
        logoutBtn.setPreferredSize(new Dimension(130, 32));
        logoutBtn.addActionListener(e -> {
            int c = UITheme.showThemedConfirm(frame2, "هل تريد حقاً تسجيل الخروج من النظام؟", "تأكيد تسجيل الخروج", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                ActivityLogger.log("تسجيل خروج", "قام المستخدم (" + SessionManager.getUsername() + ") بتسجيل الخروج");
                SessionManager.clearSession();
                frame2.dispose();
                new LoginFrame(frame2.getWidth(), frame2.getHeight());
            }
        });

        leftBox.add(logoutBtn);
        leftBox.add(dateLabel);

        topBar.add(rightBox, BorderLayout.EAST);
        topBar.add(leftBox, BorderLayout.WEST);

        return topBar;
    }

    private static JPanel createSideBar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UITheme.getBgSidebar());
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UITheme.getBorderColor()));

        java.util.List<String> titles = new java.util.ArrayList<>();
        java.util.List<String> iconNames = new java.util.ArrayList<>();
        java.util.List<String> targets = new java.util.ArrayList<>();

        titles.add("الرئيسية");
        iconNames.add("home.png");
        targets.add("page1");

        titles.add("إدارة العمال");
        iconNames.add("employee.png");
        targets.add("page3");

        titles.add("تسجيل الحضور");
        iconNames.add("absences.png");
        targets.add("page4");

        titles.add("متابعة الحضور والرواتب");
        iconNames.add("dollar.png");
        targets.add("page5");

        titles.add("إدارة المصاريف");
        iconNames.add("expense.png");
        targets.add("page6");

        if (SessionManager.isAdmin()) {
            titles.add("إدارة الحسابات");
            iconNames.add("profile-svgrepo-com 1.png");
            targets.add("page2");
        }

        titles.add("الإعدادات والمظهر");
        iconNames.add("settings.png");
        targets.add("page7");

        JPanel navList = new JPanel(new GridLayout(titles.size(), 1, 0, 4));
        navList.setOpaque(false);
        navList.setBorder(new EmptyBorder(10, 8, 10, 8));

        for (int i = 0; i < titles.size(); i++) {
            final String targetPage = targets.get(i);
            Color btnBg = ThemeManager.isDarkMode() ? new Color(30, 41, 59) : new Color(241, 245, 249);
            Color btnFg = UITheme.getTextPrimary();
            JButton btn = UITheme.createButton(titles.get(i), IconHelper.getIcon(iconNames.get(i), 18, 18), btnBg, btnFg);
            btn.setHorizontalAlignment(SwingConstants.RIGHT);
            btn.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            btn.setPreferredSize(new Dimension(184, 38));

            btn.addActionListener(e -> {
                activePage = targetPage;
                if (targetPage.equals("page1")) {
                    refreshDashboardKPIs();
                } else if (targetPage.equals("page3")) {
                    EmployeePanel.imagePath = "";
                    EmployeePanel.Action_radio(EmployeePanel.radio_buttom, EmployeePanel.g);
                } else if (targetPage.equals("page4")) {
                    AttendancePanel.update();
                } else if (targetPage.equals("page5")) {
                    WageFlowPanel.refreshEmployeeList();
                } else if (targetPage.equals("page6")) {
                    ExpensesPanel.update(true);
                } else if (targetPage.equals("page2")) {
                    userPanel.refreshUsers();
                } else if (targetPage.equals("page7")) {
                    settingsPanel.refreshLogs();
                }
                cardLayout.show(cardPanel, targetPage);
            });

            navList.add(btn);
        }

        sidebar.add(navList, BorderLayout.NORTH);

        JLabel versionLabel = new JLabel("الإصدار 2.0 • 2026", SwingConstants.CENTER);
        versionLabel.setFont(UITheme.FONT_SMALL);
        versionLabel.setForeground(UITheme.getTextSecondary());
        versionLabel.setBorder(new EmptyBorder(8, 8, 8, 8));
        sidebar.add(versionLabel, BorderLayout.SOUTH);

        return sidebar;
    }

    private static JPanel createHomePanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(UITheme.getBgMain());
        p.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Welcome Header
        JPanel headerCard = UITheme.createCard();
        headerCard.setLayout(new BorderLayout());

        JLabel welcomeTitle = new JLabel("مرحباً بك يا " + SessionManager.getUsername() + " في لوحة تحكم النظام", SwingConstants.RIGHT);
        welcomeTitle.setFont(UITheme.FONT_HERO);
        welcomeTitle.setForeground(UITheme.getTextPrimary());

        JLabel welcomeDesc = new JLabel("أنت مسجل بصلاحية (" + SessionManager.getRoleDisplay() + ") • تسجل كافة العمليات باسمك في سجل النظام.", SwingConstants.RIGHT);
        welcomeDesc.setFont(UITheme.FONT_REGULAR);
        welcomeDesc.setForeground(UITheme.getTextSecondary());

        headerCard.add(welcomeTitle, BorderLayout.NORTH);
        headerCard.add(welcomeDesc, BorderLayout.CENTER);

        // KPI Statistics Cards (Grid 4x1)
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 15, 15));
        kpiGrid.setOpaque(false);

        totalEmpLabel = new JLabel("0", SwingConstants.CENTER);
        activeEmpLabel = new JLabel("0", SwingConstants.CENTER);
        todayAbsenceLabel = new JLabel("0", SwingConstants.CENTER);
        monthlyExpenseLabel = new JLabel("0 DA", SwingConstants.CENTER);

        kpiGrid.add(createKpiCard("إجمالي العمال", totalEmpLabel, "employee.png", UITheme.PRIMARY));
        kpiGrid.add(createKpiCard("العمال الحاليين", activeEmpLabel, "profil.png", UITheme.SUCCESS));
        kpiGrid.add(createKpiCard("تسجيلات حضور اليوم", todayAbsenceLabel, "absences.png", UITheme.WARNING));
        kpiGrid.add(createKpiCard("مصاريف الشهر الحالي", monthlyExpenseLabel, "expense.png", UITheme.PURPLE));

        // Quick Actions
        JPanel quickActionsCard = UITheme.createCard();
        quickActionsCard.setLayout(new BorderLayout(10, 15));

        JLabel qaTitle = new JLabel("إجراءات سريعة", SwingConstants.RIGHT);
        qaTitle.setFont(UITheme.FONT_SUBTITLE);
        qaTitle.setForeground(UITheme.getTextPrimary());
        quickActionsCard.add(qaTitle, BorderLayout.NORTH);

        JPanel actionBtns = new JPanel(new GridLayout(2, 2, 15, 15));
        actionBtns.setOpaque(false);

        JButton btn1 = UITheme.createPrimaryButton("إضافة عامل جديد", IconHelper.getIcon("addicon.png", 20, 20));
        btn1.addActionListener(e -> {
            activePage = "page3";
            EmployeePanel.imagePath = "";
            cardLayout.show(cardPanel, "page3");
        });

        JButton btn2 = UITheme.createSuccessButton("تسجيل الحضور اليومي", IconHelper.getIcon("include.png", 20, 20));
        btn2.addActionListener(e -> {
            activePage = "page4";
            AttendancePanel.update();
            cardLayout.show(cardPanel, "page4");
        });

        JButton btn3 = UITheme.createButton("متابعة المستحقات والأجور", IconHelper.getIcon("dollar.png", 20, 20), UITheme.WARNING, Color.WHITE);
        btn3.addActionListener(e -> {
            activePage = "page5";
            WageFlowPanel.refreshEmployeeList();
            cardLayout.show(cardPanel, "page5");
        });

        JButton btn4 = UITheme.createButton("إضافة ومتابعة المصاريف", IconHelper.getIcon("expense.png", 20, 20), UITheme.PURPLE, Color.WHITE);
        btn4.addActionListener(e -> {
            activePage = "page6";
            ExpensesPanel.update(true);
            cardLayout.show(cardPanel, "page6");
        });

        actionBtns.add(btn1);
        actionBtns.add(btn2);
        actionBtns.add(btn3);
        actionBtns.add(btn4);

        quickActionsCard.add(actionBtns, BorderLayout.CENTER);

        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        centerContent.add(headerCard);
        centerContent.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContent.add(kpiGrid);
        centerContent.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContent.add(quickActionsCard);

        p.add(centerContent, BorderLayout.CENTER);
        return p;
    }

    private static JPanel createKpiCard(String title, JLabel valueLabel, String iconName, Color accentColor) {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel t = new JLabel(title, SwingConstants.RIGHT);
        t.setFont(UITheme.FONT_BOLD);
        t.setForeground(UITheme.getTextSecondary());

        JLabel icon = new JLabel(IconHelper.getIcon(iconName, 26, 26));

        top.add(t, BorderLayout.EAST);
        top.add(icon, BorderLayout.WEST);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public static void refreshDashboardKPIs() {
        try (Connection conn = DBManager.getConnection(); Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) as cnt FROM employee")) {
                if (rs.next()) totalEmpLabel.setText(rs.getString("cnt"));
            }
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) as cnt FROM employee WHERE activ_emp = 1")) {
                if (rs.next()) activeEmpLabel.setText(rs.getString("cnt"));
            }
            String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) as cnt FROM absences WHERE absence_date = '" + todayStr + "'")) {
                if (rs.next()) todayAbsenceLabel.setText(rs.getString("cnt"));
            }
            String monthPrefix = new SimpleDateFormat("yyyy-MM").format(new Date());
            int monthlySum = 0;
            try (ResultSet rs = stmt.executeQuery("SELECT total, input_date FROM expense")) {
                while (rs.next()) {
                    String inDate = rs.getString("input_date");
                    if (inDate != null && inDate.startsWith(monthPrefix)) {
                        try {
                            monthlySum += Integer.parseInt(rs.getString("total").replaceAll("[^0-9]", ""));
                        } catch (Exception ignored) {}
                    }
                }
            }
            monthlyExpenseLabel.setText(monthlySum + " DA");
        } catch (Exception ex) {
            System.err.println("Notice updating KPIs: " + ex.getMessage());
        }
    }
}
