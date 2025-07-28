
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.toedter.calendar.JDateChooser;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author mokhtar-mammeri
 */
class pd extends JPanel {

    public void paintComponent(Graphics g) {
        if (Employee.imagePath.isEmpty()) {
            Employee.img = new ImageIcon("/home/mokhtar-mammeri/Desktop/BME2/icons/profil.png").getImage();
        } else {
            Employee.img = new ImageIcon(Employee.imagePath).getImage();
        }
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR); // or VALUE_INTERPOLATION_BICUBIC
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(Employee.img, 25, 30, w - 50, h - 50, this);

    }

    public void rp() {
        repaint();
    }

}

public class Employee {

    static CardLayout cardLayout;
    static JPanel cardPanel;
    static JPanel frame;
    static DefaultTableModel model = new DefaultTableModel();
    static JTable table;
    static JButton add_icon;
    static String imagePath = "";
    static Image img;
    static pd profil_icon = new pd();
    static JRadioButton radio_buttom[] = new JRadioButton[3];
    static ButtonGroup g;
    
    Employee(CardLayout a, JPanel b) {
        cardLayout = a;
        cardPanel = b;
        frame = new JPanel();
        //frame.setSize(1000, 700);

        create_interfaceGraphics();
        frame.setVisible(true);
    }

