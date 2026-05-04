
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Flow_Absence {

    JPanel frame;
    static pd profil_icon = new pd();
    static JTable table;
    static DefaultTableModel model = new DefaultTableModel();
    static JTextField number_text;
    static JDateChooser date_text1;
    static JDateChooser date_text2;
    static CardLayout cardLayout;
    static JPanel cardPanel;
    static JComboBox<String> state_text;
    static JComboBox<String> id_combo;
    static JComboBox<String> pay_combo;
    static JLabel a;

    Flow_Absence(CardLayout cardLayout, JPanel cardPanel) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        frame = new JPanel();
        frame.setLayout(new BorderLayout());

        create_interfaceGraphics();
        frame.setVisible(true);
    }

    public void edit_label(JLabel a) {
        Font f1 = new Font("Arial", Font.BOLD, 20);
        a.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        a.setOpaque(true);
        a.setFont(f1);
        a.setHorizontalAlignment(SwingConstants.CENTER);
        a.setVerticalAlignment(SwingConstants.CENTER);
        a.setMaximumSize(new Dimension(130, a.getPreferredSize().height));
        a.setForeground(Color.white);
        a.setBackground(new Color(64, 81, 99));

    }

    public void Action(JButton res, JButton filter, JButton pay) {
        ActionListener a = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == res) {
                    if (id_combo.getItemCount() == 0) {
                        JOptionPane.showMessageDialog(null, "ليس هناك أي موظف", "خطأ", JOptionPane.ERROR_MESSAGE);
                    } else {

                        update(true);
                    }

                } else {
                    if (e.getSource() == filter) {

                        if (date_text1.getDate() == null) {
                            JOptionPane.showMessageDialog(null, "يرجى إدخال تاريخ البداية", "خطأ", JOptionPane.ERROR_MESSAGE);
                        } else {
                            if (date_text2.getDate() == null) {
                                JOptionPane.showMessageDialog(null, "يرجى إدخال تاريخ النهاية", "خطأ", JOptionPane.ERROR_MESSAGE);
                            } else {
                                update(false);
                            }
                        }

                    } else {
                        if (e.getSource() == pay) {
                            int index = pay_combo.getSelectedIndex() + 1;
                            System.out.println(index);
                            int salair = 0;
                            try {

                                
                                String sql = "select salaire from employee where id_employee = '" + id_combo.getSelectedItem() + "' ; ";
                                ResultSet rs = DataBaseMangemet.select_Query(sql);
                                if (rs.next()) {
                                    salair = Integer.parseInt(rs.getString("salaire"));
                                }
                               
                                sql = "select e.id_employee,e.nom,a.state,a.paying_state,e.prenom,a.absence_date,a.absence_id,a.reason from employee e "
                                        + "join absences a on a.id_employee=e.id_employee"
                                        + " where e.activ_emp='1' and e.id_employee='" + id_combo.getSelectedItem() + "' order by a.absence_date;";
                                rs = DataBaseMangemet.select_Query(sql);

                                while (rs.next() && index>0) { 
                                    if(rs.getString("paying_state").equals("غير مدفوع"))
                                    {
                                        sql = "UPDATE absences SET paying_state = '" + "مدفوع" + "'  WHERE id_employee= '" + id_combo.getSelectedItem() + "' and absence_id ='" + rs.getString("absence_id") + "' ;";
                                        DataBaseMangemet.ExexcuteStatement(sql);
                                        
                                        index--;  
                                    }    
                                    
                                }

                            } catch (SQLException e2) {
                                System.out.println(e2.getMessage());
                            }
                            update(true);

                        }
                    }
                }

            }

        };
        res.addActionListener(a);
        filter.addActionListener(a);
        pay.addActionListener(a);
    }

    public static void update(boolean i) {

        try {
            String sql = "select path from photo_path where employee_id = '" + id_combo.getSelectedItem() + "'";
            ResultSet rs = DataBaseMangemet.select_Query(sql);

            if (rs.next()) {
                Employee.imagePath = rs.getString("path");
            } else {
                Employee.imagePath = "";
            }

            profil_icon.rp();

        } catch (Exception e2) {
            System.out.println("❌ Connection failed.546");
            e2.printStackTrace();
        }
        int salair = 0;
        try {

            int some = 0;
            String sql = "select salaire from employee where id_employee = '" + id_combo.getSelectedItem() + "' ; ";
            ResultSet rs = DataBaseMangemet.select_Query(sql);
            if (rs.next()) {
                salair = Integer.parseInt(rs.getString("salaire"));
       
            }
            model.setRowCount(0);
            sql = "select e.id_employee,e.nom,a.state,a.paying_state,e.prenom,a.absence_date,a.reason from employee e "
                    + "join absences a on a.id_employee=e.id_employee"
                    + " where e.activ_emp='1' and e.id_employee='" + id_combo.getSelectedItem() + "' order by a.absence_date;";
            rs = DataBaseMangemet.select_Query(sql);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("reason"));
                row.add(rs.getString("paying_state"));
                row.add(rs.getString("state"));
                row.add(rs.getString("absence_date"));
                row.add(rs.getString("prenom"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("id_employee"));
                if (rs.getString("state").equals("حاضر") && rs.getString("paying_state").equals("غير مدفوع")) {
                    some += salair;
                   
                }
                if (i == true) {

                    model.addRow(row);

                } else {
                    if ((date_text1.getDate().before(rs.getDate("absence_date")) || date_text1.getDate().equals(rs.getDate("absence_date"))) && (date_text2.getDate().after(rs.getDate("absence_date")) || date_text1.getDate().equals(rs.getDate("absence_date"))) && rs.getString("state").equals(state_text.getSelectedItem())) {
                        model.addRow(row);
                    }
                }

            }
            a.setText(String.valueOf(some) + "DA");

        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }
        try {

            String sql;
            ResultSet rs;

            sql = "select e.id_employee,e.nom,a.state,a.paying_state,e.prenom,a.absence_date,a.reason from employee e "
                    + "join absences a on a.id_employee=e.id_employee"
                    + " where e.activ_emp='1' and e.id_employee='" + id_combo.getSelectedItem() + "' and paying_state='" + "غير مدفوع" + "' order by a.absence_date;";
            rs = DataBaseMangemet.select_Query(sql);
            int j = 1;
            pay_combo.removeAllItems();
            int h =0;
            while (rs.next()) {
                h += salair;
                pay_combo.addItem(Integer.toString(j) + " يوم " + " (" + h + " DA) ");     
                j++;
            }
             if (h==0) {
                 pay_combo.addItem(Integer.toString(h) + " يوم " + " (" + h + " DA) ");
            }

        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }

    }

    public void create_interfaceGraphics() {
        JPanel north = new JPanel();
        JPanel center = new JPanel();
        JPanel west = new JPanel();
        west.setBackground(new Color(10, 61, 98));
        //north ---------------
        north.setPreferredSize(new Dimension(0, 60));
        north.setBackground(Color.gray);
        frame.add(north, BorderLayout.NORTH);
        north.setLayout(new BoxLayout(north, BoxLayout.X_AXIS));

        JLabel head = new JLabel("متابعة الحضور");
        head.setFont(new Font("Arial", Font.BOLD, 35));

        ImageIcon icon = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/home.png");
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
        home_page.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));
        north.add(home_page);
        north.add(Box.createRigidArea(new Dimension(565, 0)));
        north.add(head);

        west.setPreferredSize(new Dimension(700, 0));

        //west-----------------------------------------------------------------
        west.setLayout(new BorderLayout());

        JPanel west_south_north = new JPanel();
        JPanel west_north = new JPanel();

        west_north.setPreferredSize(new Dimension(0, 150));

        JPanel west_north_west = new JPanel();
        JPanel west_north_east = new JPanel();
        profil_icon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        profil_icon.setBackground(new Color(10, 61, 98));

        west_north.setLayout(new BorderLayout());
        west_north_west.setPreferredSize(new Dimension(150, 0));
        west_north_east.setPreferredSize(new Dimension(150, 0));

        west_north_east.setBackground(new Color(10, 61, 98));
        west_north_west.setBackground(new Color(10, 61, 98));

        west_north.add(profil_icon, BorderLayout.CENTER);
        west.add(west_south_north, BorderLayout.CENTER);

        west.add(west_north, BorderLayout.NORTH);
        west_north.add(west_north_west, BorderLayout.WEST);
        west_north.add(west_north_east, BorderLayout.EAST);

        frame.add(west, BorderLayout.WEST);
        frame.add(north, BorderLayout.NORTH);

        //west south-------------------------------------------
        west_south_north.setLayout(new BorderLayout());

        Font f2 = new Font("Arial", Font.BOLD, 15);
        JPanel west_south = new JPanel();
        west_south.setLayout(new BoxLayout(west_south, BoxLayout.Y_AXIS));
        west_south.setPreferredSize(new Dimension(0, 200));
        west_south.setBackground(new Color(10, 61, 98));

        west_south_north.add(west_south, BorderLayout.NORTH);
        //p1-----------------------------------------------------
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(10, 61, 98));
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        id_combo = new JComboBox<>();

        try {
            model.setRowCount(0);

            String sql = "select id_employee from employee where activ_emp = '1';";
            ResultSet rs = DataBaseMangemet.select_Query(sql);
            while (rs.next()) {

                id_combo.addItem(rs.getString("id_employee"));
            }
        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }

        id_combo.setFont(f2);
        id_combo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) id_combo.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

        id_combo.setMaximumSize(new Dimension(220, 30));
        id_combo.setPreferredSize(new Dimension(169, 22));

        JLabel number = new JLabel("رقم الموظف");
        edit_label(number);
        p1.add(number);

        JButton res = new JButton("بحث");

        //res.setFocusPainted(false);
        //res.setBackground(new Color(80, 80, 80));
        res.setFont(f2);

        p1.add(Box.createRigidArea(new Dimension(105, 0)));
        p1.add(res);
        p1.add(Box.createRigidArea(new Dimension(10, 0)));
        p1.add(id_combo);
        p1.add(Box.createRigidArea(new Dimension(10, 0)));
        p1.add(number);

        west_south.add(p1);

        //p2-----------------------------------------------------
        JPanel p2 = new JPanel();
        p2.setBackground(new Color(10, 61, 98));
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        // p2.add(Box.createRigidArea(new Dimension(150, 0)));
        JLabel date_begin = new JLabel("تاريخ البداية");
        edit_label(date_begin);
        JLabel date_fin = new JLabel("تاريخ النهاية");
        edit_label(date_fin);

        date_text1 = new JDateChooser();
        date_text1.setFont(f2);
        date_text1.setMaximumSize(new Dimension(220, 30));
        date_text1.setDateFormatString("yyyy-MM-d");

        date_text2 = new JDateChooser();
        date_text2.setFont(f2);
        date_text2.setMaximumSize(new Dimension(220, 30));
        date_text2.setDateFormatString("yyyy-MM-d");

        p2.add(Box.createRigidArea(new Dimension(10, 0)));
        p2.add(date_text2);
        p2.add(Box.createRigidArea(new Dimension(10, 0)));
        p2.add(date_fin);
        p2.add(Box.createRigidArea(new Dimension(10, 0)));
        p2.add(date_text1);
        p2.add(Box.createRigidArea(new Dimension(10, 0)));
        p2.add(date_begin);
        p2.add(Box.createRigidArea(new Dimension(10, 0)));

        west_south.add(p2);

        //p3----------------------------------------------
        JPanel p3 = new JPanel();
        p3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p3.setBackground(new Color(10, 61, 98));
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setBackground(new Color(10, 61, 98));

        ImageIcon icon3 = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/filter.png");
        JButton filter = new JButton("تصفية      ", icon3);

        filter.setFocusPainted(false);
        filter.setFont(new Font("Arial", Font.BOLD, 15));
        filter.setBackground(Color.white);
        filter.setForeground(Color.red);
        filter.setMaximumSize(new Dimension(170, 35));

        p3.add(Box.createRigidArea(new Dimension(150, 0)));
        p3.add(filter);

        String grad_value[] = {"حاضر", "غائب"};

        state_text = new JComboBox<>(grad_value);
        state_text.setFont(f2);
        state_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) state_text.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

        state_text.setMaximumSize(new Dimension(220, 30));
        state_text.setPreferredSize(new Dimension(169, 22));
        p3.add(Box.createRigidArea(new Dimension(10, 0)));
        p3.add(state_text);

        JLabel state = new JLabel("الحالة");
        edit_label(state);
        p3.add(Box.createRigidArea(new Dimension(10, 0)));
        p3.add(state);
        west_south.add(p3);

        //p4 ------------------------------------------------------------------------
        JPanel p4 = new JPanel();
        p4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p4.setBackground(new Color(10, 61, 98));
        p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
        p4.setBackground(new Color(10, 61, 98));

        ImageIcon icon2 = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/delete.png");
        JButton delete = new JButton("حذف البيان    ", icon2);

        delete.setFocusPainted(false);
        delete.setFont(new Font("Arial", Font.BOLD, 15));
        delete.setBackground(Color.white);
        delete.setForeground(Color.red);
        delete.setMaximumSize(new Dimension(170, 42));

        ImageIcon icon4 = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/excel.png");
        JButton extract = new JButton("إستخراج    ", icon4);

        extract.setFocusPainted(false);
        extract.setFont(new Font("Arial", Font.BOLD, 15));
        extract.setBackground(Color.white);
        extract.setForeground(Color.red);
        extract.setMaximumSize(new Dimension(170, 42));

        p4.add(Box.createRigidArea(new Dimension(160, 0)));
        p4.add(delete);
        p4.add(Box.createRigidArea(new Dimension(20, 0)));
        p4.add(extract);

        west_south.add(Box.createRigidArea(new Dimension(0, 10)));
        west_south.add(p4);

        //p5 ------------------------------------------------------------------------
        JPanel p5 = new JPanel();

        p5.setBackground(new Color(10, 61, 98));
        p5.setLayout(new BoxLayout(p5, BoxLayout.Y_AXIS));
        p5.setBackground(new Color(10, 61, 98));
        p5.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Font f1 = new Font("Arial", Font.BOLD, 20);

        //title -----------------------------------
        JPanel p5_title = new JPanel();
        p5_title.setLayout(new BorderLayout());
        p5_title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p5_title.setBackground(new Color(10, 61, 98));

        JLabel title = new JLabel("المستحقات");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        p5_title.add(title, BorderLayout.CENTER);

        p5.add(p5_title);

        west_south_north.add(p5, BorderLayout.CENTER);

        //----------
        JPanel pp2 = new JPanel();
        pp2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pp2.setBackground(new Color(10, 61, 98));
        pp2.setLayout(new BoxLayout(pp2, BoxLayout.X_AXIS));
        pp2.setBackground(new Color(10, 61, 98));

        JLabel b = new JLabel("الدين  :");
        edit_label(b);

        a = new JLabel("DA");
        
        //a.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        a.setOpaque(true);
        a.setFont(new Font("Arial", Font.BOLD, 20));
        a.setHorizontalAlignment(SwingConstants.CENTER);
        a.setVerticalAlignment(SwingConstants.CENTER);
        a.setMaximumSize(new Dimension(130, a.getPreferredSize().height));
        a.setForeground(Color.RED);
        a.setBackground(new Color(10, 61, 98));

        pp2.add(Box.createRigidArea(new Dimension(220, 0)));
        pp2.add(a);
        pp2.add(Box.createRigidArea(new Dimension(10, 0)));
        pp2.add(b);

        p5.add(Box.createRigidArea(new Dimension(0, 10)));
        p5.add(pp2);

        //pp3------------------------------------------------------------------
        JPanel pp3 = new JPanel();
        pp3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pp3.setBackground(new Color(10, 61, 98));
        pp3.setLayout(new BoxLayout(pp3, BoxLayout.X_AXIS));
        pp3.setBackground(new Color(10, 61, 98));

        ImageIcon icon20 = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/pay.png");
        JButton pay = new JButton("مدفوع    ", icon20);

        pay.setFocusPainted(false);
        pay.setFont(new Font("Arial", Font.BOLD, 15));
        pay.setBackground(Color.white);
        pay.setForeground(Color.red);
        pay.setMaximumSize(new Dimension(170, 42));

        pp3.add(Box.createRigidArea(new Dimension(70, 0)));
        pp3.add(pay);

        pay_combo = new JComboBox<String>();

        pay_combo.setFont(f2);
        pay_combo.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) pay_combo.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

        pay_combo.setMaximumSize(new Dimension(220, 30));
        pay_combo.setPreferredSize(new Dimension(169, 22));

        try {

            int salair = 0;

            String sql = "select salaire from employee where id_employee = '" + id_combo.getSelectedItem() + "' ; ";
            ResultSet rs = DataBaseMangemet.select_Query(sql);
            if (rs.next()) {
                salair = Integer.parseInt(rs.getString("salaire"));
            }

            model.setRowCount(0);
            sql = "select e.id_employee,e.nom,a.state,a.paying_state,e.prenom,a.absence_date,a.reason from employee e "
                    + "join absences a on a.id_employee=e.id_employee"
                    + " where e.activ_emp='1' and e.id_employee='" + id_combo.getSelectedItem() + "' and paying_state='" + "غير مدفوع" + "' order by a.absence_date;";
            rs = DataBaseMangemet.select_Query(sql);
            int i = 0;
            int h = 0;
            while (rs.next()) {
                i++;
                pay_combo.addItem(Integer.toString(i) + " يوم " + " (" + h + " DA) ");
                h += salair;
            }
            if (i == 0) {
                pay_combo.addItem(Integer.toString(i) + " يوم " + " (" + h+ " DA) ");
            }
        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }
        JLabel lev = new JLabel("دفع :");
        edit_label(lev);
        pp3.add(Box.createRigidArea(new Dimension(5, 0)));
        pp3.add(pay_combo);
        pp3.add(Box.createRigidArea(new Dimension(5, 0)));
        pp3.add(lev);
        p5.add(pp3);
        //center ---------------------------------------------------------------
        model.addColumn("سبب  الغياب");
        model.addColumn("الدفع");
        model.addColumn("الحالة");
        model.addColumn("التاريخ");
        model.addColumn("اللقب");
        model.addColumn("إسم الموظف");
        model.addColumn("رقم التعريف الوطني");

        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }
        table.getTableHeader().setReorderingAllowed(false);

        table.setRowHeight(40);
        table.setFont(f2);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        JTableHeader header = table.getTableHeader();
        header.setFont(f2);
        header.setBackground(Color.BLUE);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(10, 61, 98));

        center.setLayout(new BorderLayout());

        frame.add(scrollPane, BorderLayout.CENTER);

        Action(res, filter, pay);

    }

}
