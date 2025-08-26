
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author mokhtar-mammeri
 */
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
    static JLabel a ;

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
        a.setMaximumSize(new Dimension(150, a.getPreferredSize().height + 11));
        a.setForeground(Color.white);
        a.setPreferredSize(new Dimension(150, 30));
        a.setBackground(new Color(10, 61, 98));
    }

    public void Action(JButton res, JButton clear) {
        ActionListener a = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == res) {
                    if (number_text.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "يرجى إدخال رقم الموظف", "خطأ", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if(number_text.getText().matches("[0-9]+"))
                        {
                            update(true);
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "يرجى إدخال رقم التعريف الوطني  صحبح", "خطأ", JOptionPane.ERROR_MESSAGE);
                        }    
                        
                    }

                } else {
                    if (e.getSource() == clear) {
                        if (number_text.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "يرجى إدخال رقم الموظف", "خطأ", JOptionPane.ERROR_MESSAGE);
                        } else {
                            if (number_text.getText().matches("[0-9]+")) {
                                if (date_text1.getDate() == null) {
                                    JOptionPane.showMessageDialog(null, "يرجى إدخال تاريخ البداية", "خطأ", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    if (date_text2.getDate() == null) {
                                        JOptionPane.showMessageDialog(null, "يرجى إدخال تاريخ النهاية", "خطأ", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        update(false);
                                    }
                                }
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(null, "يرجى إدخال رقم التعريف الوطني  صحبح", "خطأ", JOptionPane.ERROR_MESSAGE);
                            }    

                        }
                    }
                }

            }
        };
        res.addActionListener(a);
        clear.addActionListener(a);
    }

    public static void update(boolean i) {

        try {
            String sql = "select path from photo_path where employee_id = '" + number_text.getText() + "'";
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
        try {
             System.out.println("hell");
            int salair=0;
            int some = 0;
            String sql = "select salaire from employee where id_employee = '" + number_text.getText() + "' ; ";
            ResultSet rs = DataBaseMangemet.select_Query(sql);
            if(rs.next())salair=Integer.parseInt(rs.getString("salaire"));
            model.setRowCount(0);
            sql = "select e.id_employee,e.nom,a.state,a.paying_state,e.prenom,a.absence_date,a.reason  from employee e "
                    + "join absences a on a.id_employee=e.id_employee"
                    + " where e.activ_emp='1' and e.id_employee='" + number_text.getText() + "' ;";
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
                if(rs.getString("state").equals("حاضر") &&rs.getString("paying_state").equals("غير مدفوع") )some+=salair;
                if (i == true) {
                    
                    model.addRow(row);
                    
                } else {
                    if ((date_text1.getDate().before(rs.getDate("absence_date")) || date_text1.getDate().equals(rs.getDate("absence_date"))) && (date_text2.getDate().after(rs.getDate("absence_date")) || date_text1.getDate().equals(rs.getDate("absence_date")))&&rs.getString("state").equals(state_text.getSelectedItem())) {
                        model.addRow(row);
                    }
                }

            }
            a.setText(String.valueOf(some)+"DA"); 
            
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

        JLabel head = new JLabel("متابعة الغيابات");
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

        JPanel west_south = new JPanel();
        JPanel west_north = new JPanel();

        west_north.setPreferredSize(new Dimension(0, 220));

        JPanel west_north_west = new JPanel();
        JPanel west_north_east = new JPanel();
        profil_icon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        profil_icon.setBackground(new Color(10, 61, 98));

        west_north.setLayout(new BorderLayout());
        west_north_west.setPreferredSize(new Dimension(150, 0));
        west_north_east.setPreferredSize(new Dimension(150, 0));

        west_north_east.setBackground(new Color(10, 61, 98));
        west_north_west.setBackground(new Color(10, 61, 98));

        west_north.add(profil_icon, BorderLayout.CENTER);
        west.add(west_south, BorderLayout.CENTER);

        west.add(west_north, BorderLayout.NORTH);
        west_north.add(west_north_west, BorderLayout.WEST);
        west_north.add(west_north_east, BorderLayout.EAST);

        frame.add(west, BorderLayout.WEST);
        frame.add(north, BorderLayout.NORTH);

        //west south-------------------------------------------
        Font f2 = new Font("Arial", Font.BOLD, 15);
        west_south.setLayout(new BoxLayout(west_south, BoxLayout.Y_AXIS));
        //p1-----------------------------------------------------
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(10, 61, 98));
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        p1.add(Box.createRigidArea(new Dimension(280, 0)));
        JLabel number = new JLabel("رقم الموظف");
        edit_label(number);
        p1.add(number);
        west_south.add(Box.createRigidArea(new Dimension(0, 20)));
        west_south.add(p1);

        //p2---------------------------------------------------------
        JPanel p2 = new JPanel();
        p2.setBackground(new Color(10, 61, 98));
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        number_text = new JTextField();
        number_text.setMaximumSize(new Dimension(220, 30));
        number_text.setFont(f2);
        p2.add(Box.createRigidArea(new Dimension(240, 0)));
        p2.add(number_text);
        west_south.add(p2);

        //p3------------------------------------------------------------------------
        JPanel p3 = new JPanel();
        p3.setBackground(new Color(10, 61, 98));

        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton res = new JButton("بحث");
        res.setFocusPainted(false);
        res.setBackground(new Color(80, 80, 80));
        p3.add(Box.createRigidArea(new Dimension(325, 0)));
        
        p3.add(res);
        west_south.add(p3);

        //p4-----------------------------------------------------
        JPanel p4 = new JPanel();
        p4.setBackground(new Color(10, 61, 98));
        p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
        p4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        p4.add(Box.createRigidArea(new Dimension(150, 0)));
        JLabel date_begin = new JLabel("تاريخ البداية");
        edit_label(date_begin);
        p4.add(date_begin);
        west_south.add(Box.createRigidArea(new Dimension(0, 20)));
        p4.add(Box.createRigidArea(new Dimension(150, 0)));
        JLabel date_fin = new JLabel("تاريخ النهاية");
        edit_label(date_fin);
        p4.add(date_fin);
        west_south.add(p4);

        //p5 ------------------------------------------------------------------------
        JPanel p5 = new JPanel();
        p5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p5.setBackground(new Color(10, 61, 98));
        p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
        p5.setBackground(new Color(10, 61, 98));

        date_text1 = new JDateChooser();

        date_text1.setFont(f2);
        date_text1.setMaximumSize(new Dimension(220, 30));
        date_text1.setDateFormatString("yyyy-MM-d");

        p5.add(Box.createRigidArea(new Dimension(110, 0)));
        p5.add(date_text1);
        west_south.add(Box.createRigidArea(new Dimension(0, 5)));
        

       
        
        
   

        

        date_text2 = new JDateChooser();

        date_text2.setFont(f2);
        date_text2.setMaximumSize(new Dimension(220, 30));
        date_text2.setDateFormatString("yyyy-MM-d");

        p5.add(Box.createRigidArea(new Dimension(80, 0)));
        p5.add(date_text2);

        west_south.add(p5);
        
        //p6
          JPanel p6 = new JPanel();
         p6.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p6.setBackground(new Color(10, 61, 98));
        p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
        p6.setBackground(new Color(10, 61, 98));
        
        JLabel state = new JLabel("الحالة");
        edit_label(state);
        p6.add(Box.createRigidArea(new Dimension(150, 0)));
        p6.add(state);
         west_south.add(p6);
        //p7--------------------------------------------------------------------
        
        
        
        
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
        a = new JLabel("الرصيد");
        
        Font f1 = new Font("Arial", Font.BOLD, 20);
        a.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        a.setOpaque(true);
        a.setFont(f1);
        a.setHorizontalAlignment(SwingConstants.CENTER);
        a.setVerticalAlignment(SwingConstants.CENTER);
        a.setMaximumSize(new Dimension(150, a.getPreferredSize().height + 11));
        a.setForeground(Color.white);
        a.setPreferredSize(new Dimension(150, 30));
        a.setBackground(new Color(64, 81, 99));
         p7.add(Box.createRigidArea(new Dimension(100, 0)));
         p7.add(a);
         west_south.add(p7);
        //p8 ------------------------------------------------------------------------
        JPanel p8 = new JPanel();
        p8.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p8.setBackground(new Color(10, 61, 98));
        p8.setLayout(new BoxLayout(p8, BoxLayout.X_AXIS));
        p8.setBackground(new Color(10, 61, 98));

        ImageIcon icon2 = new ImageIcon("/home/mokhtar-mammeri/Desktop/BME2/icons/delete.png");
        JButton add = new JButton("حذف البيان    ", icon2);

        add.setFocusPainted(false);
        add.setFont(new Font("Arial", Font.BOLD, 15));
        add.setBackground(Color.white);
        add.setForeground(Color.red);
        add.setMaximumSize(new Dimension(170, 50));

        p8.add(Box.createRigidArea(new Dimension(160, 0)));
        p8.add(add);

        ImageIcon icon3 = new ImageIcon("/home/mokhtar-mammeri/Desktop/BME2/icons/clearfilter.png");
        JButton clear = new JButton("تصفية      ", icon3);

        clear.setFocusPainted(false);
        clear.setFont(new Font("Arial", Font.BOLD, 15));
        clear.setBackground(Color.white);
        clear.setForeground(Color.red);
        clear.setMaximumSize(new Dimension(170, 50));

        p8.add(Box.createRigidArea(new Dimension(50, 0)));
        p8.add(clear);
        west_south.add(Box.createRigidArea(new Dimension(0, 20)));
        west_south.add(p8);

        //p9 ------------------------------------------------------------------------
        JPanel p9 = new JPanel();
        p9.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p9.setBackground(new Color(10, 61, 98));
        p9.setLayout(new BoxLayout(p9, BoxLayout.X_AXIS));
        p9.setBackground(new Color(10, 61, 98));

        ImageIcon icon4 = new ImageIcon("/home/mokhtar-mammeri/Desktop/BME2/icons/excel.png");
        JButton extract = new JButton("إستخراج    ", icon4);

        extract.setFocusPainted(false);
        extract.setFont(new Font("Arial", Font.BOLD, 15));
        extract.setBackground(Color.white);
        extract.setForeground(Color.red);
        extract.setMaximumSize(new Dimension(170, 50));

        p9.add(Box.createRigidArea(new Dimension(260, 0)));
        west_south.add(Box.createRigidArea(new Dimension(0, 10)));
        p9.add(extract);
        west_south.add(p9);

        //south----------
        west_south.setBackground(new Color(10, 61, 98));

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

        Action(res, clear);

    }

}
