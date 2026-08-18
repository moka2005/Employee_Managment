import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

class ProfilePhotoPanel extends JPanel {
    public ProfilePhotoPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h) - 16;
        int x = (w - size) / 2;
        int y = (h - size) / 2;

        Image img = null;
        if (EmployeePanel.imagePath != null && !EmployeePanel.imagePath.trim().isEmpty()) {
            File f = new File(EmployeePanel.imagePath);
            if (f.exists()) {
                img = new ImageIcon(EmployeePanel.imagePath).getImage();
            }
        }

        // Fill background of the photo card with clean theme secondary card color
        g2.setColor(UITheme.getBgCardSecondary());
        g2.fill(new RoundRectangle2D.Float(x, y, size, size, 16, 16));

        if (img != null) {
            Shape clip = new RoundRectangle2D.Float(x, y, size, size, 16, 16);
            g2.setClip(clip);
            g2.drawImage(img, x, y, size, size, this);
            g2.setClip(null);
        } else {
            // Use original profil.png as placeholder avatar
            ImageIcon def = IconHelper.getIcon("profil.png", size, size);
            if (def != null && def.getImage() != null) {
                Shape clip = new RoundRectangle2D.Float(x, y, size, size, 16, 16);
                g2.setClip(clip);
                g2.drawImage(def.getImage(), x, y, size, size, this);
                g2.setClip(null);
            }
        }

        g2.setColor(UITheme.getBorderColor());
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(x, y, size, size, 16, 16));
        g2.dispose();
    }

    public void refresh() {
        repaint();
    }
}

public class EmployeePanel {
    public static CardLayout cardLayout;
    public static JPanel cardPanel;
    public JPanel frame;

    public static DefaultTableModel model = new DefaultTableModel();
    public static JTable table;
    public static TableRowSorter<DefaultTableModel> sorter;
    public static String imagePath = "";
    public static ProfilePhotoPanel profil_icon = new ProfilePhotoPanel();
    public static JRadioButton radio_buttom[] = new JRadioButton[3];
    public static ButtonGroup g;

    // Form fields
    private JTextField idField;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField phoneField;
    private JComboBox<String> postCombo;
    private JComboBox<String> salaireCombo;
    private JComboBox<String> etatCombo;
    private JDateChooser birthDateChooser;
    private JDateChooser joinDateChooser;
    private JTextField searchField;

    public EmployeePanel(CardLayout a, JPanel b) {
        cardLayout = a;
        cardPanel = b;
        frame = new JPanel(new BorderLayout(10, 10));
        frame.setBackground(UITheme.getBgMain());
        frame.setBorder(new EmptyBorder(8, 10, 8, 10));

        buildUI();
    }

    private void buildUI() {
        // TOP Header Card
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout(10, 0));

        JLabel titleLabel = new JLabel("إدارة العمال والموظفين", SwingConstants.RIGHT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.getTextPrimary());

