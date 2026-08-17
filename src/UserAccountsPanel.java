import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class UserAccountsPanel {
    public JPanel frame;
    public static CardLayout cardLayout;
    public static JPanel cardPanel;

    private DefaultTableModel model;
    private JTable table;

    public UserAccountsPanel(CardLayout cardLayout, JPanel cardPanel) {
        UserAccountsPanel.cardLayout = cardLayout;
        UserAccountsPanel.cardPanel = cardPanel;
        frame = new JPanel(new BorderLayout(15, 15));
        frame.setBackground(UITheme.getBgMain());
        frame.setBorder(new EmptyBorder(20, 20, 20, 20));

        buildUI();
    }

    private void buildUI() {
        // TOP Header Card
        JPanel topCard = UITheme.createCard();
        topCard.setLayout(new BorderLayout(15, 0));

        JLabel titleLabel = new JLabel("إدارة حسابات مستخدمي النظام والصلاحيات", SwingConstants.RIGHT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.getTextPrimary());

        JLabel subTitle = new JLabel("التحكم في المستخدمين، كلمات المرور وتعيين الصلاحيات (مدير / مستخدم عادي)", SwingConstants.RIGHT);
        subTitle.setFont(UITheme.FONT_SMALL);
        subTitle.setForeground(UITheme.getTextSecondary());

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(subTitle);

        JButton addUserBtn = UITheme.createPrimaryButton("إضافة مستخدم جديد", IconHelper.getIcon("addicon.png", 18, 18));
        addUserBtn.addActionListener(e -> showAddUserDialog());

        topCard.add(titleBox, BorderLayout.EAST);
        topCard.add(addUserBtn, BorderLayout.WEST);
        frame.add(topCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel centerCard = UITheme.createCard();
        centerCard.setLayout(new BorderLayout(10, 12));

        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        model.addColumn("معرف الحساب");
        model.addColumn("اسم المستخدم");
        model.addColumn("نوع الصلاحية");

        table = new JTable(model);
        UITheme.styleTable(table);

        JScrollPane scrollPane = UITheme.createScrollPane(table);
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        actionBox.setOpaque(false);
        actionBox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton resetPassBtn = UITheme.createButton("تغيير كلمة المرور", IconHelper.getIcon("edit.png", 18, 18), UITheme.WARNING, Color.WHITE);
        resetPassBtn.addActionListener(e -> showChangePasswordDialog());

        JButton toggleRoleBtn = UITheme.createButton("تغيير نوع الصلاحية", IconHelper.getIcon("profil.png", 18, 18), UITheme.INFO, Color.WHITE);
        toggleRoleBtn.addActionListener(e -> toggleSelectedUserRole());

        JButton deleteBtn = UITheme.createDangerButton("حذف الحساب المحدد", IconHelper.getIcon("delete.png", 18, 18));
        deleteBtn.addActionListener(e -> deleteSelectedUser());

        actionBox.add(resetPassBtn);
        actionBox.add(toggleRoleBtn);
        actionBox.add(deleteBtn);

        centerCard.add(actionBox, BorderLayout.SOUTH);
        frame.add(centerCard, BorderLayout.CENTER);

        refreshUsers();
    }

    public void refreshUsers() {
        model.setRowCount(0);
        String sql = "SELECT id, FirstN, role FROM users ORDER BY id ASC";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("FirstN"));
                String role = rs.getString("role");
                row.add(role != null && role.equalsIgnoreCase("ADMIN") ? "مدير النظام (ADMIN)" : "مستخدم عادي (USER)");
                model.addRow(row);
            }
        } catch (Exception ex) {
            System.err.println("Error loading users: " + ex.getMessage());
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) null, "إضافة مستخدم جديد للنظام", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(440, 390);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(UITheme.getBgCard());

        JPanel p = new JPanel(new GridLayout(4, 2, 8, 14));
        p.setBackground(UITheme.getBgCard());
        p.setBorder(new EmptyBorder(20, 20, 15, 20));
        p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JTextField uField = UITheme.createTextField(10);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"مستخدم عادي (USER)", "مدير النظام (ADMIN)"});
        UITheme.styleComboBox(roleCombo);

        JPasswordField pField = UITheme.createPasswordField(10);
        JPasswordField pConfirmField = UITheme.createPasswordField(10);

        p.add(UITheme.createFieldLabel("اسم المستخدم:"));
        p.add(uField);
        p.add(UITheme.createFieldLabel("نوع الصلاحية:"));
        p.add(roleCombo);
        p.add(UITheme.createFieldLabel("كلمة المرور:"));
        p.add(pField);
        p.add(UITheme.createFieldLabel("تأكيد كلمة المرور:"));
        p.add(pConfirmField);

        JButton saveBtn = UITheme.createPrimaryButton("حفظ المستخدم الجديد", null);
        saveBtn.setPreferredSize(new Dimension(0, 42));
        saveBtn.addActionListener(e -> {
            String u = uField.getText().trim();
            String p1 = new String(pField.getPassword()).trim();
            String p2 = new String(pConfirmField.getPassword()).trim();
            String chosenRole = roleCombo.getSelectedIndex() == 1 ? "ADMIN" : "USER";

            if (u.isEmpty() || p1.isEmpty()) {
                UITheme.showThemedMessage(dialog, "الرجاء إدخال اسم المستخدم وكلمة المرور!", "تنبيه", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!p1.equals(p2)) {
                UITheme.showThemedMessage(dialog, "كلمتا المرور غير متطابقتين!", "خطأ", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO users (FirstN, pass, role) VALUES (?, ?, ?)";
            boolean ok = DBManager.executeUpdate(sql, u, p1, chosenRole);
            if (ok) {
                ActivityLogger.log("إضافة مستخدم", "قام بإنشاء حساب جديد للمستخدم: " + u + " بصلاحية (" + chosenRole + ")");
                refreshUsers();
                dialog.dispose();
                UITheme.showThemedMessage(frame, "تمت إضافة المستخدم بنجاح!", "تم بنجاح", JOptionPane.INFORMATION_MESSAGE);
            } else {
                UITheme.showThemedMessage(dialog, "حدث خطأ أثناء إضافة المستخدم! قد يكون الاسم مكرراً.", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(UITheme.getBgCard());
        btnPanel.setBorder(new EmptyBorder(0, 20, 15, 20));
        btnPanel.add(saveBtn, BorderLayout.CENTER);

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showChangePasswordDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UITheme.showThemedMessage(frame, "الرجاء اختيار مستخدم من الجدول لتغيير كلمة مروره!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mRow = table.convertRowIndexToModel(row);
        String userId = String.valueOf(model.getValueAt(mRow, 0));
        String username = String.valueOf(model.getValueAt(mRow, 1));

        JDialog dialog = new JDialog((Frame) null, "تغيير كلمة المرور لـ " + username, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(440, 290);
        dialog.setLocationRelativeTo(frame);
        dialog.getContentPane().setBackground(UITheme.getBgCard());

        JPanel p = new JPanel(new GridLayout(2, 2, 8, 14));
        p.setBackground(UITheme.getBgCard());
        p.setBorder(new EmptyBorder(20, 20, 15, 20));
        p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPasswordField pField = UITheme.createPasswordField(10);
        JPasswordField pConfirmField = UITheme.createPasswordField(10);

        p.add(UITheme.createFieldLabel("كلمة المرور الجديدة:"));
        p.add(pField);
        p.add(UITheme.createFieldLabel("تأكيد كلمة المرور:"));
        p.add(pConfirmField);

        JButton saveBtn = UITheme.createSuccessButton("تحديث كلمة المرور", null);
        saveBtn.setPreferredSize(new Dimension(0, 42));
        saveBtn.addActionListener(e -> {
            String p1 = new String(pField.getPassword()).trim();
            String p2 = new String(pConfirmField.getPassword()).trim();

            if (p1.isEmpty()) {
                UITheme.showThemedMessage(dialog, "الرجاء إدخال كلمة المرور الجديدة!", "تنبيه", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!p1.equals(p2)) {
                UITheme.showThemedMessage(dialog, "كلمتا المرور غير متطابقتين!", "خطأ", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "UPDATE users SET pass = ? WHERE id = ?";
            boolean ok = DBManager.executeUpdate(sql, p1, Integer.parseInt(userId));
            if (ok) {
                ActivityLogger.log("تغيير كلمة مرور", "قام بتغيير كلمة المرور للحساب: " + username);
                dialog.dispose();
                UITheme.showThemedMessage(frame, "تم تحديث كلمة المرور للمستخدم (" + username + ") بنجاح!", "تم بنجاح", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(UITheme.getBgCard());
        btnPanel.setBorder(new EmptyBorder(0, 20, 15, 20));
        btnPanel.add(saveBtn, BorderLayout.CENTER);

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void toggleSelectedUserRole() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UITheme.showThemedMessage(frame, "الرجاء اختيار مستخدم لتعديل صلاحيته!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mRow = table.convertRowIndexToModel(row);
        String userId = String.valueOf(model.getValueAt(mRow, 0));
        String username = String.valueOf(model.getValueAt(mRow, 1));
        String currentRoleText = String.valueOf(model.getValueAt(mRow, 2));

        String newRole = currentRoleText.contains("ADMIN") ? "USER" : "ADMIN";
        int confirm = UITheme.showThemedConfirm(frame, "هل تريد تغيير صلاحية المستخدم (" + username + ") إلى: " + (newRole.equals("ADMIN") ? "مدير النظام (ADMIN)" : "مستخدم عادي (USER)") + "؟", "تأكيد تغيير الصلاحية", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DBManager.executeUpdate("UPDATE users SET role = ? WHERE id = ?", newRole, Integer.parseInt(userId));
            ActivityLogger.log("تغيير صلاحية مستخدم", "قام بتغيير صلاحية المستخدم: " + username + " إلى: " + newRole);
            refreshUsers();
            UITheme.showThemedMessage(frame, "تم تغيير الصلاحية بنجاح!", "تم التحديث", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UITheme.showThemedMessage(frame, "الرجاء اختيار مستخدم لحذفه!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mRow = table.convertRowIndexToModel(row);
        String userId = String.valueOf(model.getValueAt(mRow, 0));
        String username = String.valueOf(model.getValueAt(mRow, 1));

        if (username.equalsIgnoreCase("admin") || Integer.parseInt(userId) == SessionManager.getUserId()) {
            UITheme.showThemedMessage(frame, "لا يمكنك حذف حساب المدير الرئيسي أو حسابك الحالي!", "منع الحذف", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = UITheme.showThemedConfirm(frame, "هل أنت متأكد من رغبتك في حذف حساب المستخدم (" + username + ") نهائياً؟", "تأكيد الحذف", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DBManager.executeUpdate("DELETE FROM users WHERE id = ?", Integer.parseInt(userId));
            ActivityLogger.log("حذف مستخدم", "قام بحذف حساب المستخدم: " + username);
            refreshUsers();
            UITheme.showThemedMessage(frame, "تم حذف الحساب بنجاح!", "تم الحذف", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
