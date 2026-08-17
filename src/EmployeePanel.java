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
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h) - 20;
        int x = (w - size) / 2;
        int y = (h - size) / 2;

        Image img = null;
        if (EmployeePanel.imagePath != null && !EmployeePanel.imagePath.trim().isEmpty()) {
            File f = new File(EmployeePanel.imagePath);
            if (f.exists()) {
                img = new ImageIcon(EmployeePanel.imagePath).getImage();
            }
        }
        if (img == null) {
            ImageIcon def = IconHelper.getIcon("profil.png", size, size);
            if (def != null) img = def.getImage();
        }

        g2.setColor(UITheme.getBgCard());
        g2.fill(new RoundRectangle2D.Float(x, y, size, size, 20, 20));
        if (img != null) {
            Shape clip = new RoundRectangle2D.Float(x, y, size, size, 20, 20);
            g2.setClip(clip);
            g2.drawImage(img, x, y, size, size, this);
            g2.setClip(null);
        }
        g2.setColor(UITheme.getBorderColor());
        g2.setStroke(new BasicStroke(2));
        g2.draw(new RoundRectangle2D.Float(x, y, size, size, 20, 20));
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
        frame = new JPanel(new BorderLayout(15, 15));
        frame.setBackground(UITheme.getBgMain());
        frame.setBorder(new EmptyBorder(15, 15, 15, 15));

        buildUI();
    }

    private void buildUI() {
        // TOP Header Card
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout(15, 0));

        JLabel titleLabel = new JLabel("إدارة العمال والموظفين", SwingConstants.RIGHT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.getTextPrimary());

        // Search Bar in Top Card
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBox.setOpaque(false);
        searchBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel searchIcon = UITheme.createFieldLabel("🔍 بحث سريع:");
        searchField = UITheme.createTextField(16);
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
        JPanel centerContent = new JPanel(new BorderLayout(15, 15));
        centerContent.setOpaque(false);

        // Left Panel (Form + Photo)
        JPanel leftFormCard = UITheme.createCard();
        leftFormCard.setLayout(new BorderLayout(10, 10));
        leftFormCard.setPreferredSize(new Dimension(380, 0));

        // Photo Preview on top of Form
        JPanel photoContainer = new JPanel(new BorderLayout(0, 8));
        photoContainer.setOpaque(false);
        profil_icon.setPreferredSize(new Dimension(150, 150));
        photoContainer.add(profil_icon, BorderLayout.CENTER);

        JButton uploadBtn = UITheme.createSecondaryButton("رفع صورة الموظف", IconHelper.getIcon("addicon.png", 16, 16));
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
        JPanel formGrid = new JPanel(new GridLayout(8, 2, 8, 8));
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

        formGrid.add(UITheme.createFieldLabel("رقم التعريف الوطني (9 أرقام):"));
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
        JPanel rightTablePanel = new JPanel(new BorderLayout(10, 10));
        rightTablePanel.setOpaque(false);

        // Radio Buttons Filter Card
        JPanel radioCard = UITheme.createCard();
        radioCard.setLayout(new FlowLayout(FlowLayout.RIGHT, 25, 0));
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

        // Action Buttons Bar
        JPanel actionCard = UITheme.createCard();
        actionCard.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionCard.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton addBtn = UITheme.createPrimaryButton("موظف جديد", IconHelper.getIcon("include.png", 16, 16));
        JButton editBtn = UITheme.createSuccessButton("تعديل وحفظ", IconHelper.getIcon("edit.png", 16, 16));
        JButton deleteBtn = UITheme.createDangerButton("إنهاء خدمة / حذف", IconHelper.getIcon("delete.png", 16, 16));
        JButton clearBtn = UITheme.createSecondaryButton("إفراغ الخانات", IconHelper.getIcon("empty.png", 16, 16));
        JButton exportBtn = UITheme.createButton("تصدير Excel", IconHelper.getIcon("excel.png", 16, 16), new Color(16, 185, 129), Color.WHITE);
        JButton printBtn = UITheme.createButton("طباعة القائمة", IconHelper.getIcon("print.png", 16, 16), new Color(99, 102, 241), Color.WHITE);

        addBtn.addActionListener(e -> addEmployee());
        editBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteOrArchiveEmployee());
        clearBtn.addActionListener(e -> clearFields());
        exportBtn.addActionListener(e -> ExcelExporter.exportTable(table, "قائمة_العمال.xlsx", "العمال"));
        printBtn.addActionListener(e -> printTable());

        actionCard.add(addBtn);
        actionCard.add(editBtn);
        actionCard.add(deleteBtn);
        actionCard.add(clearBtn);
        actionCard.add(exportBtn);
        actionCard.add(printBtn);

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

        java.sql.Date birthDate = birthDateChooser.getDate() != null ? new java.sql.Date(birthDateChooser.getDate().getTime()) : null;
        java.sql.Date joinDate = new java.sql.Date(new Date().getTime());

        String checkSql = "SELECT activ_emp FROM employee WHERE id_employee = ?";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int active = rs.getInt("activ_emp");
                    if (active == 0) {
                        int c = UITheme.showThemedConfirm(frame, "هذا الموظف موجود في الأرشيف (سابق). هل تريد إعادة تفعيله؟", "استرجاع موظف", JOptionPane.YES_NO_OPTION);
                        if (c == JOptionPane.YES_OPTION) {
                            String reactivateSql = "UPDATE employee SET activ_emp = 1, nom=?, prenom=?, telephone=?, post=?, salaire=?, etat=?, date_embauche=?, date_depart='2030-12-12' WHERE id_employee = ?";
                            DBManager.executeUpdate(reactivateSql, nom, prenom, phone, post, salaire, etat, joinDate, id);
                            Action_radio(radio_buttom, g);
                            clearFields();
                            ActivityLogger.log("استرجاع موظف", "قام بإعادة تفعيل الموظف: " + nom + " " + prenom + " (معرف: " + id + ")");
                            UITheme.showThemedMessage(frame, "تمت إعادة تفعيل الموظف بنجاح!", "نجاح", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        UITheme.showThemedMessage(frame, "رقم التعريف الوطني مستعمل بالفعل لموظف حالي!", "خطأ", JOptionPane.ERROR_MESSAGE);
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
                DBManager.executeUpdate("INSERT INTO photo_path (employee_id, path) VALUES (?, ?) ON CONFLICT (employee_id) DO UPDATE SET path = EXCLUDED.path", id, imagePath);
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

        int salaire = 1000;
        try {
            salaire = Integer.parseInt(salaireStr.replaceAll("[^0-9]", ""));
        } catch (Exception ignored) {}

        java.sql.Date birthDate = birthDateChooser.getDate() != null ? new java.sql.Date(birthDateChooser.getDate().getTime()) : null;

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

    private void deleteOrArchiveEmployee() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            UITheme.showThemedMessage(frame, "الرجاء اختيار الموظف المراد إنهاء خدمته / حذفه", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) null, "تأكيد إنهاء خدمة الموظف", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(380, 220);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(UITheme.getBgCard());

        JPanel p = new JPanel(new GridLayout(2, 1, 8, 8));
        p.setBackground(UITheme.getBgCard());
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel lbl = UITheme.createFieldLabel("تاريخ الخروج / إنهاء الخدمة:");
        JDateChooser exitDateChooser = new JDateChooser(new Date());
        exitDateChooser.setDateFormatString("yyyy-MM-dd");
        UITheme.styleDateChooser(exitDateChooser);

        p.add(lbl);
        p.add(exitDateChooser);

        JButton okBtn = UITheme.createDangerButton("تأكيد الإنهاء", null);
        okBtn.addActionListener(e -> {
            Date exitDate = exitDateChooser.getDate() != null ? exitDateChooser.getDate() : new Date();
            java.sql.Date sqlExitDate = new java.sql.Date(exitDate.getTime());

            DBManager.executeUpdate("UPDATE employee SET activ_emp = 0, date_depart = ? WHERE id_employee = ?", sqlExitDate, id);
            DBManager.executeUpdate("UPDATE date_emp SET \"date_départ\" = ? WHERE id_employee = ?", sqlExitDate, id);

            Action_radio(radio_buttom, g);
            clearFields();
            ActivityLogger.log("إنهاء خدمة موظف", "قام بنقل الموظف (معرف: " + id + ") إلى الأرشيف بتاريخ خروج: " + sqlExitDate);
            dialog.dispose();
            UITheme.showThemedMessage(frame, "تم تحويل الموظف إلى الأرشيف (موظف سابق) بنجاح!", "تمت العملية", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(UITheme.getBgCard());
        btnPanel.add(okBtn);

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
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