        // Search Bar in Top Card
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBox.setOpaque(false);
        searchBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel searchIcon = new JLabel("بحث سريع:", IconHelper.getIcon("search.png", 16, 16), SwingConstants.RIGHT);
        searchIcon.setFont(UITheme.FONT_BOLD);
        searchIcon.setForeground(UITheme.getTextPrimary());
        searchIcon.setHorizontalTextPosition(SwingConstants.LEFT);
        searchField = UITheme.createTextField(14);
        searchField.setToolTipText("ابحث بالاسم، اللقب، رقم الهاتف، أو رقم التعريف");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
            private void applyFilter() {
                String text = searchField.getText().trim();
                if (sorter != null) {
                    if (text.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    }
                }
            }
        });

        searchBox.add(searchIcon);
        searchBox.add(searchField);

        topCard.add(titleLabel, BorderLayout.EAST);
        topCard.add(searchBox, BorderLayout.WEST);
        frame.add(topCard, BorderLayout.NORTH);

        // CENTER: Left Form + Right/Center Table
        JPanel centerContent = new JPanel(new BorderLayout(10, 10));
        centerContent.setOpaque(false);

        // Left Panel (Form + Photo)
        JPanel leftFormCard = UITheme.createCard();
        leftFormCard.setLayout(new BorderLayout(8, 8));
        leftFormCard.setPreferredSize(new Dimension(310, 0));

        // Photo Preview on top of Form
        JPanel photoContainer = new JPanel(new BorderLayout(0, 6));
        photoContainer.setOpaque(false);
        profil_icon.setPreferredSize(new Dimension(110, 110));
        photoContainer.add(profil_icon, BorderLayout.CENTER);

        JButton uploadBtn = UITheme.createSecondaryButton("رفع صورة الموظف", IconHelper.getIcon("addicon.png", 14, 14));
        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("صور", "jpg", "jpeg", "png", "gif"));
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                imagePath = chooser.getSelectedFile().getAbsolutePath();
                profil_icon.refresh();
            }
        });
        photoContainer.add(uploadBtn, BorderLayout.SOUTH);
        leftFormCard.add(photoContainer, BorderLayout.NORTH);

        // Form Fields Grid
        JPanel formGrid = new JPanel(new GridLayout(8, 2, 6, 4));
        formGrid.setOpaque(false);
        formGrid.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        idField = UITheme.createTextField(10);
        nomField = UITheme.createTextField(10);
        prenomField = UITheme.createTextField(10);
        phoneField = UITheme.createTextField(10);

        birthDateChooser = new JDateChooser();
        birthDateChooser.setDateFormatString("yyyy-MM-dd");
        UITheme.styleDateChooser(birthDateChooser);

        joinDateChooser = new JDateChooser(new Date());
        joinDateChooser.setDateFormatString("yyyy-MM-dd");
        UITheme.styleDateChooser(joinDateChooser);

        postCombo = new JComboBox<>(new String[]{"عامل", "سائق", "ميكانيكي", "إداري", "أخرى"});
        UITheme.styleComboBox(postCombo);

        salaireCombo = new JComboBox<>(new String[]{"1000", "1200", "1500", "1800", "2000", "2500", "3000", "4000"});
        UITheme.styleComboBox(salaireCombo);

        etatCombo = new JComboBox<>(new String[]{"أعزب", "متزوج"});
        UITheme.styleComboBox(etatCombo);

        formGrid.add(UITheme.createFieldLabel("رقم التعريف (9 أرقام):"));
        formGrid.add(idField);

        formGrid.add(UITheme.createFieldLabel("الإسم:"));
        formGrid.add(nomField);

        formGrid.add(UITheme.createFieldLabel("اللقب:"));
        formGrid.add(prenomField);

        formGrid.add(UITheme.createFieldLabel("رقم الهاتف (10 أرقام):"));
        formGrid.add(phoneField);

        formGrid.add(UITheme.createFieldLabel("تاريخ الميلاد:"));
        formGrid.add(birthDateChooser);

        formGrid.add(UITheme.createFieldLabel("الرتبة / المنصب:"));
        formGrid.add(postCombo);

        formGrid.add(UITheme.createFieldLabel("الراتب اليومي (DA):"));
        formGrid.add(salaireCombo);

        formGrid.add(UITheme.createFieldLabel("الحالة العائلية:"));
        formGrid.add(etatCombo);

        leftFormCard.add(formGrid, BorderLayout.CENTER);
        centerContent.add(leftFormCard, BorderLayout.WEST);

        // Right / Center Panel: Table + Radio Filter + Action Buttons
        JPanel rightTablePanel = new JPanel(new BorderLayout(8, 8));
        rightTablePanel.setOpaque(false);

        // Radio Buttons Filter Card
        JPanel radioCard = UITheme.createCard();
        radioCard.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        radioCard.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String[] radioTitles = {"كل الموظفين", "الموظفين الحاليين", "الموظفين السابقين"};
        g = new ButtonGroup();
        for (int i = 0; i < 3; i++) {
            radio_buttom[i] = UITheme.createRadioButton(radioTitles[i]);
            g.add(radio_buttom[i]);
            radioCard.add(radio_buttom[i]);
            radio_buttom[i].addActionListener(e -> Action_radio(radio_buttom, g));
        }
        radio_buttom[1].setSelected(true);
        rightTablePanel.add(radioCard, BorderLayout.NORTH);

        // Table Setup
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        model.addColumn("رقم التعريف الوطني");
        model.addColumn("الإسم");
        model.addColumn("اللقب");
        model.addColumn("تاريخ الميلاد");
        model.addColumn("تاريخ الإلتحاق");
        model.addColumn("تاريخ الخروج");
        model.addColumn("الهاتف");
        model.addColumn("المنصب");
        model.addColumn("الراتب اليومي");
        model.addColumn("الحالة");

        table = new JTable(model);
        UITheme.styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Set optimized column widths so headers never get truncated
        int[] colWidths = {105, 80, 80, 85, 85, 85, 90, 70, 80, 65};
        for (int i = 0; i < colWidths.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Table Row Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    idField.setText(String.valueOf(model.getValueAt(modelRow, 0)));
                    nomField.setText(String.valueOf(model.getValueAt(modelRow, 1)));
                    prenomField.setText(String.valueOf(model.getValueAt(modelRow, 2)));

                    try {
                        String bDate = String.valueOf(model.getValueAt(modelRow, 3));
                        if (bDate != null && !bDate.equals("null") && !bDate.isEmpty()) {
                            birthDateChooser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(bDate));
                        }
                    } catch (Exception ignored) {}

                    phoneField.setText(String.valueOf(model.getValueAt(modelRow, 6)));
                    postCombo.setSelectedItem(String.valueOf(model.getValueAt(modelRow, 7)));

                    String sal = String.valueOf(model.getValueAt(modelRow, 8)).replace("DA", "").trim();
                    salaireCombo.setSelectedItem(sal);
                    etatCombo.setSelectedItem(String.valueOf(model.getValueAt(modelRow, 9)));

                    loadEmployeePhoto(String.valueOf(model.getValueAt(modelRow, 0)));
                }
            }
        });

        JScrollPane scrollPane = UITheme.createScrollPane(table);
        rightTablePanel.add(scrollPane, BorderLayout.CENTER);

        // Action Buttons Bar - Structured into 2 clean rows that never get clipped
        JPanel actionCard = UITheme.createCard();
        actionCard.setLayout(new GridLayout(2, 1, 0, 4));
        actionCard.setBorder(new EmptyBorder(6, 8, 6, 8));

        JButton addBtn = UITheme.createPrimaryButton("موظف جديد", IconHelper.getIcon("include.png", 14, 14));
        JButton editBtn = UITheme.createSuccessButton("تعديل وحفظ", IconHelper.getIcon("edit.png", 14, 14));
        JButton reactivateBtn = UITheme.createButton("إعادة تفعيل الموظف", IconHelper.getIcon("check.png", 14, 14), new Color(16, 185, 129), Color.WHITE);
        JButton deleteBtn = UITheme.createDangerButton("إنهاء خدمة / حذف", IconHelper.getIcon("delete.png", 14, 14));
        JButton clearBtn = UITheme.createSecondaryButton("إفراغ الخانات", IconHelper.getIcon("empty.png", 14, 14));
        JButton exportBtn = UITheme.createButton("تصدير Excel", IconHelper.getIcon("excel.png", 14, 14), new Color(16, 185, 129), Color.WHITE);
        JButton printBtn = UITheme.createButton("طباعة القائمة", IconHelper.getIcon("print.png", 14, 14), new Color(99, 102, 241), Color.WHITE);

        addBtn.addActionListener(e -> addEmployee());
        editBtn.addActionListener(e -> updateEmployee());
        reactivateBtn.addActionListener(e -> reactivateSelectedEmployee());
        deleteBtn.addActionListener(e -> deleteOrArchiveEmployee());
        clearBtn.addActionListener(e -> clearFields());
        exportBtn.addActionListener(e -> ExcelExporter.exportTable(table, "قائمة_العمال.xlsx", "العمال"));
        printBtn.addActionListener(e -> printTable());

        // Row 1: Core CRUD Operations
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        row1.setOpaque(false);
        row1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        row1.add(addBtn);
        row1.add(editBtn);
        row1.add(reactivateBtn);
        row1.add(deleteBtn);

        // Row 2: Secondary / Tool Actions
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        row2.setOpaque(false);
        row2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        row2.add(clearBtn);
        row2.add(exportBtn);
        row2.add(printBtn);

        actionCard.add(row1);
        actionCard.add(row2);

        rightTablePanel.add(actionCard, BorderLayout.SOUTH);
        centerContent.add(rightTablePanel, BorderLayout.CENTER);

        frame.add(centerContent, BorderLayout.CENTER);

        Action_radio(radio_buttom, g);
    }

    private static void loadEmployeePhoto(String empId) {
        String sql = "SELECT path FROM photo_path WHERE employee_id = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    imagePath = rs.getString("path");
                } else {
                    imagePath = "";
                }
            }
        } catch (Exception ex) {
            imagePath = "";
        }
        profil_icon.refresh();
    }

    public static void Action_radio(JRadioButton b[], ButtonGroup g) {
        String selectSQL;
        if (b[0] != null && b[0].isSelected()) {
            selectSQL = "SELECT * FROM employee ORDER BY id_employee";
        } else if (b[2] != null && b[2].isSelected()) {
            selectSQL = "SELECT * FROM employee WHERE activ_emp = 0 ORDER BY id_employee";
        } else {
            selectSQL = "SELECT * FROM employee WHERE activ_emp = 1 ORDER BY id_employee";
        }

        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(selectSQL); ResultSet rs = pstmt.executeQuery()) {
            model.setRowCount(0);
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id_employee"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("prenom"));
                row.add(rs.getString("date_naissance"));
                row.add(rs.getString("date_embauche"));
                row.add(rs.getString("date_depart"));
                row.add(rs.getString("telephone"));
                row.add(rs.getString("post"));
                row.add(rs.getString("salaire") + " DA");
                row.add(rs.getString("etat"));
                model.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }
    }

    /** Returns true if the given employee ID exists in the database. */
    private boolean employeeExistsInDB(String id) {
        String sql = "SELECT 1 FROM employee WHERE id_employee = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void addEmployee() {
        String id = idField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String phone = phoneField.getText().trim();
        String post = (String) postCombo.getSelectedItem();
        String salaireStr = (String) salaireCombo.getSelectedItem();
        String etat = (String) etatCombo.getSelectedItem();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty() || phone.isEmpty()) {
            UITheme.showThemedMessage(frame, "الرجاء ملء كافة الحقول الإلزامية!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (id.length() != 9 || !id.matches("\\d+")) {
            UITheme.showThemedMessage(frame, "رقم التعريف الوطني يجب أن يتكون من 9 أرقام!", "خطأ في الإدخال", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.length() != 10 || !phone.matches("\\d+")) {
            UITheme.showThemedMessage(frame, "رقم الهاتف يجب أن يتكون من 10 أرقام!", "خطأ في الإدخال", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int salaire = 1000;
        try {
            salaire = Integer.parseInt(salaireStr.replaceAll("[^0-9]", ""));
        } catch (Exception ignored) {}

        String birthDate = birthDateChooser.getDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(birthDateChooser.getDate()) : null;
        String joinDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Check if ID already exists
        String checkSql = "SELECT nom, prenom, activ_emp FROM employee WHERE id_employee = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int active = rs.getInt("activ_emp");
                    String empName = rs.getString("nom") + " " + rs.getString("prenom");
                    if (active == 0) {
                        int c = UITheme.showThemedConfirm(frame, "الموظف (" + empName + ") موجود في الأرشيف (سابق).\nهل تريد إعادة تفعيله وتحديث بياناته؟", "استرجاع موظف سابق", JOptionPane.YES_NO_OPTION);
                        if (c == JOptionPane.YES_OPTION) {
                            performReactivation(id, nom, prenom, birthDate, phone, post, salaire, etat, joinDate);
                        }
                    } else {
                        UITheme.showThemedMessage(frame, "رقم التعريف الوطني (" + id + ") مستعمل بالفعل للموظف الحالي: " + empName + "!", "تنبيه معرف مكرر", JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Check if Phone already exists for another employee
        String checkPhoneSql = "SELECT id_employee, nom, prenom, activ_emp FROM employee WHERE telephone = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkPhoneSql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String existingId = rs.getString("id_employee");
                    String empName = rs.getString("nom") + " " + rs.getString("prenom");
                    int active = rs.getInt("activ_emp");
                    if (active == 1) {
                        UITheme.showThemedMessage(frame, "رقم الهاتف (" + phone + ") مسجل بالفعل للموظف الحالي:\n" + empName + " (رقم التعريف: " + existingId + ")\nالرجاء إدخال رقم هاتف آخر.", "تنبيه رقم الهاتف مكرر", JOptionPane.WARNING_MESSAGE);
                    } else {
                        int c = UITheme.showThemedConfirm(frame, "رقم الهاتف (" + phone + ") مسجل لموظف سابق في الأرشيف:\n" + empName + " (رقم التعريف: " + existingId + ").\nهل تريد استرجاع وإعادة تفعيل هذا الموظف؟", "استرجاع موظف", JOptionPane.YES_NO_OPTION);
                        if (c == JOptionPane.YES_OPTION) {
                            idField.setText(existingId);
                            performReactivation(existingId, nom, prenom, birthDate, phone, post, salaire, etat, joinDate);
                        }
                    }
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String insertSql = "INSERT INTO employee (id_employee, nom, prenom, date_naissance, date_embauche, date_depart, telephone, post, salaire, etat, activ_emp) " +
            "VALUES (?, ?, ?, ?, ?, '2030-12-12', ?, ?, ?, ?, 1)";

        boolean success = DBManager.executeUpdate(insertSql, id, nom, prenom, birthDate, joinDate, phone, post, salaire, etat);
        if (success) {
            if (!imagePath.isEmpty()) {
                DBManager.executeUpdate("DELETE FROM photo_path WHERE employee_id = ?", id);
                DBManager.executeUpdate("INSERT INTO photo_path (employee_id, path) VALUES (?, ?)", id, imagePath);
            }
            DBManager.executeUpdate("INSERT INTO date_emp (id_employee, date_embauche, \"date_départ\") VALUES (?, ?, '2030-12-12')", id, joinDate);

            Action_radio(radio_buttom, g);
            clearFields();
            ActivityLogger.log("إضافة موظف", "قام بإضافة موظف جديد: " + nom + " " + prenom + " (معرف: " + id + ")");
            UITheme.showThemedMessage(frame, "تمت إضافة الموظف الجديد بنجاح!", "تم بنجاح", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateEmployee() {
        String id = idField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String phone = phoneField.getText().trim();
        String post = (String) postCombo.getSelectedItem();
        String salaireStr = (String) salaireCombo.getSelectedItem();
        String etat = (String) etatCombo.getSelectedItem();

        if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty() || phone.isEmpty()) {
            UITheme.showThemedMessage(frame, "الرجاء تحديد الموظف وملء كافة الحقول!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ── NEW: verify employee is actually saved in DB ──────────────────
        if (!employeeExistsInDB(id)) {
            UITheme.showThemedMessage(frame,
                "الموظف غير موجود في قاعدة البيانات!\nالرجاء إضافته أولاً بالضغط على زر \"موظف جديد\".",
                "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.length() != 10 || !phone.matches("\\d+")) {
            UITheme.showThemedMessage(frame, "رقم الهاتف يجب أن يتكون من 10 أرقام!", "خطأ في الإدخال", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int salaire = 1000;
        try {
            salaire = Integer.parseInt(salaireStr.replaceAll("[^0-9]", ""));
        } catch (Exception ignored) {}

        String birthDate = birthDateChooser.getDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(birthDateChooser.getDate()) : null;

        // Check if phone belongs to another employee
        String checkPhoneSql = "SELECT nom, prenom FROM employee WHERE telephone = ? AND id_employee != ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkPhoneSql)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UITheme.showThemedMessage(frame, "رقم الهاتف (" + phone + ") مستخدم بالفعل لموظف آخر (" + rs.getString("nom") + " " + rs.getString("prenom") + ")!", "خطأ في الهاتف", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Check if employee is inactive, ask to reactivate
        String checkActSql = "SELECT activ_emp FROM employee WHERE id_employee = ?";
        int currentActive = 1;
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkActSql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    currentActive = rs.getInt("activ_emp");
                }
            }
        } catch (Exception ignored) {}

        if (currentActive == 0) {
            int c = UITheme.showThemedConfirm(frame, "هذا الموظف غير نشط حالياً (في الأرشيف).\nهل تريد حفظ التعديلات وإعادة تفعيله كموظف حالي أيضاً؟", "تأكيد إعادة التفعيل", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                String joinDateSql = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                performReactivation(id, nom, prenom, birthDate, phone, post, salaire, etat, joinDateSql);
                return;
            }
        }

        String updateSql = "UPDATE employee SET nom=?, prenom=?, date_naissance=?, telephone=?, post=?, salaire=?, etat=? WHERE id_employee=?";
        boolean success = DBManager.executeUpdate(updateSql, nom, prenom, birthDate, phone, post, salaire, etat, id);
        if (success) {
            if (!imagePath.isEmpty()) {
                DBManager.executeUpdate("DELETE FROM photo_path WHERE employee_id = ?", id);
                DBManager.executeUpdate("INSERT INTO photo_path (employee_id, path) VALUES (?, ?)", id, imagePath);
            }
            Action_radio(radio_buttom, g);
            ActivityLogger.log("تعديل موظف", "قام بتعديل بيانات الموظف: " + nom + " " + prenom + " (معرف: " + id + ")");
            UITheme.showThemedMessage(frame, "تم حفظ وتحديث بيانات الموظف بنجاح!", "تم التحديث", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void reactivateSelectedEmployee() {
        String id = idField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String phone = phoneField.getText().trim();
        String post = (String) postCombo.getSelectedItem();
        String salaireStr = (String) salaireCombo.getSelectedItem();
        String etat = (String) etatCombo.getSelectedItem();

        if (id.isEmpty()) {
            UITheme.showThemedMessage(frame, "الرجاء اختيار الموظف المراد إعادة تفعيله من الجدول أولاً!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ── verify employee is actually saved in DB ───────────────────────
        if (!employeeExistsInDB(id)) {
            UITheme.showThemedMessage(frame,
                "الموظف غير موجود في قاعدة البيانات!\nالرجاء إضافته أولاً بالضغط على زر \"موظف جديد\".",
                "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int salaire = 1000;
        try {
            salaire = Integer.parseInt(salaireStr.replaceAll("[^0-9]", ""));
        } catch (Exception ignored) {}

        String birthDate = birthDateChooser.getDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(birthDateChooser.getDate()) : null;
        String joinDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Check if phone belongs to another active employee
        String checkPhoneSql = "SELECT nom, prenom FROM employee WHERE telephone = ? AND id_employee != ? AND activ_emp = 1";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkPhoneSql)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UITheme.showThemedMessage(frame, "رقم الهاتف (" + phone + ") مستخدم بالفعل لموظف نشط آخر (" + rs.getString("nom") + " " + rs.getString("prenom") + ")!\nيرجى تعديل رقم الهاتف قبل إعادة التفعيل.", "تنبيه", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int confirm = UITheme.showThemedConfirm(frame, "هل أنت متأكد من إعادة تفعيل الموظف: " + nom + " " + prenom + " (معرف: " + id + ")؟", "تأكيد إعادة التفعيل", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            performReactivation(id, nom, prenom, birthDate, phone, post, salaire, etat, joinDate);
        }
    }

    private void performReactivation(String id, String nom, String prenom, String birthDate, String phone, String post, int salaire, String etat, String joinDate) {
        String reactivateSql = "UPDATE employee SET activ_emp = 1, nom=?, prenom=?, date_naissance=?, telephone=?, post=?, salaire=?, etat=?, date_embauche=?, date_depart='2030-12-12' WHERE id_employee = ?";
        boolean ok = DBManager.executeUpdate(reactivateSql, nom, prenom, birthDate, phone, post, salaire, etat, joinDate, id);
        if (ok) {
            DBManager.executeUpdate("INSERT INTO date_emp (id_employee, date_embauche, \"date_départ\") VALUES (?, ?, '2030-12-12')", id, joinDate);
            if (!imagePath.isEmpty()) {
                DBManager.executeUpdate("DELETE FROM photo_path WHERE employee_id = ?", id);
                DBManager.executeUpdate("INSERT INTO photo_path (employee_id, path) VALUES (?, ?)", id, imagePath);
            }
            if (radio_buttom[1] != null) {
                radio_buttom[1].setSelected(true);
            }
            Action_radio(radio_buttom, g);
            clearFields();
            ActivityLogger.log("إعادة تفعيل موظف", "قام بإعادة تفعيل الموظف: " + nom + " " + prenom + " (معرف: " + id + ")");
            UITheme.showThemedMessage(frame, "تمت إعادة تفعيل الموظف بنجاح وعودته إلى قائمة العمال النشطين!", "نجاح العملية", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteOrArchiveEmployee() {
        String id = idField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();

        if (id.isEmpty()) {
            UITheme.showThemedMessage(frame, "الرجاء اختيار الموظف المراد إنهاء خدمته / حذفه", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String empName = nom.isEmpty() ? id : nom + " " + prenom;

        // ── verify employee is actually saved in DB ───────────────────────
        if (!employeeExistsInDB(id)) {
            UITheme.showThemedMessage(frame,
                "الموظف غير موجود في قاعدة البيانات!\nالرجاء إضافته أولاً بالضغط على زر \"موظف جديد\".",
                "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ── Main choice dialog ──────────────────────────────────────────────
        JDialog choiceDialog = new JDialog((Frame) null, "إجراء على الموظف", true);
        choiceDialog.setLayout(new BorderLayout(0, 0));
        choiceDialog.setSize(460, 270);
        choiceDialog.setLocationRelativeTo(frame);
        choiceDialog.setResizable(false);
        choiceDialog.getContentPane().setBackground(UITheme.getBgCard());

        // Title bar
        JLabel titleLbl = new JLabel("اختر الإجراء المناسب للموظف: " + empName, SwingConstants.RIGHT);
        titleLbl.setFont(UITheme.FONT_BOLD);
        titleLbl.setForeground(UITheme.getTextPrimary());
        titleLbl.setBorder(new EmptyBorder(18, 18, 10, 18));
        choiceDialog.add(titleLbl, BorderLayout.NORTH);

        // Description labels panel
        JPanel descPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        descPanel.setOpaque(false);
        descPanel.setBorder(new EmptyBorder(0, 18, 0, 18));
        descPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel archiveDesc = new JLabel("إنهاء المهام  — يُحفظ الموظف في الأرشيف مع تسجيل تاريخ الخروج",
            IconHelper.getIcon("dismissal.png", 18, 18), SwingConstants.RIGHT);
        archiveDesc.setFont(UITheme.FONT_REGULAR);
        archiveDesc.setForeground(UITheme.getTextSecondary());
        archiveDesc.setIconTextGap(8);
        archiveDesc.setHorizontalTextPosition(SwingConstants.LEFT);

        JLabel deleteDesc = new JLabel("حذف نهائي  — يُحذف الموظف وجميع بياناته بشكل لا يمكن التراجع عنه",
            IconHelper.getIcon("delete.png", 18, 18), SwingConstants.RIGHT);
        deleteDesc.setFont(UITheme.FONT_REGULAR);
        deleteDesc.setForeground(new Color(239, 68, 68));
        deleteDesc.setIconTextGap(8);
        deleteDesc.setHorizontalTextPosition(SwingConstants.LEFT);

        descPanel.add(archiveDesc);
        descPanel.add(deleteDesc);
        choiceDialog.add(descPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 14));
        btnPanel.setBackground(UITheme.getBgCard());

        JButton archiveBtn = UITheme.createButton("إنهاء المهام", IconHelper.getIcon("dismissal.png", 16, 16), new Color(245, 158, 11), Color.WHITE);
        JButton deleteBtn  = UITheme.createDangerButton("حذف نهائي", IconHelper.getIcon("delete.png", 16, 16));
        JButton cancelBtn  = UITheme.createSecondaryButton("إلغاء", null);

        // ── Archive action ──────────────────────────────────────────────────
        archiveBtn.addActionListener(e -> {
            choiceDialog.dispose();
            showArchiveDialog(id, empName);
        });

        // ── Permanent delete action ─────────────────────────────────────────
        deleteBtn.addActionListener(e -> {
            choiceDialog.dispose();
            int confirm = UITheme.showThemedConfirm(frame,
                "تحذير: سيتم حذف الموظف \"" + empName + "\" وجميع بياناته نهائياً!\n" +
                "هذا الإجراء لا يمكن التراجع عنه. هل أنت متأكد؟",
                "تأكيد الحذف النهائي", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = DBManager.executeUpdate("DELETE FROM employee WHERE id_employee = ?", id);
                if (ok) {
                    Action_radio(radio_buttom, g);
                    clearFields();
                    ActivityLogger.log("حذف نهائي لموظف", "قام بحذف الموظف نهائياً: " + empName + " (معرف: " + id + ")");
                    UITheme.showThemedMessage(frame, "تم حذف الموظف \"" + empName + "\" نهائياً من قاعدة البيانات.", "تم الحذف", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(e -> choiceDialog.dispose());

        btnPanel.add(archiveBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);
        choiceDialog.add(btnPanel, BorderLayout.SOUTH);
        choiceDialog.setVisible(true);
    }

    /** Shows the archive sub-dialog to pick an exit date and archive the employee. */
    private void showArchiveDialog(String id, String empName) {
        JDialog archDialog = new JDialog((Frame) null, "إنهاء مهام الموظف", true);
        archDialog.setLayout(new BorderLayout());
        archDialog.setSize(400, 230);
        archDialog.setLocationRelativeTo(frame);
        archDialog.setResizable(false);
        archDialog.getContentPane().setBackground(UITheme.getBgCard());

        JLabel titleLbl = new JLabel("إنهاء مهام: " + empName, SwingConstants.RIGHT);
        titleLbl.setFont(UITheme.FONT_BOLD);
        titleLbl.setForeground(UITheme.getTextPrimary());
        titleLbl.setBorder(new EmptyBorder(16, 16, 6, 16));
        archDialog.add(titleLbl, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(2, 1, 8, 8));
        p.setBackground(UITheme.getBgCard());
        p.setBorder(new EmptyBorder(10, 16, 10, 16));
        p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel lbl = UITheme.createFieldLabel("تاريخ الخروج / إنهاء الخدمة:");
        JDateChooser exitDateChooser = new JDateChooser(new Date());
        exitDateChooser.setDateFormatString("yyyy-MM-dd");
        UITheme.styleDateChooser(exitDateChooser);

        p.add(lbl);
        p.add(exitDateChooser);
        archDialog.add(p, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(UITheme.getBgCard());

        JButton confirmBtn = UITheme.createButton("تأكيد إنهاء المهام", IconHelper.getIcon("dismissal.png", 16, 16), new Color(245, 158, 11), Color.WHITE);
        JButton cancelBtn  = UITheme.createSecondaryButton("إلغاء", null);

        confirmBtn.addActionListener(e -> {
            Date exitDate = exitDateChooser.getDate() != null ? exitDateChooser.getDate() : new Date();
            String sqlExitDate = new SimpleDateFormat("yyyy-MM-dd").format(exitDate);

            DBManager.executeUpdate("UPDATE employee SET activ_emp = 0, date_depart = ? WHERE id_employee = ?", sqlExitDate, id);
            DBManager.executeUpdate("UPDATE date_emp SET \"date_départ\" = ? WHERE id_employee = ?", sqlExitDate, id);

            Action_radio(radio_buttom, g);
            clearFields();
            ActivityLogger.log("إنهاء خدمة موظف", "قام بنقل الموظف (معرف: " + id + ") إلى الأرشيف بتاريخ خروج: " + sqlExitDate);
            archDialog.dispose();
            UITheme.showThemedMessage(frame, "تم تحويل الموظف \"" + empName + "\" إلى الأرشيف بنجاح.", "تمت العملية", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelBtn.addActionListener(e -> archDialog.dispose());

        btnPanel.add(confirmBtn);
        btnPanel.add(cancelBtn);
        archDialog.add(btnPanel, BorderLayout.SOUTH);
        archDialog.setVisible(true);
    }

    private void clearFields() {
        idField.setText("");
        nomField.setText("");
        prenomField.setText("");
        phoneField.setText("");
        birthDateChooser.setDate(null);
        imagePath = "";
        profil_icon.refresh();
        table.clearSelection();
    }

    private void printTable() {
        try {
            MessageFormat header = new MessageFormat("قائمة العمال والموظفين - نظام الإدارة");
            MessageFormat footer = new MessageFormat("صفحة {0,number,integer}");
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            if (complete) {
                UITheme.showThemedMessage(frame, "تمت الطباعة بنجاح!", "طباعة", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            UITheme.showThemedMessage(frame, "حدث خطأ أثناء الطباعة: " + e.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }
}