    public static void Action_radio(JRadioButton b[], ButtonGroup g) {

        try {
            String selectSQL = "";

            if (b[0].isSelected()) {
                selectSQL = "SELECT * FROM Employee";
                System.out.println("hellersoeiru");
            } else if (b[1].isSelected()) {
                selectSQL = "SELECT * FROM Employee WHERE activ_emp = '1'";
            } else if (b[2].isSelected()) {
                selectSQL = "SELECT * FROM Employee WHERE activ_emp = '0'";
                System.out.println("hellersoeiru999999999999");
            } else {

                selectSQL = "SELECT * FROM Employee WHERE activ_emp = '1'";
            }

            ResultSet rs = DataBaseMangemet.select_Query(selectSQL);
            model.setRowCount(0);
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id_Employee"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("prenom"));
                row.add(rs.getString("date_naissance"));
                row.add(rs.getString("date_embauche"));
                row.add(rs.getString("date_depart"));
                row.add(rs.getString("telephone"));
                row.add(rs.getString("post"));
                row.add(rs.getString("salaire"));
                row.add(rs.getString("etat"));
                model.addRow(row);
            }

            DataBaseMangemet.conn.close();
        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }

    }

    public void create_interfaceGraphics() {
        frame.setLayout(new BorderLayout());

        JPanel North = new JPanel();
        JPanel east = new JPanel();
        east.setPreferredSize(new Dimension(190, 0));
        east.setBackground(Color.GRAY);
        North.setLayout(new BorderLayout());
        North.setPreferredSize(new Dimension(frame.getWidth(), 60));

        JLabel label_head = new JLabel("العمال");
        label_head.setHorizontalAlignment(SwingConstants.CENTER);
        label_head.setVerticalAlignment(SwingConstants.CENTER);
        Font f1 = new Font("Arial", Font.BOLD, 35);
        label_head.setFont(f1);
        North.setBackground(Color.gray);

        North.add(label_head, BorderLayout.CENTER);
        North.add(east, BorderLayout.EAST);
        ImageIcon icon = new ImageIcon("/home/mokhtar-mammeri/Desktop/BME2/icons/home.png");
        JButton home_page = new JButton("الصفحة الرئيسية", icon);
        home_page.setIconTextGap(4);
        home_page.setPreferredSize(new Dimension(250, 60));
        home_page.setBackground(Color.gray);
        home_page.setFont(new Font("Arial", Font.BOLD, 25));
        home_page.setFocusPainted(false);

        home_page.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                cardLayout.show(cardPanel, "page1");
            }
        });

        North.add(home_page, BorderLayout.WEST);
        frame.add(North, BorderLayout.NORTH);

        JPanel west = new JPanel();
        west.setLayout(new BorderLayout());
        JPanel south2 = new JPanel();
        south2.setLayout(new BoxLayout(south2, BoxLayout.Y_AXIS));
        west.add(south2, BorderLayout.SOUTH);

        west.setPreferredSize(new Dimension(300, 0));
        west.setBackground(Color.red);
        frame.add(west, BorderLayout.WEST);

        profil_icon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        profil_icon.setBackground(new Color(10, 61, 98));

        west.add(profil_icon, BorderLayout.CENTER);

        south2.setBackground(new Color(10, 61, 98));
        south2.setPreferredSize(new Dimension(0, 200));

        south2.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel img_emp = new JLabel("صورة الموضف");
        img_emp.setOpaque(true);

        edit_label(img_emp);
        img_emp.setMaximumSize(new Dimension(200, 30));

        img_emp.setAlignmentX(Component.CENTER_ALIGNMENT);
        img_emp.setVerticalAlignment(SwingConstants.CENTER);
        south2.add(img_emp);
        south2.add(Box.createRigidArea(new Dimension(0, 20)));

        ImageIcon addicon = new ImageIcon("/home/mokhtar-mammeri/Desktop/BME2/icons/addicon.png");

        add_icon = new JButton("رفع صورة", addicon);
        add_icon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "صور", "jpg", "jpeg", "png", "gif"));

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePath = selectedFile.getAbsolutePath();
                    profil_icon.rp();
                }
            }
        });
        add_icon.setForeground(Color.red);
        add_icon.setFont(new Font("Arial", Font.BOLD, 15));
        add_icon.setIconTextGap(40);
        add_icon.setMaximumSize(new Dimension(240, add_icon.getPreferredSize().height + 16));

        add_icon.setBackground(Color.WHITE);
        add_icon.setFocusPainted(false);
        add_icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        south2.add(add_icon);
        south2.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel center = new JPanel();
        center.setLayout(new GridLayout(9, 1, 0, 5));

        center.setBackground(new Color(10, 61, 98));
        frame.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setPreferredSize(new Dimension(frame.getWidth(), 200));
        frame.add(south, BorderLayout.SOUTH);

        model.addColumn("رقم التعريف الوطني");
        model.addColumn("الإسم");
        model.addColumn("اللقب");
        model.addColumn("تاريخ الميلاد");
        model.addColumn("تاريخ الإلتحاق");
        model.addColumn("تاريخ الخروج");
        model.addColumn("الهاتف");
        model.addColumn("الرتبة");
        model.addColumn("الدخل");
        model.addColumn("الحالة");

        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };
        inputData(center);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }
        table.getTableHeader().setReorderingAllowed(false);

        Font f2 = new Font("Arial", Font.BOLD, 15);
        table.setRowHeight(40);
        table.setFont(f2);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        JTableHeader header = table.getTableHeader();
        header.setFont(f2);
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);

        south.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);

        south.add(scrollPane, BorderLayout.CENTER);

    }

    public static void edit_label(JLabel a) {
        Font f1 = new Font("Arial", Font.BOLD, 15);
        a.setFont(f1);
        a.setOpaque(true);
        a.setBackground(new Color(64, 81, 99));
        a.setPreferredSize(new Dimension(150, 20));
        a.setHorizontalAlignment(SwingConstants.CENTER);
        a.setVerticalAlignment(SwingConstants.CENTER);
        a.setForeground(Color.WHITE);
    }

    public static void inputData(JPanel center) {
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();
        JPanel p6 = new JPanel();
        JPanel p0 = new JPanel();

        p0.setBackground(new Color(10, 61, 98));
        center.add(p0);

        Font f1 = new Font("Arial", Font.BOLD, 15);

        // p1 
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        JLabel name_label = new JLabel("إسم الموضف");
        edit_label(name_label);
        JTextField name_text = new JTextField(11);
        name_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        name_text.setFont(f1);

        name_label.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        name_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        JLabel prenom_label = new JLabel("اللقب");
        JTextField prenom_text = new JTextField(11);
        prenom_text.setFont(f1);
        prenom_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        edit_label(prenom_label);
        prenom_label.setMaximumSize(new Dimension(Integer.MAX_VALUE, prenom_label.getPreferredSize().height + 11));
        prenom_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, prenom_text.getPreferredSize().height + 11));

        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        p1.add(prenom_text);
        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        p1.add(prenom_label);

        p1.add(prenom_label);
        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        JButton a = new JButton("بحث");
        a.setPreferredSize(new Dimension(50, 20));
        //p1.add(a);
        p1.add(name_text);
        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        p1.add(name_label);
        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        p1.setBackground(new Color(10, 61, 98));

        center.add(p1);
        System.out.println(name_text.getSize());

        //p2
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        JLabel date_label = new JLabel("تاريخ الميلاد");
        edit_label(date_label);
        JDateChooser date_text = new JDateChooser();

