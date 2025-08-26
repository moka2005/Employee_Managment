


import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Absence {

    JPanel frame ;
    static pd profil_icon = new pd();
    static JTable table;
    static DefaultTableModel model = new DefaultTableModel();
    JTextField name_text;
    JTextField number_text;
    JTextField cause_text;
    JDateChooser date_text;
    JComboBox<String> state_text;
    JComboBox<String> paying_text;
   static CardLayout cardLayout;
    static JPanel cardPanel;

    Absence(CardLayout cardLayout,JPanel cardPanel) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        
        
        frame = new JPanel();
        create_interfaceGraphics();
        frame.setVisible(true);
    }
    public static void update()
    {
        try {
            model.setRowCount(0);
            String sql = "select id_employee,nom,prenom from employee where activ_emp='1' ;";
            ResultSet rs = DataBaseMangemet.select_Query(sql);
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id_employee"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("prenom"));
                model.addRow(row);
            }
        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }
    }        

    public static void edit_label(JLabel a) {
        Font f1 = new Font("Arial", Font.BOLD, 20);
        a.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        a.setOpaque(true);
        a.setFont(f1);
        a.setHorizontalAlignment(SwingConstants.CENTER);
        a.setVerticalAlignment(SwingConstants.CENTER);
        a.setMaximumSize(new Dimension(150, a.getPreferredSize().height + 11));
        a.setForeground(Color.white);
        a.setPreferredSize(new Dimension(150, 30));
        a.setBackground(new Color(10, 61, 98));
    }

    public void create_interfaceGraphics() {
        frame.setLayout(new BorderLayout());
        JPanel west = new JPanel();
        west.setLayout(new BorderLayout());
        west.setPreferredSize(new Dimension(750, 0));
        frame.add(west, BorderLayout.WEST);
        west.setBackground(new Color(10, 61, 98));

        JPanel south = new JPanel();
        south.setPreferredSize(new Dimension(0, 5));
        south.setBackground(new Color(10, 61, 98));
        frame.add(south, BorderLayout.SOUTH);

        JPanel north = new JPanel();
        north.setPreferredSize(new Dimension(0, 60));
        north.setBackground(Color.gray);
        frame.add(north, BorderLayout.NORTH);
       north.setLayout(new BoxLayout(north, BoxLayout.X_AXIS));
       
        JLabel head = new JLabel("إدخال الغيابات");
        head.setFont(new Font("Arial", Font.BOLD, 35));
       
        
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

                Employee.imagePath="";
                cardLayout.show(cardPanel, "page1");
            }
        });
        home_page.setMaximumSize(new Dimension(250,Integer.MAX_VALUE));
        north.add(home_page);
        north.add(Box.createRigidArea(new Dimension(550, 0)));
         north.add(head);

        //center-------------------------------------------------------------
        model.addColumn("رقم التعريف الوطني");
        model.addColumn("إسم الموظف");
        model.addColumn("اللقب");
        update();
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(10, 61, 98));
        JPanel center = new JPanel();

        center.setLayout(new BorderLayout());

        
        frame.add(center, BorderLayout.CENTER);
        
        center.add(scrollPane, BorderLayout.CENTER);

        //west.north--------------------------------------------------------------
        JPanel west_north = new JPanel();

        west_north.setPreferredSize(new Dimension(0, 300));
        west_north.setLayout(new BorderLayout());

        JPanel west_north_north = new JPanel();

        JPanel west_north_west = new JPanel();
        JPanel west_north_east = new JPanel();

        west_north_north.setPreferredSize(new Dimension(0, 0));
        west_north_west.setPreferredSize(new Dimension(187, 0));
        west_north_east.setPreferredSize(new Dimension(187, 0));
        profil_icon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        profil_icon.setBackground(new Color(10, 61, 98));

        west_north.add(west_north_north, BorderLayout.NORTH);
        west_north.add(west_north_west, BorderLayout.WEST);
        west_north.add(west_north_east, BorderLayout.EAST);
        west_north.add(profil_icon, BorderLayout.CENTER);

        west.add(west_north, BorderLayout.NORTH);

        west_north_north.setBackground(new Color(10, 61, 98));
        west_north_east.setBackground(new Color(10, 61, 98));
        west_north_west.setBackground(new Color(10, 61, 98));

        // west center ----------------------------------------------------
        JPanel west_center = new JPanel();
        west_center.setLayout(new BoxLayout(west_center, BoxLayout.Y_AXIS));
        west_center.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel p1 = new JPanel();

        p1.setBackground(new Color(10, 61, 98));
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel name_emp = new JLabel("إسم  الموظف");

        edit_label(name_emp);
        //p1.add(Box.createHorizontalGlue());
        p1.add(Box.createRigidArea(new Dimension(150, 0)));
        p1.add(name_emp);

        JLabel number_emp = new JLabel("رقم الموظف");

        edit_label(number_emp);
        //p1.add(Box.createHorizontalGlue());
        p1.add(Box.createRigidArea(new Dimension(150, 0)));
        p1.add(number_emp);

        west_center.add(p1);
        west_center.setBackground(new Color(10, 61, 98));
        west.add(west_center, BorderLayout.CENTER);
        west_center.add(Box.createRigidArea(new Dimension(0, -5)));
        //p2-------------------------------------------------------------------------------     
        JPanel p2 = new JPanel();
        p2.setBackground(new Color(10, 61, 98));

        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        name_text = new JTextField();
        number_text = new JTextField();

        name_text.setFont(f2);
        number_text.setFont(f2);

        name_text.setMaximumSize(new Dimension(220, 30));
        number_text.setMaximumSize(new Dimension(220, 30));

        name_text.setHorizontalAlignment(JTextField.CENTER);
        number_text.setHorizontalAlignment(JTextField.CENTER);
        p2.add(Box.createRigidArea(new Dimension(110, 0)));
        p2.add(name_text);
        p2.add(Box.createRigidArea(new Dimension(80, 0)));
        p2.add(number_text);

        west_center.add(p2);
        west_center.add(Box.createRigidArea(new Dimension(0, -5)));

        //p3------------------------------------------------------------------------
        JPanel p3 = new JPanel();
        p3.setBackground(new Color(10, 61, 98));

        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton res = new JButton("بحث");
        res.setFocusPainted(false);
        res.setBackground(new Color(80, 80, 80));
        p3.add(Box.createRigidArea(new Dimension(200, 0)));
        p3.add(res);

        JButton res2 = new JButton("بحث");
        res2.setFocusPainted(false);
        res2.setBackground(new Color(80, 80, 80));
        p3.add(Box.createRigidArea(new Dimension(230, 0)));
        p3.add(res2);
        west_center.add(p3);

        //p4 -----------------------------------------------------------------------
        JPanel p4 = new JPanel();
        p4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p4.setBackground(new Color(10, 61, 98));
        p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
        JLabel date_ap = new JLabel("تاريخ الغياب");
        edit_label(date_ap);
        JLabel cause = new JLabel("سبب الغياب");
        edit_label(cause);

        p4.add(Box.createRigidArea(new Dimension(150, 0)));
        p4.add(date_ap);
        

        

        p4.add(Box.createRigidArea(new Dimension(150, 0)));
        p4.add(cause);
        west_center.add(p4);

        //p5 ------------------------------------------------------------------------
        JPanel p5 = new JPanel();
        p5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p5.setBackground(new Color(10, 61, 98));
        p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
        p5.setBackground(new Color(10, 61, 98));

        date_text = new JDateChooser();
        
        date_text.setFont(f2);
        date_text.setMaximumSize(new Dimension(220, 30));
        date_text.setDateFormatString("yyyy-MM-d");

        p5.add(Box.createRigidArea(new Dimension(110, 0)));
        p5.add(date_text);

      

       
       
        
       

        
       

        cause_text = new JTextField();
        cause_text.setColumns(15);
        cause_text.setFont(f2);
        cause_text.setMaximumSize(new Dimension(220, 30));
        cause_text.setHorizontalAlignment(JTextField.CENTER);
        
        p5.add(Box.createRigidArea(new Dimension(80, 0)));
        p5.add(cause_text);
          west_center.add(p5);
        
        //p6-----------------------------------------------------------
        JPanel p6 = new JPanel();
        p6.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p6.setBackground(new Color(10, 61, 98));
        p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
        p6.setBackground(new Color(10, 61, 98));
        
        JLabel state = new JLabel("الحالة");
        edit_label(state);
        
        JLabel paying = new JLabel("الدفع");
        edit_label(paying);
        
        p6.add(Box.createRigidArea(new Dimension(150, 0)));
        p6.add(state);
        p6.add(Box.createRigidArea(new Dimension(150, 0)));
        p6.add(paying);
        west_center.add(p6);

        //p7-------------------------------------------------------------------
        JPanel p7 = new JPanel();
         p7.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p7.setBackground(new Color(10, 61, 98));
        p7.setLayout(new BoxLayout(p7, BoxLayout.X_AXIS));
        p7.setBackground(new Color(10, 61, 98));
        String grad_value[] = {"حاضر", "غائب"};

        state_text = new JComboBox<>(grad_value);
        state_text.setFont(f2);
        state_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) state_text.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

        
        state_text.setMaximumSize(new Dimension(220, 30));

        state_text.setPreferredSize(new Dimension(169, 22));
        p7.add(Box.createRigidArea(new Dimension(110, 0)));
        p7.add(state_text);
        String paying_value[] = {"مدفوع", "غير مدفوع"};

        paying_text = new JComboBox<>(paying_value);
        paying_text.setFont(f2);
        paying_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ((JLabel) paying_text.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);

        
        paying_text.setMaximumSize(new Dimension(220, 30));

        paying_text.setPreferredSize(new Dimension(169, 22));
        p7.add(Box.createRigidArea(new Dimension(80, 0)));
        p7.add(paying_text);
        west_center.add(p7);
        //p8 ------------------------------------------------------------------------
        JPanel p8 = new JPanel();
        p8.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p8.setBackground(new Color(10, 61, 98));
        p8.setLayout(new BoxLayout(p8, BoxLayout.X_AXIS));
        p8.setBackground(new Color(10, 61, 98));

        ImageIcon icon2 = new ImageIcon("/home/mokhtar-mammeri/Desktop/BME2/icons/include.png");
        JButton add = new JButton("إدخال الغياب", icon2);

        add.setFocusPainted(false);
        add.setFont(new Font("Arial", Font.BOLD, 15));
        add.setBackground(Color.white);
        add.setForeground(Color.red);
        add.setMaximumSize(new Dimension(170, 50));

        p8.add(Box.createRigidArea(new Dimension(280, 0)));
        p8.add(add);
        west_center.add(Box.createRigidArea(new Dimension(0, 20)));
        west_center.add(p8);

        Action(res, res2, add);

    }

    public void Action(JButton b1, JButton b2, JButton add) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel model2 = table.getSelectionModel();

        model2.addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        number_text.setText(String.valueOf(table.getValueAt(row, 0)));
                        name_text.setText(String.valueOf(table.getValueAt(row, 1)));
                         try {
                            String sql = "select path from photo_path where employee_id = '" + table.getValueAt(row, 0) + "'";
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
                    }

                }
            }
        });
        ActionListener a = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == b1) {
                    if (name_text.getText().length() > 0) {
                        try {

                            String sql = "SELECT nom,id_employee,prenom FROM employee "
                                    + "WHERE nom LIKE '%" + name_text.getText() + "%';";

                            ResultSet rs = DataBaseMangemet.select_Query(sql);

                            model.setRowCount(0);
                            while (rs.next()) {
                                Vector<String> row = new Vector<>();
                                row.add(rs.getString("id_employee"));
                                row.add(rs.getString("nom"));
                                row.add(rs.getString("prenom"));
                                model.addRow(row);
                            }
                        } catch (SQLException e2) {
                            System.out.println(e2.getMessage());
                        }
                    } else {
                        try {
                            model.setRowCount(0);

                            String sql = "select id_employee,nom,prenom from employee;";
                            ResultSet rs = DataBaseMangemet.select_Query(sql);
                            while (rs.next()) {
                                Vector<String> row = new Vector<>();
                                row.add(rs.getString("id_employee"));
                                row.add(rs.getString("nom"));
                                row.add(rs.getString("prenom"));
                                model.addRow(row);
                            }
                        } catch (SQLException e2) {
                            System.out.println(e2.getMessage());
                        }
                    }
                } else {
                    if (e.getSource() == b2) {
                        if (number_text.getText().length() > 0) {
                            try {

                                String sql = "SELECT nom,id_employee,prenom FROM employee "
                                        + "WHERE id_employee::text LIKE '%" + number_text.getText() + "%';";

                                ResultSet rs = DataBaseMangemet.select_Query(sql);
                                model.setRowCount(0);
                                while (rs.next()) {
                                    Vector<String> row = new Vector<>();
                                    row.add(rs.getString("id_employee"));
                                    row.add(rs.getString("nom"));
                                    row.add(rs.getString("prenom"));
                                    model.addRow(row);
                                }
                            } catch (SQLException e2) {
                                System.out.println(e2.getMessage());
                            }
                        } else {
                            try {
                                model.setRowCount(0);
                                String sql = "select id_employee,nom,prenom from employee;";
                                ResultSet rs = DataBaseMangemet.select_Query(sql);
                                while (rs.next()) {
                                    Vector<String> row = new Vector<>();
                                    row.add(rs.getString("id_employee"));
                                    row.add(rs.getString("nom"));
                                    row.add(rs.getString("prenom"));
                                    model.addRow(row);
                                }
                            } catch (SQLException e2) {
                                System.out.println(e2.getMessage());
                            }
                        }

                    } else {
                        if (e.getSource() == add) {
                            if (date_text.getDate() == null) {
                                JOptionPane.showMessageDialog(null, "يرجى إدخال تاريخ الغياب", "خطأ", JOptionPane.ERROR_MESSAGE);
                            } else {
                                if (number_text.getText().isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "يرجى إدخال رقم التعريف الوطني ", "خطأ", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    try {
                                        if (number_text.getText().matches("[0-9]+")) {
                                            String sql = "select id_employee from employee where id_employee='" + number_text.getText() + "';";
                                            ResultSet rs = DataBaseMangemet.select_Query(sql);
                                            if (rs.next()) {
                                                if(state_text.getSelectedItem().equals("حاضر"))
                                                {
                                                    sql = "insert into absences (id_employee,absence_date,state,paying_state) values('" + number_text.getText() + "','" + date_text.getDate() + "','"+state_text.getSelectedItem()+"','"+paying_text.getSelectedItem()+"');";
                                                    cause_text.setForeground(Color.red);
                                                    DataBaseMangemet.ExexcuteStatement(sql);
                                                }
                                                else
                                                {
                                                    sql = "insert into absences (id_employee,absence_date,cause,state,paying_state) values('" + number_text.getText() + "','" + cause_text.getText() + "','" + date_text.getDate() + "','"+state_text.getSelectedItem()+"','"+paying_text.getSelectedItem()+"');";
                                                    cause_text.setForeground(Color.red);
                                                    DataBaseMangemet.ExexcuteStatement(sql);
                                                }    
                                                
                                                if(JOptionPane.showConfirmDialog(null, "هل أنت متأكد", "تأكيد",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                                                {
                                                    JOptionPane.showMessageDialog(null, "تم إاضافة الغياب بنجاح", "", JOptionPane.INFORMATION_MESSAGE);
                                                }    
                                            }
                                            else
                                            {
                                                JOptionPane.showMessageDialog(null, " رقم التعريف الوطني غير موجود", "خطأ", JOptionPane.ERROR_MESSAGE);
                                            }    
                                        }
                                        else
                                        {
                                            JOptionPane.showMessageDialog(null, "يرجى إدخال رقم التعريف الوطني  صحبح", "خطأ", JOptionPane.ERROR_MESSAGE);
                                        }    

                                    } catch (SQLException e3) {
                                        System.out.println(e3.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
        b1.addActionListener(a);
        b2.addActionListener(a);
        add.addActionListener(a);
    }

   /* public static void main(String[] args) {
        Absence a = new Absence();
    }*/

}
