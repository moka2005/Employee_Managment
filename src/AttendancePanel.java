import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class AttendancePanel {
    public JPanel frame;
    public static CardLayout cardLayout;
    public static JPanel cardPanel;

    public static DefaultTableModel model = new DefaultTableModel();
    public static JTable table;
    public static TableRowSorter<DefaultTableModel> sorter;

    private JTextField idField;
    private JTextField nameField;
    private JTextField causeField;
    private JDateChooser dateChooser;
    private JComboBox<String> stateCombo;
    private JComboBox<String> payingCombo;
    private JTextField searchField;
    private ProfilePhotoPanel photoPanel;

    public AttendancePanel(CardLayout cardLayout, JPanel cardPanel) {
        AttendancePanel.cardLayout = cardLayout;
        AttendancePanel.cardPanel = cardPanel;
        frame = new JPanel(new BorderLayout(10, 10));
        frame.setBackground(UITheme.getBgMain());
        frame.setBorder(new EmptyBorder(8, 10, 8, 10));

        buildUI();
    }

    private void buildUI() {
        // TOP Header Card
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout(10, 0));

        JLabel titleLabel = new JLabel("تسجيل الحضور والغياب اليومي", SwingConstants.RIGHT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.getTextPrimary());

        // Search Box
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBox.setOpaque(false);
        searchBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel searchIcon = new JLabel("تصفية الموظفين:", IconHelper.getIcon("search.png", 16, 16), SwingConstants.RIGHT);
        searchIcon.setFont(UITheme.FONT_BOLD);
        searchIcon.setForeground(UITheme.getTextPrimary());
        searchIcon.setHorizontalTextPosition(SwingConstants.LEFT);
        searchField = UITheme.createTextField(14);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
            private void applyFilter() {
                String text = searchField.getText().trim();
                if (sorter != null) {
                    if (text.isEmpty()) sorter.setRowFilter(null);
                    else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        searchBox.add(searchIcon);
        searchBox.add(searchField);

        topCard.add(titleLabel, BorderLayout.EAST);
        topCard.add(searchBox, BorderLayout.WEST);
        frame.add(topCard, BorderLayout.NORTH);

        // CENTER: Left Form + Right Employee Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Left Form Card
        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new BorderLayout(8, 10));
        formCard.setPreferredSize(new Dimension(310, 0));

        // Photo Preview
        photoPanel = new ProfilePhotoPanel();
        photoPanel.setPreferredSize(new Dimension(110, 110));
        formCard.add(photoPanel, BorderLayout.NORTH);

        // Inputs Grid
        JPanel formGrid = new JPanel(new GridLayout(6, 2, 6, 6));
        formGrid.setOpaque(false);
        formGrid.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        idField = UITheme.createTextField(10);
        idField.setEditable(false);

        nameField = UITheme.createTextField(10);
        nameField.setEditable(false);

        dateChooser = new JDateChooser(new Date());
        dateChooser.setDateFormatString("yyyy-MM-dd");
        UITheme.styleDateChooser(dateChooser);

        stateCombo = new JComboBox<>(new String[]{"حاضر", "غائب"});
        UITheme.styleComboBox(stateCombo);
        stateCombo.addActionListener(e -> {
            boolean isAbsent = "غائب".equals(stateCombo.getSelectedItem());
            causeField.setEnabled(isAbsent);
            if (!isAbsent) causeField.setText("");
        });

        payingCombo = new JComboBox<>(new String[]{"غير مدفوع", "مدفوع"});
        UITheme.styleComboBox(payingCombo);

        causeField = UITheme.createTextField(10);
        causeField.setEnabled(false);

        formGrid.add(UITheme.createFieldLabel("رقم الموظف:"));
        formGrid.add(idField);

        formGrid.add(UITheme.createFieldLabel("اسم الموظف:"));
        formGrid.add(nameField);

        formGrid.add(UITheme.createFieldLabel("تاريخ التسجيل:"));
        formGrid.add(dateChooser);

        formGrid.add(UITheme.createFieldLabel("حالة الحضور:"));
        formGrid.add(stateCombo);

        formGrid.add(UITheme.createFieldLabel("حالة الدفع:"));
        formGrid.add(payingCombo);

        formGrid.add(UITheme.createFieldLabel("سبب الغياب:"));
        formGrid.add(causeField);

        formCard.add(formGrid, BorderLayout.CENTER);

        // Submit Button
        JButton submitBtn = UITheme.createPrimaryButton("تسجيل الحضور / الغياب", IconHelper.getIcon("include.png", 18, 18));
        submitBtn.setPreferredSize(new Dimension(0, 44));
        submitBtn.addActionListener(e -> saveAttendance());
        formCard.add(submitBtn, BorderLayout.SOUTH);

        centerPanel.add(formCard, BorderLayout.WEST);

        // Right Table Card
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(10, 10));

        JLabel tableTitle = new JLabel("اختر موظفاً من قائمة العمال النشطين", SwingConstants.RIGHT);
        tableTitle.setFont(UITheme.FONT_SUBTITLE);
        tableTitle.setForeground(UITheme.getTextPrimary());
        tableCard.add(tableTitle, BorderLayout.NORTH);

        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        model.addColumn("رقم التعريف الوطني");
        model.addColumn("الإسم");
        model.addColumn("اللقب");
        model.addColumn("المنصب");

        table = new JTable(model);
        UITheme.styleTable(table);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int mRow = table.convertRowIndexToModel(row);
                    String empId = String.valueOf(model.getValueAt(mRow, 0));
                    String empName = String.valueOf(model.getValueAt(mRow, 1)) + " " + String.valueOf(model.getValueAt(mRow, 2));

                    idField.setText(empId);
                    nameField.setText(empName);

                    loadPhoto(empId);
                }
            }
        });

        JScrollPane scrollPane = UITheme.createScrollPane(table);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Table Bottom Actions
        JPanel botActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botActions.setOpaque(false);
        botActions.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton refreshBtn = UITheme.createSecondaryButton("تحديث القائمة", IconHelper.getIcon("empty.png", 16, 16));
        refreshBtn.addActionListener(e -> update());

        JButton exportBtn = UITheme.createButton("تصدير Excel", IconHelper.getIcon("excel.png", 16, 16), new Color(16, 185, 129), Color.WHITE);
        exportBtn.addActionListener(e -> ExcelExporter.exportTable(table, "قائمة_تسجيل_الحضور.xlsx", "الحضور والغياب"));

        JButton printBtn = UITheme.createButton("طباعة قائمة العمال", IconHelper.getIcon("print.png", 16, 16), new Color(99, 102, 241), Color.WHITE);
        printBtn.addActionListener(e -> {
            try {
                table.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("قائمة العمال لتسجيل الحضور"), new MessageFormat("صفحة {0}"));
            } catch (Exception ex) {
                UITheme.showThemedMessage(frame, "خطأ في الطباعة: " + ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        botActions.add(refreshBtn);
        botActions.add(exportBtn);
        botActions.add(printBtn);
        tableCard.add(botActions, BorderLayout.SOUTH);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        frame.add(centerPanel, BorderLayout.CENTER);

        update();
    }

    private void loadPhoto(String empId) {
        String sql = "SELECT path FROM photo_path WHERE employee_id = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EmployeePanel.imagePath = rs.getString("path");
                } else {
                    EmployeePanel.imagePath = "";
                }
            }
        } catch (Exception ex) {
            EmployeePanel.imagePath = "";
        }
        photoPanel.refresh();
    }

    public static void update() {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id_employee, nom, prenom, post FROM employee WHERE activ_emp = 1 ORDER BY id_employee");
             ResultSet rs = pstmt.executeQuery()) {

            model.setRowCount(0);
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id_employee"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("prenom"));
                row.add(rs.getString("post"));
                model.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Error updating absence list: " + e.getMessage());
        }
    }

    private void saveAttendance() {
        String empId = idField.getText().trim();
        if (empId.isEmpty()) {
            UITheme.showThemedMessage(frame, "الرجاء تحديد الموظف من القائمة أولاً!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dateChooser.getDate() == null) {
            UITheme.showThemedMessage(frame, "الرجاء تحديد تاريخ الحضور / الغياب!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String state = (String) stateCombo.getSelectedItem();
        String payingState = (String) payingCombo.getSelectedItem();
        String reason = causeField.getText().trim();
        String sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate());

        int confirm = UITheme.showThemedConfirm(frame,
            "هل أنت متأكد من تسجيل حالة (" + state + " - " + payingState + ") للموظف: " + nameField.getText() + " بتاريخ " + sqlDate + "؟",
            "تأكيد تسجيل الحضور", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String insertSql = "INSERT INTO absences (id_employee, absence_date, state, paying_state, reason) VALUES (?, ?, ?, ?, ?)";
        boolean success = DBManager.executeUpdate(insertSql, empId, sqlDate, state, payingState, reason);

        if (success) {
            ActivityLogger.log("تسجيل حضور/غياب", "قام بتسجيل حالة (" + state + " - " + payingState + ") للموظف (معرف: " + empId + ") بتاريخ " + sqlDate);
            UITheme.showThemedMessage(frame, "تم تسجيل الحضور/الغياب بنجاح!", "تم بنجاح", JOptionPane.INFORMATION_MESSAGE);
            causeField.setText("");
        }
    }
}
