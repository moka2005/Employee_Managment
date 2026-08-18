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

public class ExpensesPanel {
    public JPanel frame;
    public static CardLayout cardLayout;
    public static JPanel cardPanel;

    public static DefaultTableModel model = new DefaultTableModel();
    public static JTable table;
    public static TableRowSorter<DefaultTableModel> sorter;

    private JTextField nameField;
    private JTextField amountField;
    private JTextField priceField;
    private JTextField totalField;
    private JTextField respField;
    private static JDateChooser inputDateChooser;

    public static JDateChooser begin_text;
    public static JDateChooser end_text;
    private JTextField searchField;
    private static JLabel grandTotalLabel = new JLabel("0 DA");

    public ExpensesPanel(CardLayout cardLayout, JPanel cardPanel) {
        ExpensesPanel.cardLayout = cardLayout;
        ExpensesPanel.cardPanel = cardPanel;
        frame = new JPanel(new BorderLayout(10, 10));
        frame.setBackground(UITheme.getBgMain());
        frame.setBorder(new EmptyBorder(8, 10, 8, 10));

        buildUI();
    }

    private void buildUI() {
        // TOP Header Card
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout(10, 0));

        JLabel titleLabel = new JLabel("إدارة وتتبع المصاريف والمشتريات", SwingConstants.RIGHT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.getTextPrimary());

        // Search Box in Header
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBox.setOpaque(false);
        searchBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel searchIcon = new JLabel("بحث سريع:", IconHelper.getIcon("search.png", 16, 16), SwingConstants.RIGHT);
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