// الوصول إلى محرر التاريخ الداخلي وتعديل الخط والمحاذاة
        JTextField dateEditor = (JTextField) date_text.getDateEditor().getUiComponent();
        // محاذاة لليمين
        dateEditor.setFont(f1);      // الخط
        dateEditor.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        date_text.setFont(f1);

        date_label.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        date_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        date_text.setPreferredSize(new Dimension(169, 0));

        JLabel phone = new JLabel("الهاتف");
        JTextField phone_text2 = new JTextField(11);
        phone_text2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        edit_label(phone);
        phone_text2.setFont(f1);

        phone.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        phone_text2.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        p2.add(phone_text2);
        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        p2.add(phone);
        p2.add(Box.createRigidArea(new Dimension(20, 0)));

        p2.add(date_text);
        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        p2.add(date_label);
        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        p2.setBackground(new Color(10, 61, 98));

        center.add(p2);

        //p3
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        JLabel grad = new JLabel("الرتبة");
        edit_label(grad);
        String grad_value[] = {"سائق", "عامل"};

        JComboBox<String> grad_text = new JComboBox<>(grad_value);
        grad_text.setFont(f1);
        grad_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) grad_text.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

        grad.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        grad_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        grad_text.setPreferredSize(new Dimension(169, 22));

        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        p3.add(grad_text);
        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        p3.add(grad);
        p3.add(Box.createRigidArea(new Dimension(20, 0)));

        JLabel salar = new JLabel("الدخل");
        edit_label(salar);
        String salar_value[] = {"30000DA", "40000DA"};
        JComboBox<String> salar_text = new JComboBox<>(salar_value);
        salar_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) salar_text.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
        salar_text.setFont(f1);
        salar.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        salar_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        salar_text.setPreferredSize(new Dimension(169, 22));

        p3.add(salar_text);
        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        p3.add(salar);
        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        p3.setBackground(new Color(10, 61, 98));

        center.add(p3);

        //p4
        p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
        JLabel state = new JLabel("الحالة الإجتماعية");
        edit_label(state);
        String state_value[] = {"أعزب", "متزوج"};

        JComboBox<String> state_text = new JComboBox<>(state_value);
        state_text.setFont(f1);
        state_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) state_text.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

        state.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        state_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        state_text.setPreferredSize(new Dimension(169, 22));

        p4.add(Box.createRigidArea(new Dimension(20, 0)));
        p4.add(state_text);
        p4.add(Box.createRigidArea(new Dimension(20, 0)));
        p4.add(state);
        p4.add(Box.createRigidArea(new Dimension(20, 0)));
        p4.setBackground(new Color(10, 61, 98));

        JLabel number = new JLabel("رقم التعريف الوطني");
        edit_label(number);

        JTextField number_text = new JTextField(11);
        number_text.setFont(f1);
        number_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        number.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        number_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        JButton searche1 = new JButton("بحث");
        // searche1.setMaximumSize(new Dimension(, number_text.getPreferredSize().height + 11));
        searche1.setForeground(Color.white);
        searche1.setFocusPainted(false);
        // searche1.setBackground(new Color(64, 81, 99));
        //  searche1.setFont(f1);
        //p4.add(searche1);

        p4.add(number_text);
        p4.add(Box.createRigidArea(new Dimension(20, 0)));
        p4.add(number);
        p4.add(Box.createRigidArea(new Dimension(20, 0)));

        center.add(p4);

        // p7
        JPanel p7 = new JPanel();
        p7.setBackground(new Color(10, 61, 98));
        p7.setLayout(new BoxLayout(p7, BoxLayout.X_AXIS));
        JLabel l = new JLabel("sdfs");
        edit_label(l);

        JTextField b22 = new JTextField(11);

        b22.setFont(f1);

        b22.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        l.setForeground(l.getBackground());
        l.setForeground(new Color(10, 61, 98));

        l.setBackground(new Color(10, 61, 98));

        b22.setForeground(new Color(10, 61, 98));
        b22.setCaretColor(new Color(10, 61, 98));
        b22.setBackground(new Color(10, 61, 98));
        b22.setBorder(null);
        b22.setEnabled(false);
        b22.setFocusable(false);

        p7.add(Box.createRigidArea(new Dimension(20, 0)));
        p7.add(b22);
        p7.add(Box.createRigidArea(new Dimension(20, 0)));
        p7.add(l);
        p7.add(Box.createRigidArea(new Dimension(20, 0)));

        JLabel date_join = new JLabel("تاريخ التسجيل");
        edit_label(date_join);
        JDateChooser date_text2 = new JDateChooser();

        JTextField dateEditor2 = (JTextField) date_text2.getDateEditor().getUiComponent();

        dateEditor2.setFont(f1);
        dateEditor2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        date_text2.setFont(f1);

        date_join.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_label.getPreferredSize().height + 11));
        date_text2.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        date_text2.setPreferredSize(new Dimension(169, 0));

        p7.add(date_text2);
        p7.add(Box.createRigidArea(new Dimension(20, 0)));
        p7.add(date_join);
        p7.add(Box.createRigidArea(new Dimension(20, 0)));
        center.add(p7);

        //p5
        ImageIcon icons[] = new ImageIcon[6];
        String icons_path[] = {"/home/mokhtar-mammeri/Desktop/BME2/icons/excel.png", "/home/mokhtar-mammeri/Desktop/BME2/icons/delete.png",
            "/home/mokhtar-mammeri/Desktop/BME2/icons/include.png", "/home/mokhtar-mammeri/Desktop/BME2/icons/edit.png", "/home/mokhtar-mammeri/Desktop/BME2/icons/print.png",
            "/home/mokhtar-mammeri/Desktop/BME2/icons/empty.png"};
        for (int i = 0; i < 6; i++) {
            icons[i] = new ImageIcon(icons_path[i]);
        }

        String buttons[] = {"إستخراج", "حذف البيان", "موضف جديد", "تعديل وحفض"};
        JButton b[] = new JButton[6];
        p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
        for (int i = 0; i < 3; i++) {
            b[i] = new JButton(buttons[i], icons[i]);
            b[i].setIconTextGap(35);
            b[i].setPreferredSize(new Dimension(230, 40));
            b[i].setForeground(Color.red);
            b[i].setFocusPainted(false);
            b[i].setBackground(Color.white);
            b[i].setFont(f1);
            b[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, b[i].getPreferredSize().height + 16));

            p5.add(Box.createRigidArea(new Dimension(10, 0)));
            p5.add(b[i]);
        }
        p5.add(Box.createRigidArea(new Dimension(10, 0)));
        p5.setBackground(new Color(10, 61, 98));
        center.add(p5);

        //p6
        p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));

        JButton b1 = new JButton("طباعة", icons[4]);
        b1.setIconTextGap(65);

        JButton b2 = new JButton("إفراغ", icons[5]);
        b2.setIconTextGap(80);
        b[4] = b1;
        b[5] = b2;
        b1.setFont(f1);
        b2.setFont(f1);
        b1.setPreferredSize(new Dimension(228, 37));
        b1.setForeground(Color.red);
        b1.setFocusPainted(false);
        b1.setBackground(Color.white);

        b2.setPreferredSize(new Dimension(228, 37));
        b2.setForeground(Color.red);
        b2.setFocusPainted(false);
        b2.setBackground(Color.white);

        b1.setMaximumSize(new Dimension(Integer.MAX_VALUE, b1.getPreferredSize().height + 16));
        b2.setMaximumSize(new Dimension(Integer.MAX_VALUE, b2.getPreferredSize().height + 16));

        b[3] = new JButton(buttons[3], icons[3]);
        b[3].setIconTextGap(10);
        b[3].setFont(f1);

        b[3].setPreferredSize(new Dimension(226, 37));
        b[3].setMaximumSize(new Dimension(Integer.MAX_VALUE, b[3].getPreferredSize().height + 16));
        b[3].setForeground(Color.red);
        b[3].setFocusPainted(false);
        b[3].setBackground(Color.white);

        p6.add(Box.createRigidArea(new Dimension(10, 0)));

        p6.add(b[3]);
        p6.add(Box.createRigidArea(new Dimension(10, 0)));
        p6.add(b1);
        p6.add(Box.createRigidArea(new Dimension(10, 0)));
        p6.add(b2);
        p6.add(Box.createRigidArea(new Dimension(10, 0)));
        p6.setBackground(new Color(10, 61, 98));
        center.add(p6);

        JPanel p8 = new JPanel();

        p8.setBackground(new Color(10, 61, 98));

        String radio_string[] = {"كل الموظفين", "الموظفين الحاليين", "الموظفين السابقين"};

        
        g = new ButtonGroup();
        for (int i = 0; i < 3; i++) {
            radio_buttom[i] = new JRadioButton(radio_string[i]);
            radio_buttom[i].setFont(new Font("Arial", Font.BOLD, 15));
            radio_buttom[i].setFocusPainted(false);
            radio_buttom[i].setBackground(new Color(10, 61, 98));
            radio_buttom[i].setForeground(Color.black);
            g.add(radio_buttom[i]);
            p8.add(radio_buttom[i]);
            p8.add(new JLabel("                                          "));

        }
        ActionListener aa = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                
                    System.out.println("hell");
                    Action_radio(Employee.radio_buttom,Employee.g);
                    System.out.println("hell");
                
                  
            }
        };

        radio_buttom[0].addActionListener(aa);
        radio_buttom[1].addActionListener(aa);
        radio_buttom[2].addActionListener(aa);
        Action_radio(radio_buttom, g);

        center.add(p8);

        // Actions -------------------------------------------------------------------
        ActionListener ActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == b[2]) {
                    if (number_text.getText().length() == 9 && name_text.getText().length() < 50 && prenom_text.getText().length() < 50 && phone_text2.getText().length() == 10) {
                        if (name_text.getText().length() == 0 || prenom_text.getText().length() == 0 || phone_text2.getText().length() == 0) {
                            JOptionPane.showMessageDialog(null, "الرجاء ملئ كل المعلومات", "خطأ", JOptionPane.ERROR_MESSAGE);
                        } else {
                            if (!phone_text2.getText().matches("\\d+")) {
                                JOptionPane.showMessageDialog(null, "يرجى التأكد من رقم الهاتف", "خطأ", JOptionPane.ERROR_MESSAGE);
                            } else {
                                try {
                                    String selectSQL = "SELECT id_employee,activ_emp FROM employee where id_employee = '" + number_text.getText() + "'";
                                    ResultSet rs2 = DataBaseMangemet.select_Query(selectSQL);
                                    if (rs2.next()) {

                                        if (rs2.getString("activ_emp").equals("0")) {
                                            if (JOptionPane.showConfirmDialog(null, "هذا الموظف محذوف هل أنت متأكد من إسترجاعه ", "خطأ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                                selectSQL = "UPDATE employee SET activ_emp = '1', date_embauche= '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "',date_depart= '" + "2030-12-12" + "' where id_employee='"+number_text.getText()+"';";
                                                DataBaseMangemet.ExexcuteStatement(selectSQL);
                                                selectSQL = "insert into date_emp (id_employee,date_embauche,date_départ) values('" + number_text.getText() + "', '" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "', '2030-12-12')";
                                                DataBaseMangemet.ExexcuteStatement(selectSQL);

                                                Action_radio(radio_buttom, g);

                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "رقم التعريف الوطني مستعمل", "خطأ", JOptionPane.ERROR_MESSAGE);
                                        }

                                    } else {
                                        selectSQL = "SELECT telephone FROM employee where telephone = '" + phone_text2.getText() + "'";
                                        rs2 = DataBaseMangemet.select_Query(selectSQL);
                                        if (rs2.next()) {
                                            JOptionPane.showMessageDialog(null, "رقم الهاتف موجود ", "خطأ", JOptionPane.ERROR_MESSAGE);
                                        } else {
                                            Date date = new Date();  // التاريخ الحالي
                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                                            String formattedDate = formatter.format(date);
                                            String[] info = {
                                                name_text.getText(),
                                                prenom_text.getText(),
                                                phone_text2.getText(),
                                                (String) salar_text.getSelectedItem(),
                                                (String) state_text.getSelectedItem(),
                                                (String) grad_text.getSelectedItem(),
                                                number_text.getText(),
                                                new SimpleDateFormat("yyyy-MM-dd").format(date_text.getDate()),
                                                formattedDate
                                            };

                                            String sql = "INSERT INTO employee (id_employee, nom, prenom, date_naissance, date_embauche, telephone, post, salaire, etat, date_depart,activ_emp) "
                                                    + "VALUES ('" + info[6] + "', '" + info[0] + "', '" + info[1] + "', '" + info[7] + "', '" + info[8] + "', '" + info[2] + "', '" + info[5] + "', '" + info[3] + "', '" + info[4] + "', '2030-12-12','1');";

                                            DataBaseMangemet.ExexcuteStatement(sql);
                                            sql = "insert into photo_path(employee_id,path) values('" + info[6] + "','" + imagePath + "');";
                                            if (!imagePath.isEmpty()) {
                                                DataBaseMangemet.ExexcuteStatement(sql);
                                            }

                                            sql = "insert into date_emp (id_employee,date_embauche,date_départ) values('" + info[6] + "', '" + info[8] + "', '2030-12-12')";
                                            DataBaseMangemet.ExexcuteStatement(sql);

                                            Action_radio(radio_buttom, g);

                                        }

                                    }
                                } catch (Exception e2) {
                                    System.out.println("❌ Connection failed.546");
                                    e2.printStackTrace();

                                }
                            }

                        }

                    } else {
                        if (number_text.getText().length() == 0) {
                            JOptionPane.showMessageDialog(null, "الرجاء ملئ كل المعلومات", "خطأ", JOptionPane.ERROR_MESSAGE);
                        } else {
                            if (number_text.getText().length() > 9 || number_text.getText().length() < 9 || name_text.getText().length() > 50 || prenom_text.getText().length() > 50 || phone_text2.getText().length() > 10 || phone_text2.getText().length() < 10) {
                                JOptionPane.showMessageDialog(null, "الرجاء التأكد من طول المعلومات المدخلة", "خطأ", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }

                } else {
                    if (e.getSource() == b[5]) {
                        name_text.setText("");
                        prenom_text.setText("");
                        number_text.setText("");
                        phone_text2.setText("");
                        date_text.setDate(null);
                        imagePath = "";
                        profil_icon.rp();

                    } else {
                        if (e.getSource() == b[3]) {
                            if (number_text.getText().length() == 9) {
                                try {
                                    String selectSQL = "SELECT id_employee FROM employee where id_employee = '" + number_text.getText() + "'";
                                    ResultSet rs2 = DataBaseMangemet.select_Query(selectSQL);
                                    if (!rs2.next()) {
                                        JOptionPane.showMessageDialog(null, "رقم التعريف الوطني غير موجود", "خطأ", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        if (name_text.getText().length() < 50 && prenom_text.getText().length() < 50 && phone_text2.getText().length() == 10) {
                                            if (prenom_text.getText().length() == 0 || phone_text2.getText().length() == 0) {
                                                JOptionPane.showMessageDialog(null, "الرجاء ملئ كل المعلومات", "خطأ", JOptionPane.ERROR_MESSAGE);
                                            } else {
                                                if (!phone_text2.getText().matches("\\d+")) {
                                                    JOptionPane.showMessageDialog(null, "يرجى التأكد من رقم الهاتف", "خطأ", JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    try {
                                                        if (JOptionPane.showConfirmDialog(null, "هل أنت متأكد", "تأكيد", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                                            selectSQL = "SELECT id_employee FROM employee where id_employee = '" + number_text.getText() + "'";
                                                            rs2 = DataBaseMangemet.select_Query(selectSQL);

                                                            selectSQL = "SELECT telephone FROM employee where telephone = '" + phone_text2.getText() + "' and id_employee!='" + number_text.getText() + "'";
                                                            rs2 = DataBaseMangemet.select_Query(selectSQL);
                                                            if (rs2.next()) {
                                                                JOptionPane.showMessageDialog(null, "رقم الهاتف موجود ", "خطأ", JOptionPane.ERROR_MESSAGE);
                                                            } else {
                                                                Date date = new Date();  // التاريخ الحالي
                                                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                                                                String formattedDate = formatter.format(date);
                                                                String[] info = {
                                                                    name_text.getText(),
                                                                    prenom_text.getText(),
                                                                    phone_text2.getText(),
                                                                    (String) salar_text.getSelectedItem(),
                                                                    (String) state_text.getSelectedItem(),
                                                                    (String) grad_text.getSelectedItem(),
                                                                    number_text.getText(),
                                                                    new SimpleDateFormat("yyyy-MM-dd").format(date_text.getDate()),
                                                                    formattedDate
                                                                };

                                                                String sql = "UPDATE employee SET "
                                                                        + "nom='" + info[0] + "', "
                                                                        + "prenom='" + info[1] + "', "
                                                                        + "date_naissance='" + info[7] + "', "
                                                                        + "telephone='" + info[2] + "', "
                                                                        + "post='" + info[5] + "', "
                                                                        + "salaire='" + info[3] + "', "
                                                                        + "etat='" + info[4] + "', "
                                                                        + "date_embauche='" + info[8] + "' "
                                                                        + "WHERE id_employee='" + info[6] + "'";

                                                                DataBaseMangemet.ExexcuteStatement(sql);

                                                                if (!imagePath.isEmpty()) {
                                                                    sql = " select employee_id from photo_path where employee_id='" + info[6] + "';";
                                                                    ResultSet rs = DataBaseMangemet.select_Query(sql);
                                                                    if (rs.next()) {
                                                                        sql = "update  photo_path set path = '" + imagePath + "'WHERE employee_id='" + info[6] + "';";
                                                                        DataBaseMangemet.ExexcuteStatement(sql);
                                                                    } else {
                                                                        sql = "insert into photo_path(employee_id,path) values('" + info[6] + "','" + imagePath + "');";
                                                                        DataBaseMangemet.ExexcuteStatement(sql);

                                                                    }

                                                                }

                                                                Action_radio(radio_buttom, g);
                                                            }
                                                        }

                                                    } catch (Exception e2) {
                                                        System.out.println("❌ Connection failed.546");
                                                        e2.printStackTrace();

                                                    }
                                                }

                                            }

                                        }
                                    }
                                } catch (Exception e2) {
                                    System.out.println("❌ Connection failed.546");
                                    e2.printStackTrace();

                                }
                            }
                        } else {
                            if (e.getSource() == b[0]) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("Choose File To Transfer");
                                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                                int result = fileChooser.showOpenDialog(null);
                                String filePath = "";
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    File selectedDirectory = fileChooser.getSelectedFile();
                                    filePath = selectedDirectory.getAbsolutePath() + File.separator + "العمال.xlsx";

                                }
                                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                                    filePath += ".xlsx";
                                }

                                try {
                                    String sql = "SELECT * FROM employee";
                                    ResultSet rs = DataBaseMangemet.select_Query(sql);

                                    XSSFWorkbook workbook = new XSSFWorkbook();
                                    XSSFSheet sheet = workbook.createSheet("employee");

                                    ResultSetMetaData meta = rs.getMetaData();
                                    int columnCount = meta.getColumnCount();

                                    FileOutputStream out = new FileOutputStream(filePath);
                                    workbook.write(out);
                                    out.close();
                                    workbook.close();

                                    System.out.println("تم التصدير بنجاح إلى: " + filePath);

                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                }
                            } else {
                                if (e.getSource() == b[1]) {

                                    if (number_text.getText().length() == 9) {
                                        try {
                                            String selectSQL = "SELECT id_employee FROM employee where id_employee = '" + number_text.getText() + "'";
                                            ResultSet rs2 = DataBaseMangemet.select_Query(selectSQL);
                                            if (rs2.next()) {
                                                selectSQL = "select activ_emp from employee where id_employee = '" + number_text.getText() + "';";
                                                rs2 = DataBaseMangemet.select_Query(selectSQL);
                                                if (rs2.next()) {
                                                    if (rs2.getString("activ_emp").equals("1")) {
                                                        Date past = new Date();
                                                        selectSQL = "select date_embauche from employee where id_employee = '" + number_text.getText() + "';";
                                                        rs2 = DataBaseMangemet.select_Query(selectSQL);
                                                        if (rs2.next()) {
                                                            past = rs2.getDate("date_embauche");
                                                        }
                                                        System.out.println(past);
                                                        final Date past2 = past;

                                                        JDateChooser jd = new JDateChooser();
                                                        String message = "حذف موظف";

                                                        Object[] params = {message, jd};
                                                        JDialog dialog = new JDialog((Frame) null, message, true);
                                                        dialog.setLayout(new GridLayout(3, 1, 0, 0));
                                                        JPanel l1 = new JPanel();
                                                        //l1.setLayout(new GridLayout(2));
                                                        JPanel l2 = new JPanel();
                                                        JPanel l3 = new JPanel();

                                                        JLabel id_label = new JLabel("تاريخ الخروج");

                                                        JLabel err = new JLabel("");
                                                        err.setForeground(Color.red);

                                                        JButton ok = new JButton(" Ok");
                                                        ok.setPreferredSize(new Dimension(70, 30));
                                                        JButton cancel = new JButton("Cancel");
                                                        cancel.setPreferredSize(new Dimension(70, 30));
                                                        cancel.setMargin(new Insets(0, 0, 0, 0));

                                                        l1.add(id_label);
                                                        l1.add(jd);

                                                        l2.add(ok);
                                                        l2.add(cancel);

                                                        l3.add(err);

                                                        dialog.add(l1);
                                                        dialog.add(l2);
                                                        dialog.add(l3);

                                                        dialog.pack();
                                                        dialog.setLocationRelativeTo(frame);
                                                        ok.addActionListener(new ActionListener() {
                                                            // @Override
                                                            public void actionPerformed(ActionEvent e) {

                                                                if (jd.getDate().after(past2)) {
                                                                    if (JOptionPane.showConfirmDialog(null, "هل أنت متأكد ", "تأكيد", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                                                                        String update = "UPDATE date_emp SET date_départ = '" + new SimpleDateFormat("yyyy-MM-dd").format(jd.getDate()) + "' where id_employee ='" + number_text.getText() + "';";
                                                                        DataBaseMangemet.ExexcuteStatement(update);
                                                                        update = "UPDATE employee SET activ_emp = 0 where id_employee = '" + number_text.getText() + "';";

                                                                        DataBaseMangemet.ExexcuteStatement(update);

                                                                        update = "UPDATE employee SET date_depart = '" + new SimpleDateFormat("yyyy-MM-dd").format(jd.getDate()) + "' where id_employee ='" + number_text.getText() + "';";
                                                                        dialog.dispose();
                                                                        DataBaseMangemet.ExexcuteStatement(update);
                                                                        System.out.println("asldkfjasldkfjas;dlkfj");
                                                                        Action_radio(radio_buttom, g);

                                                                    }
                                                                } else {
                                                                    err.setText("هذا التاريخ لايمكن إستعماله");
                                                                }
                                                            }
                                                        });
                                                        cancel.addActionListener(new ActionListener() {
                                                            public void actionPerformed(ActionEvent e) {
                                                                dialog.dispose();
                                                            }
                                                        });
                                                        dialog.setVisible(true);

                                                    }
                                                } else {
                                                    JOptionPane.showMessageDialog(null, "هذا الموظف محذوف", "حطأ", JOptionPane.ERROR_MESSAGE);
                                                }

                                            } else {
                                                JOptionPane.showMessageDialog(null, "هذا المعرف غير موجود", "حطأ", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } catch (Exception e2) {
                                            System.out.println("❌ Connection failed.546");
                                            e2.printStackTrace();

                                        }

                                    } else {
                                        JOptionPane.showMessageDialog(null, "هذا المعرف خاطئ", "حطأ", JOptionPane.ERROR_MESSAGE);
                                    }

                                }

                            }
                        }
                    }
                }
            }
        };
        b[0].addActionListener(ActionListener);
        b[1].addActionListener(ActionListener);
        b[3].addActionListener(ActionListener);
        b[2].addActionListener(ActionListener);
        b[5].addActionListener(ActionListener);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel model = table.getSelectionModel();

        model.addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        number_text.setText(String.valueOf(table.getValueAt(row, 0)));
                        name_text.setText(String.valueOf(table.getValueAt(row, 1)));
                        prenom_text.setText(String.valueOf(table.getValueAt(row, 2)));
                        try {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = (String) table.getValueAt(row, 3);
                            Date date = new Date();
                            date = df.parse(dateStr);
                            date_text.setDate(date);

                            System.out.println(String.valueOf(table.getValueAt(row, 2)));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        phone_text2.setText(String.valueOf(table.getValueAt(row, 6)));
                        state_text.setSelectedItem((String) table.getValueAt(table.getSelectedRow(), 9));
                        grad_text.setSelectedItem((String) table.getValueAt(table.getSelectedRow(), 7));
                        //salar_text.setSelectedItem(String.valueOf(table.getValueAt(row, 7)));
                        try {
                            String sql = "select path from photo_path where employee_id = '" + table.getValueAt(row, 0) + "'";
                            ResultSet rs = DataBaseMangemet.select_Query(sql);

                            if (rs.next()) {
                                imagePath = rs.getString("path");
                            } else {
                                imagePath = "";
                            }

                            profil_icon.rp();

                        } catch (Exception e2) {
                            System.out.println("❌ Connection failed.546");
                            e2.printStackTrace();
                        }

                    }

                }
            }
        }
        );

    }

    /* public static void main(String[] args) {
        Employee e = new Employee();
    }*/
}
