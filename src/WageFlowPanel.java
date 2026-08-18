import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class WageFlowPanel {
    public JPanel frame;
    public static CardLayout cardLayout;
    public static JPanel cardPanel;

    public static DefaultTableModel model = new DefaultTableModel();
    public static JTable table;
    public static JComboBox<String> id_combo = new JComboBox<>();
    public static JComboBox<String> pay_combo = new JComboBox<>();
    public static JDateChooser date_text1;
    public static JDateChooser date_text2;
    public static JComboBox<String> state_filter_combo;
    public static JLabel a = new JLabel("0 DA");
    public static JLabel daysCountLabel = new JLabel("0 يوم");

    private ProfilePhotoPanel photoPanel;
    private JLabel empDetailsLabel;

    public WageFlowPanel(CardLayout cardLayout, JPanel cardPanel) {
        WageFlowPanel.cardLayout = cardLayout;
        WageFlowPanel.cardPanel = cardPanel;
        frame = new JPanel(new BorderLayout(10, 10));
        frame.setBackground(UITheme.getBgMain());
        frame.setBorder(new EmptyBorder(8, 10, 8, 10));

        buildUI();
    }

    private void buildUI() {
        // TOP Header Card
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout(10, 0));

        JLabel titleLabel = new JLabel("متابعة الحضور، الغيابات وتسوية الأجور", SwingConstants.RIGHT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.getTextPrimary());

        JPanel selectBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectBox.setOpaque(false);
        selectBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel selectLabel = UITheme.createFieldLabel("اختر الموظف:");
        UITheme.styleComboBox(id_combo);
        id_combo.setPreferredSize(new Dimension(240, 34));
        id_combo.addActionListener(e -> {
            loadSelectedEmployeeData();
            update(true);
        });

        selectBox.add(selectLabel);
        selectBox.add(id_combo);

        topCard.add(titleLabel, BorderLayout.EAST);
        topCard.add(selectBox, BorderLayout.WEST);
        frame.add(topCard, BorderLayout.NORTH);

        // CENTER: Left Sidebar + Right Records Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Left Financial Settlement Card
        JPanel leftCard = UITheme.createCard();
        leftCard.setLayout(new BorderLayout(8, 10));
        leftCard.setPreferredSize(new Dimension(310, 0));

        // Photo & Info
        JPanel infoBox = new JPanel(new BorderLayout(0, 6));
        infoBox.setOpaque(false);

        photoPanel = new ProfilePhotoPanel();
        photoPanel.setPreferredSize(new Dimension(110, 110));
        infoBox.add(photoPanel, BorderLayout.CENTER);

        empDetailsLabel = new JLabel("الموظف: -", SwingConstants.CENTER);
        empDetailsLabel.setFont(UITheme.FONT_BOLD);
        empDetailsLabel.setForeground(UITheme.getTextPrimary());
        infoBox.add(empDetailsLabel, BorderLayout.SOUTH);

        leftCard.add(infoBox, BorderLayout.NORTH);

        // Wage Totals & Settlement Form
        JPanel settleBox = new JPanel(new GridLayout(6, 1, 6, 8));
        settleBox.setOpaque(false);
        settleBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel totalDueTitle = new JLabel("المستحقات غير المدفوعة:", SwingConstants.CENTER);
        totalDueTitle.setFont(UITheme.FONT_BOLD);
        totalDueTitle.setForeground(UITheme.getTextSecondary());

        a.setFont(new Font("Segoe UI", Font.BOLD, 26));
        a.setForeground(UITheme.DANGER);
        a.setHorizontalAlignment(SwingConstants.CENTER);

        daysCountLabel.setFont(UITheme.FONT_BOLD);
        daysCountLabel.setForeground(UITheme.getTextPrimary());
        daysCountLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel payComboTitle = UITheme.createFieldLabel("اختر عدد الأيام المراد تسويتها:");

        UITheme.styleComboBox(pay_combo);
        pay_combo.setPreferredSize(new Dimension(0, 38));

        JButton payBtn = UITheme.createSuccessButton("تسوية ودفع الأيام المحددة", IconHelper.getIcon("include.png", 18, 18));
        payBtn.addActionListener(e -> executePayment());

        settleBox.add(totalDueTitle);
        settleBox.add(a);
        settleBox.add(daysCountLabel);
        settleBox.add(payComboTitle);
        settleBox.add(pay_combo);
        settleBox.add(payBtn);

        leftCard.add(settleBox, BorderLayout.CENTER);
        centerPanel.add(leftCard, BorderLayout.WEST);

        // Right Table & Filters Card
        JPanel rightCard = UITheme.createCard();
        rightCard.setLayout(new BorderLayout(10, 12));

        // Filters Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        filterBar.setOpaque(false);
        filterBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        date_text1 = new JDateChooser();
        date_text1.setDateFormatString("yyyy-MM-dd");
        date_text1.setPreferredSize(new Dimension(145, 38));
        UITheme.styleDateChooser(date_text1);

        date_text2 = new JDateChooser(new Date());
        date_text2.setDateFormatString("yyyy-MM-dd");
        date_text2.setPreferredSize(new Dimension(145, 38));
        UITheme.styleDateChooser(date_text2);

        state_filter_combo = new JComboBox<>(new String[]{"الكل", "حاضر", "غائب", "غير مدفوع", "مدفوع"});
        UITheme.styleComboBox(state_filter_combo);
        state_filter_combo.setPreferredSize(new Dimension(130, 38));

        JButton filterBtn = UITheme.createPrimaryButton("تصفية البيانات", IconHelper.getIcon("filter.png", 18, 18));
        filterBtn.setPreferredSize(new Dimension(145, 38));
        filterBtn.addActionListener(e -> update(false));

        JButton resetBtn = UITheme.createSecondaryButton("تحديث / الكل", IconHelper.getIcon("empty.png", 18, 18));
        resetBtn.setPreferredSize(new Dimension(145, 38));
        resetBtn.addActionListener(e -> {
            date_text1.setDate(null);
            date_text2.setDate(new Date());
            state_filter_combo.setSelectedIndex(0);
            update(true);
        });

        filterBar.add(UITheme.createFieldLabel("من تاريخ:"));
        filterBar.add(date_text1);
        filterBar.add(UITheme.createFieldLabel("إلى تاريخ:"));
        filterBar.add(date_text2);
        filterBar.add(UITheme.createFieldLabel("الحالة:"));
        filterBar.add(state_filter_combo);
        filterBar.add(filterBtn);
        filterBar.add(resetBtn);

        rightCard.add(filterBar, BorderLayout.NORTH);

        // Table Model Setup (Includes hidden raw DB absence_id in column 5)
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        model.addColumn("رقم");
        model.addColumn("تاريخ التسجيل");
        model.addColumn("حالة الحضور");
        model.addColumn("حالة الدفع");
        model.addColumn("السبب / الملاحظات");
        model.addColumn("معرف السجل");

        table = new JTable(model);
        UITheme.styleTable(table);

        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        JScrollPane scrollPane = UITheme.createScrollPane(table);
        rightCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel botActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botActions.setOpaque(false);
        botActions.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton deleteBtn = UITheme.createDangerButton("حذف السجل المحدد", IconHelper.getIcon("delete.png", 16, 16));
        deleteBtn.addActionListener(e -> deleteSelectedAbsence());

        JButton exportBtn = UITheme.createButton("تصدير كشف Excel", IconHelper.getIcon("excel.png", 16, 16), new Color(16, 185, 129), Color.WHITE);
        exportBtn.addActionListener(e -> ExcelExporter.exportTable(table, "كشف_حضور_" + getSelectedEmpId() + ".xlsx", "كشف الحضور"));

        JButton printBtn = UITheme.createButton("طباعة الكشف", IconHelper.getIcon("print.png", 16, 16), new Color(99, 102, 241), Color.WHITE);
        printBtn.addActionListener(e -> {
            try {
                String empName = empDetailsLabel.getText();
                table.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("كشف الحضور والغيابات - " + empName), new MessageFormat("صفحة {0}"));
            } catch (Exception ex) {
                UITheme.showThemedMessage(frame, "خطأ في الطباعة: " + ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        botActions.add(deleteBtn);
        botActions.add(exportBtn);
        botActions.add(printBtn);
        rightCard.add(botActions, BorderLayout.SOUTH);

        centerPanel.add(rightCard, BorderLayout.CENTER);
        frame.add(centerPanel, BorderLayout.CENTER);

        refreshEmployeeList();
    }

    public static String getSelectedEmpId() {
        String selected = (String) id_combo.getSelectedItem();
        if (selected == null || selected.isEmpty()) return "";
        if (selected.contains(" - ")) {
            return selected.split(" - ")[0].trim();
        }
        return selected.trim();
    }

    public static void refreshEmployeeList() {
        String prevSelected = getSelectedEmpId();
        id_combo.removeAllItems();

        String sql = "SELECT id_employee, nom, prenom FROM employee WHERE activ_emp = 1 ORDER BY id_employee";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String item = rs.getString("id_employee") + " - " + rs.getString("nom") + " " + rs.getString("prenom");
                id_combo.addItem(item);
                if (rs.getString("id_employee").equals(prevSelected)) {
                    id_combo.setSelectedItem(item);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading employees in WageFlowPanel: " + ex.getMessage());
        }

        if (id_combo.getItemCount() > 0) {
            update(true);
        }
    }

    private void loadSelectedEmployeeData() {
        String empId = getSelectedEmpId();
        if (empId.isEmpty()) return;

        String sql = "SELECT e.nom, e.prenom, e.post, e.salaire, p.path FROM employee e LEFT JOIN photo_path p ON e.id_employee = p.employee_id WHERE e.id_employee = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    empDetailsLabel.setText("الموظف: " + rs.getString("nom") + " " + rs.getString("prenom") + " (" + rs.getString("post") + ")");
                    String path = rs.getString("path");
                    EmployeePanel.imagePath = path != null ? path : "";
                }
            }
        } catch (Exception ex) {
            EmployeePanel.imagePath = "";
        }
        photoPanel.refresh();
    }

    public static void update(boolean showAll) {
        String empId = getSelectedEmpId();
        if (empId.isEmpty()) {
            model.setRowCount(0);
            a.setText("0 DA");
            daysCountLabel.setText("0 يوم");
            pay_combo.removeAllItems();
            return;
        }

        int dailySalary = 0;
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT salaire FROM employee WHERE id_employee = ?")) {
            pstmt.setString(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dailySalary = rs.getInt("salaire");
                }
            }
        } catch (Exception ignored) {}

        String sql = "SELECT absence_id, absence_date, state, paying_state, reason FROM absences WHERE id_employee = ? ORDER BY absence_date ASC, absence_id ASC";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                model.setRowCount(0);
                int totalUnpaidDays = 0;
                int totalUnpaidDue = 0;
                int sequentialNumber = 1;

                Date fromDate = date_text1 != null ? date_text1.getDate() : null;
                Date toDate = date_text2 != null ? date_text2.getDate() : null;
                String stateFilter = state_filter_combo != null ? (String) state_filter_combo.getSelectedItem() : "الكل";

                while (rs.next()) {
                    int absId = rs.getInt("absence_id");
                    java.sql.Date absDate = rs.getDate("absence_date");
                    String state = rs.getString("state");
                    String payingState = rs.getString("paying_state");
                    String reason = rs.getString("reason") != null ? rs.getString("reason") : "";

                    if ("حاضر".equals(state) && "غير مدفوع".equals(payingState)) {
                        totalUnpaidDays++;
                        totalUnpaidDue += dailySalary;
                    }

                    boolean match = true;
                    if (!showAll) {
                        if (fromDate != null && absDate.before(fromDate)) match = false;
                        if (toDate != null && absDate.after(toDate)) match = false;
                        if (!"الكل".equals(stateFilter)) {
                            if ("حاضر".equals(stateFilter) && !"حاضر".equals(state)) match = false;
                            if ("غائب".equals(stateFilter) && !"غائب".equals(state)) match = false;
                            if ("غير مدفوع".equals(stateFilter) && !"غير مدفوع".equals(payingState)) match = false;
                            if ("مدفوع".equals(stateFilter) && !"مدفوع".equals(payingState)) match = false;
                        }
                    }

                    if (match) {
                        Vector<String> row = new Vector<>();
                        row.add(String.valueOf(sequentialNumber++));
                        row.add(new SimpleDateFormat("yyyy-MM-dd").format(absDate));
                        row.add(state);
                        row.add(payingState);
                        row.add(reason);
                        row.add(String.valueOf(absId));
                        model.addRow(row);
                    }
                }

                a.setText(totalUnpaidDue + " DA");
                daysCountLabel.setText("الأيام غير المدفوعة: " + totalUnpaidDays + " يوم");

                pay_combo.removeAllItems();
                if (totalUnpaidDays > 0) {
                    for (int i = 1; i <= totalUnpaidDays; i++) {
                        pay_combo.addItem(i + " يوم (" + (i * dailySalary) + " DA)");
                    }
                    pay_combo.setSelectedIndex(pay_combo.getItemCount() - 1);
                } else {
                    pay_combo.addItem("لا توجد مستحقات معلقة");
                }
            }
        } catch (Exception ex) {
            System.err.println("Error querying absences in WageFlowPanel: " + ex.getMessage());
        }
    }

    private void deleteSelectedAbsence() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            UITheme.showThemedMessage(frame, "الرجاء اختيار السجل المراد حذفه من الجدول أولاً!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mRow = table.convertRowIndexToModel(selectedRow);
        String absId = String.valueOf(model.getValueAt(mRow, 5));
        String dateStr = String.valueOf(model.getValueAt(mRow, 1));
        String stateStr = String.valueOf(model.getValueAt(mRow, 2));

        int confirm = UITheme.showThemedConfirm(frame,
            "هل أنت متأكد من حذف سجل (" + stateStr + ") بتاريخ " + dateStr + " للموظف؟",
            "تأكيد حذف السجل", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DBManager.executeUpdate("DELETE FROM absences WHERE absence_id = ?", Integer.parseInt(absId));
            ActivityLogger.log("حذف تسجيل حضور/غياب", "قام بحذف سجل حضور (" + stateStr + ") بتاريخ " + dateStr + " للموظف (معرف: " + getSelectedEmpId() + ")");
            UITheme.showThemedMessage(frame, "تم حذف السجل بنجاح!", "تم الحذف", JOptionPane.INFORMATION_MESSAGE);
            update(true);
        }
    }

    private void executePayment() {
        String empId = getSelectedEmpId();
        if (empId.isEmpty()) return;

        int selectedIndex = pay_combo.getSelectedIndex();
        if (selectedIndex < 0 || pay_combo.getSelectedItem().toString().contains("لا توجد")) {
            UITheme.showThemedMessage(frame, "لا توجد أيام غير مدفوعة لتسويتها!", "تنبيه", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int daysToPay = selectedIndex + 1;
        int confirm = UITheme.showThemedConfirm(frame,
            "هل أنت متأكد من تسوية ودفع مستحقات " + daysToPay + " يوم للموظف؟",
            "تأكيد التسوية المالية", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String fetchSql = "SELECT absence_id FROM absences WHERE id_employee = ? AND state = 'حاضر' AND paying_state = 'غير مدفوع' ORDER BY absence_date ASC LIMIT ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(fetchSql)) {
            pstmt.setString(1, empId);
            pstmt.setInt(2, daysToPay);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int absId = rs.getInt("absence_id");
                    DBManager.executeUpdate("UPDATE absences SET paying_state = 'مدفوع' WHERE absence_id = ?", absId);
                }
            }
            ActivityLogger.log("تسوية رواتب", "قام بتسوية ودفع مستحقات " + daysToPay + " يوم للموظف (معرف: " + empId + ")");
            UITheme.showThemedMessage(frame, "تمت تسوية ودفع الأيام بنجاح!", "تم بنجاح", JOptionPane.INFORMATION_MESSAGE);
            update(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            UITheme.showThemedMessage(frame, "حدث خطأ أثناء التسوية: " + ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }
}