        // CENTER: Left Form + Right Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Left Form Card
        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new BorderLayout(8, 10));
        formCard.setPreferredSize(new Dimension(310, 0));

        JLabel formTitle = new JLabel("إضافة مصروف جديد", SwingConstants.RIGHT);
        formTitle.setFont(UITheme.FONT_SUBTITLE);
        formTitle.setForeground(UITheme.PRIMARY);
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridLayout(6, 2, 6, 6));
        formGrid.setOpaque(false);
        formGrid.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        nameField = UITheme.createTextField(10);
        amountField = UITheme.createTextField(10);
        priceField = UITheme.createTextField(10);
        totalField = UITheme.createTextField(10);
        totalField.setEditable(false);
        respField = UITheme.createTextField(10);
        respField.setText(SessionManager.getUsername());

        inputDateChooser = new JDateChooser(new Date());
        inputDateChooser.setDateFormatString("yyyy-MM-dd");
        UITheme.styleDateChooser(inputDateChooser);

        // Auto calculate total
        DocumentListener calcListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculate(); }
            @Override public void removeUpdate(DocumentEvent e) { calculate(); }
            @Override public void changedUpdate(DocumentEvent e) { calculate(); }
            private void calculate() {
                try {
                    double amt = Double.parseDouble(amountField.getText().trim());
                    double prc = Double.parseDouble(priceField.getText().trim());
                    totalField.setText(String.valueOf((int) (amt * prc)));
                } catch (Exception ex) {
                    totalField.setText("");
                }
            }
        };
        amountField.getDocument().addDocumentListener(calcListener);
        priceField.getDocument().addDocumentListener(calcListener);

        formGrid.add(UITheme.createFieldLabel("اسم المادة / السلعة:"));
        formGrid.add(nameField);

        formGrid.add(UITheme.createFieldLabel("الكمية:"));
        formGrid.add(amountField);

        formGrid.add(UITheme.createFieldLabel("سعر الوحدة (DA):"));
        formGrid.add(priceField);

        formGrid.add(UITheme.createFieldLabel("المجموع الإجمالي (DA):"));
        formGrid.add(totalField);

        formGrid.add(UITheme.createFieldLabel("تاريخ الإدخال:"));
        formGrid.add(inputDateChooser);

        formGrid.add(UITheme.createFieldLabel("اسم المسؤول:"));
        formGrid.add(respField);

        formCard.add(formGrid, BorderLayout.CENTER);

        JButton addBtn = UITheme.createPrimaryButton("حفظ المصروف", IconHelper.getIcon("include.png", 18, 18));
        addBtn.setPreferredSize(new Dimension(0, 44));
        addBtn.addActionListener(e -> addExpense());
        formCard.add(addBtn, BorderLayout.SOUTH);

        centerPanel.add(formCard, BorderLayout.WEST);

        // Right Table Card
        JPanel rightCard = UITheme.createCard();
        rightCard.setLayout(new BorderLayout(10, 12));

        // Date Filtering Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        filterBar.setOpaque(false);
        filterBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        begin_text = new JDateChooser();
        begin_text.setPreferredSize(new Dimension(145, 38));
        begin_text.setDateFormatString("yyyy-MM-dd");
        UITheme.styleDateChooser(begin_text);

        end_text = new JDateChooser(new Date());
        end_text.setPreferredSize(new Dimension(145, 38));
        end_text.setDateFormatString("yyyy-MM-dd");
        UITheme.styleDateChooser(end_text);

        JButton filterBtn = UITheme.createPrimaryButton("تصفية", IconHelper.getIcon("filter.png", 18, 18));
        filterBtn.setPreferredSize(new Dimension(120, 38));
        filterBtn.addActionListener(e -> update(false));

        JButton resetBtn = UITheme.createSecondaryButton("عرض الكل", IconHelper.getIcon("empty.png", 18, 18));
        resetBtn.setPreferredSize(new Dimension(130, 38));
        resetBtn.addActionListener(e -> {
            begin_text.setDate(null);
            end_text.setDate(new Date());
            update(true);
        });

        filterBar.add(UITheme.createFieldLabel("من تاريخ:"));
        filterBar.add(begin_text);
        filterBar.add(UITheme.createFieldLabel("إلى تاريخ:"));
        filterBar.add(end_text);
        filterBar.add(filterBtn);
        filterBar.add(resetBtn);

        rightCard.add(filterBar, BorderLayout.NORTH);

        // Table Setup
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        model.addColumn("رقم");
        model.addColumn("اسم المادة");
        model.addColumn("الكمية");
        model.addColumn("السعر الفردي");
        model.addColumn("المجموع");
        model.addColumn("التاريخ");
        model.addColumn("المسؤول");
        model.addColumn("معرف المنتج");

        table = new JTable(model);
        UITheme.styleTable(table);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.getColumnModel().getColumn(7).setMinWidth(0);
        table.getColumnModel().getColumn(7).setMaxWidth(0);
        table.getColumnModel().getColumn(7).setWidth(0);

        JScrollPane scrollPane = UITheme.createScrollPane(table);
        rightCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Summary & Actions
        JPanel botPanel = new JPanel(new BorderLayout(10, 0));
        botPanel.setOpaque(false);

        JPanel totalBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        totalBox.setOpaque(false);
        totalBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel grandTotalTitle = new JLabel("المجموع الإجمالي للمصاريف:");
        grandTotalTitle.setFont(UITheme.FONT_BOLD);
        grandTotalTitle.setForeground(UITheme.getTextPrimary());
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        grandTotalLabel.setForeground(UITheme.DANGER);

        totalBox.add(grandTotalTitle);
        totalBox.add(grandTotalLabel);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionBtns.setOpaque(false);

        JButton deleteBtn = UITheme.createDangerButton("حذف المصروف المحدد", IconHelper.getIcon("delete.png", 16, 16));
        deleteBtn.addActionListener(e -> deleteSelectedExpense());

        JButton exportBtn = UITheme.createButton("تصدير Excel", IconHelper.getIcon("excel.png", 16, 16), new Color(16, 185, 129), Color.WHITE);
        exportBtn.addActionListener(e -> ExcelExporter.exportTable(table, "تقرير_المصاريف.xlsx", "المصاريف"));

        JButton printBtn = UITheme.createButton("طباعة التقرير", IconHelper.getIcon("print.png", 16, 16), new Color(99, 102, 241), Color.WHITE);
        printBtn.addActionListener(e -> {
            try {
                table.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("تقرير المصاريف والمشتريات"), new MessageFormat("صفحة {0}"));
            } catch (Exception ex) {
                UITheme.showThemedMessage(frame, "خطأ في الطباعة: " + ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        actionBtns.add(deleteBtn);
        actionBtns.add(exportBtn);
        actionBtns.add(printBtn);

        botPanel.add(totalBox, BorderLayout.EAST);
        botPanel.add(actionBtns, BorderLayout.WEST);

        rightCard.add(botPanel, BorderLayout.SOUTH);

        centerPanel.add(rightCard, BorderLayout.CENTER);
        frame.add(centerPanel, BorderLayout.CENTER);

        update(true);
    }

    public static void update(boolean showAll) {
        String sql = "SELECT product_id, num, product_name, product_amount, product_price, total, input_date, name_expenses FROM expense ORDER BY input_date ASC, product_id ASC";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            model.setRowCount(0);
            int grandTotal = 0;
            int sequentialNumber = 1;

            Date fromDate = begin_text != null ? begin_text.getDate() : null;
            Date toDate = end_text != null ? end_text.getDate() : null;

            while (rs.next()) {
                String inDateStr = rs.getString("input_date");
                if (inDateStr == null || inDateStr.isEmpty()) continue;
                boolean match = true;
                if (!showAll) {
                    try {
                        Date inDate = new SimpleDateFormat("yyyy-MM-dd").parse(inDateStr);
                        if (fromDate != null && inDate.before(fromDate)) match = false;
                        if (toDate != null && inDate.after(toDate)) match = false;
                    } catch (Exception ignored) {}
                }

                if (match) {
                    Vector<String> row = new Vector<>();
                    row.add(String.valueOf(sequentialNumber++));
                    row.add(rs.getString("product_name"));
                    row.add(rs.getString("product_amount"));
                    row.add(rs.getString("product_price") + " DA");
                    row.add(rs.getString("total") + " DA");
                    row.add(inDateStr);
                    row.add(rs.getString("name_expenses"));
                    row.add(rs.getString("product_id"));
                    model.addRow(row);

                    try {
                        grandTotal += Integer.parseInt(rs.getString("total").replaceAll("[^0-9]", ""));
                    } catch (Exception ignored) {}
                }
            }
            grandTotalLabel.setText(grandTotal + " DA");

        } catch (Exception e) {
            System.err.println("Error loading expenses: " + e.getMessage());
        }
    }

    private void addExpense() {
        String name = nameField.getText().trim();
        String amount = amountField.getText().trim();
        String price = priceField.getText().trim();
        String total = totalField.getText().trim();
        String resp = respField.getText().trim();

        if (name.isEmpty() || amount.isEmpty() || price.isEmpty() || resp.isEmpty() || inputDateChooser.getDate() == null) {
            UITheme.showThemedMessage(frame, "الرجاء ملء كافة حقول المصروف!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!amount.matches("\\d+(\\.\\d+)?") || !price.matches("\\d+(\\.\\d+)?")) {
            UITheme.showThemedMessage(frame, "الرجاء إدخال أرقام صحيحة للكمية والسعر!", "خطأ في الإدخال", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(inputDateChooser.getDate());

        String insertSql = "INSERT INTO expense (product_name, product_amount, product_price, total, input_date, name_expenses) VALUES (?, ?, ?, ?, ?, ?)";
        boolean success = DBManager.executeUpdate(insertSql, name, amount, price, total, sqlDate, resp);

        if (success) {
            ActivityLogger.log("إضافة مصروف", "قام بإضافة مصروف: " + name + " (المجموع: " + total + " DA) للمسؤول: " + resp);
            nameField.setText("");
            amountField.setText("");
            priceField.setText("");
            totalField.setText("");
            respField.setText(SessionManager.getUsername());
            update(true);
            UITheme.showThemedMessage(frame, "تمت إضافة المصروف بنجاح!", "تم بنجاح", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            UITheme.showThemedMessage(frame, "الرجاء اختيار المصروف المراد حذفه من الجدول!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mRow = table.convertRowIndexToModel(selectedRow);
        String prodId = String.valueOf(model.getValueAt(mRow, 7));
        String prodName = String.valueOf(model.getValueAt(mRow, 1));
        String prodTotal = String.valueOf(model.getValueAt(mRow, 4));

        int confirm = UITheme.showThemedConfirm(frame, "هل أنت متأكد من حذف بند المصروف: (" + prodName + ")؟", "تأكيد الحذف", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DBManager.executeUpdate("DELETE FROM expense WHERE product_id = ?", Integer.parseInt(prodId));
            ActivityLogger.log("حذف مصروف", "قام بحذف بند المصروف: " + prodName + " (القيمة: " + prodTotal + ") رقم: " + prodId);
            update(true);
            UITheme.showThemedMessage(frame, "تم حذف المصروف بنجاح!", "تم الحذف", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
