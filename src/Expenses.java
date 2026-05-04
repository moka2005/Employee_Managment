
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.*;
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
public class Expenses {

    JPanel frame;
    static JTable table;
    static DefaultTableModel model = new DefaultTableModel();
    JTextField name_text;
    JTextField pay_text;
    JTextField amount_text;
    JTextField total_text;
    JTextField resp_text;
    static JDateChooser date_text;
    static JDateChooser end_text;
    static JDateChooser begin_text;
    static CardLayout cardLayout;
    static JPanel cardPanel;

    Expenses(CardLayout cardLayout,JPanel cardPanel) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        frame = new JPanel();
        
        frame.setLayout(new BorderLayout());
        creat_interfceGraphice();
        

    }

    public static  void update(boolean i) {
        
        try {
            model.setRowCount(0);
            String sql = "select * from expense ;";
            ResultSet rs = DataBaseMangemet.select_Query(sql);

            while (rs.next()) {

                Vector<String> row = new Vector<>();
                row.add(rs.getString("total") + "DA");
                row.add(rs.getString("product_price") + " DA");
                row.add(rs.getString("product_amount"));
                row.add(rs.getString("input_date"));
                row.add(rs.getString("product_name"));
                row.add(rs.getString("name_expenses"));
                row.add(rs.getString("num"));
                
                 if (i == true) {                   
                    model.addRow(row);
                    
                } else {
                     
                    if ((begin_text.getDate().before(rs.getDate("input_date")) || begin_text.getDate().equals(rs.getDate("input_date"))) && (end_text.getDate().after(rs.getDate("input_date")) || end_text.getDate().equals(rs.getDate("input_date")))) {
                      
                        model.addRow(row);
                    }
                      
                }

            }

        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }
        

    }

    public void Action(JButton add, JButton delete,JButton filtering) {
        ActionListener a = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == add) {
                    if (name_text.getText().isEmpty() || pay_text.getText().isEmpty() || amount_text.getText().isEmpty() || total_text.getText().isEmpty() || resp_text.getText().isEmpty() || date_text.getDate() == null) {
                        JOptionPane.showMessageDialog(null, "يرجى ملئ كل المعلومات ", "خطأ", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (name_text.getText().length() <= 30 && resp_text.getText().length() <= 30 && amount_text.getText().length() <= 10 && pay_text.getText().length() <= 10 && total_text.getText().length() <= 10) {
                            if (pay_text.getText().matches("\\d+") && amount_text.getText().matches("\\d+") && total_text.getText().matches("\\d+")) {
                                if (resp_text.getText().matches("[a-zA-Zأ-ي]+") && name_text.getText().matches("[a-zA-Zأ-ي]+")) {

                                    try {
                                        String sql = "insert into expense (product_name,product_amount,total,product_price,input_date,name_expenses) values ("
                                                + "'" + name_text.getText() + "','" + amount_text.getText() + "','" + total_text.getText() + "','" + pay_text.getText() + "','" + date_text.getDate() + "','" + resp_text.getText() + "');";
                                        DataBaseMangemet.ExexcuteStatement(sql);
                                         DataBaseMangemet.ExexcuteStatement("WITH ordered AS (\n"
                                                    + "    SELECT product_id, ROW_NUMBER() OVER (ORDER BY product_id) AS new_num\n"
                                                    + "    FROM expense\n"
                                                    + ")\n"
                                                    + "UPDATE expense\n"
                                                    + "SET num = ordered.new_num\n"
                                                    + "FROM ordered\n"
                                                    + "WHERE expense.product_id= ordered.product_id;");

                                        update(true);

                                    } catch (Exception e2) {
                                        System.out.println("❌ Connection failed.546");
                                        e2.printStackTrace();
                                    }
                                } else {
                                    System.out.print("lojg222");
                                    JOptionPane.showMessageDialog(null, "يرجى إحترام محتوى كل خانة ", "خطأ", JOptionPane.ERROR_MESSAGE);
                                }

                            } else {
                                System.out.print("lojg");
                                JOptionPane.showMessageDialog(null, "يرجى إحترام محتوى كل خانة ", "خطأ", JOptionPane.ERROR_MESSAGE);
                            }

                        } else {
                            JOptionPane.showMessageDialog(null, "يرجى إحترام طول كل خانة ", "خطأ", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } else {
                    if (e.getSource() == delete) {
                        JDialog dialog = new JDialog((Frame) null, "حذف مدخل", true);
                        dialog.setLayout(new GridLayout(3, 1, 0, 0));
                        JPanel l1 = new JPanel();
                        //l1.setLayout(new GridLayout(2));
                        JPanel l2 = new JPanel();
                        JPanel l3 = new JPanel();

                        JLabel id_label = new JLabel("رقم المنتج");
                        JTextField id_text = new JTextField(15);

                        JLabel err = new JLabel("");
                        err.setForeground(Color.red);

                        JButton ok = new JButton(" Ok");
                        ok.setPreferredSize(new Dimension(70, 30));
                        JButton cancel = new JButton("Cancel");
                        cancel.setPreferredSize(new Dimension(70, 30));
                        cancel.setMargin(new Insets(0, 0, 0, 0));

                        l1.add(id_label);
                        l1.add(id_text);

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
                                String s = id_text.getText();
                                int id = 0;

                                if (!s.equals("")) {
                                    id = Integer.parseInt(s);
                                    try {
                                        String selectSQL = "SELECT product_id FROM expense WHERE num = '" + id + "'";
                                        ResultSet rs = DataBaseMangemet.select_Query(selectSQL);
                                        if (rs.next()) {

                                            dialog.dispose();
                                            model.setRowCount(0);

                                            DataBaseMangemet.ExexcuteStatement("DELETE FROM expense WHERE num = '" + id + "'  ");
                                            
                                            DataBaseMangemet.ExexcuteStatement("WITH ordered AS (\n"
                                                    + "    SELECT product_id, ROW_NUMBER() OVER (ORDER BY product_id) AS new_num\n"
                                                    + "    FROM expense\n"
                                                    + ")\n"
                                                    + "UPDATE expense\n"
                                                    + "SET num = ordered.new_num\n"
                                                    + "FROM ordered\n"
                                                    + "WHERE expense.product_id= ordered.product_id;");
                                            update(true);

                                        } else {
                                            if (id != 0) {
                                                err.setText("هذاالرقم غير موجود");
                                            }
                                        }
                                    } catch (Exception e2) {
                                        System.out.println("❌ Connection failed.546");
                                        e2.printStackTrace();

                                    }

                                } else {
                                    err.setText("يرجى إدخال الرقم");
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
                    else
                    {
                        if(e.getSource()==filtering)
                        {
                             if (begin_text.getDate() == null) {
                                    JOptionPane.showMessageDialog(null, "يرجى إدخال تاريخ البداية", "خطأ", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    if (end_text.getDate() == null) {
                                        JOptionPane.showMessageDialog(null, "يرجى إدخال تاريخ النهاية", "خطأ", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                       
                                        update(false);
                                        
                                    }
                                }
                        }    
                    }    
                }

            }
            

        };
        add.addActionListener(a);
        delete.addActionListener(a);
        filtering.addActionListener(a);

    }

    public void edit_label(JLabel a) {
        Font f1 = new Font("Arial", Font.BOLD, 20);
        a.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        a.setOpaque(true);
        a.setFont(f1);
        a.setHorizontalAlignment(SwingConstants.CENTER);
        a.setVerticalAlignment(SwingConstants.CENTER);
        a.setMaximumSize(new Dimension(200, a.getPreferredSize().height + 11));
        a.setForeground(Color.white);

        a.setBackground(new Color(10, 61, 98));
    }

    public void creat_interfceGraphice() {

        // north--------------------------------------------
        JPanel North = new JPanel();
        North.setPreferredSize(new Dimension(0, 60));
        North.setBackground(Color.GRAY);

        North.setLayout(new BorderLayout());

        JLabel l = new JLabel("إدارة المصاريف");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);

        Font f = new Font("Arial", Font.BOLD, 35);
        l.setFont(f);
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

                Employee.imagePath="";
                cardLayout.show(cardPanel, "page1");
            }
        });

        JPanel Padding = new JPanel();
        Padding.setPreferredSize(new Dimension(220, 0));
        Padding.setBackground(Color.GRAY);

        home_page.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));
        North.add(home_page, BorderLayout.WEST);
        North.add(Padding, BorderLayout.EAST);
        North.add(l, BorderLayout.CENTER);
        frame.add(North, BorderLayout.NORTH);

        // west ----------------------------------------------------------------
        Font f1 = new Font("Arial", Font.BOLD, 15);
        JPanel west = new JPanel();
        west.setPreferredSize(new Dimension(750, 0));
        west.setBackground(new Color(10, 61, 98));
        west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
        frame.add(west, BorderLayout.WEST);
        // p1 -----------------------------
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(10, 61, 98));
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel name_product = new JLabel("إسم المنتج");
        name_product.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_product.getPreferredSize().height + 11));
        Employee.edit_label(name_product);

        name_text = new JTextField(11);
        name_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        name_text.setFont(f1);
        name_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        p1.add(name_text);
        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        p1.add(name_product);

        JLabel amount_product = new JLabel("كمية المنتج");
        amount_product.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_product.getPreferredSize().height + 11));
        Employee.edit_label(amount_product);

        amount_text = new JTextField(11);
        amount_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        amount_text.setFont(f1);
        amount_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        p1.add(amount_text);
        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        p1.add(amount_product);
        p1.add(Box.createRigidArea(new Dimension(20, 0)));
        west.add(Box.createRigidArea(new Dimension(0, 60)));
        west.add(p1);

        // p2 -------------------------------------------------------------------
        JPanel p2 = new JPanel();
        p2.setBackground(new Color(10, 61, 98));
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel pay = new JLabel("سعر المنتج");
        pay.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_product.getPreferredSize().height + 11));
        Employee.edit_label(pay);

        pay_text = new JTextField(11);
        pay_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        pay_text.setFont(f1);
        pay_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        p2.add(pay_text);
        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        p2.add(pay);

        JLabel total = new JLabel("المجموع");
        total.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_product.getPreferredSize().height + 11));
        Employee.edit_label(total);

        total_text = new JTextField(11);
        total_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        total_text.setFont(f1);
        total_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        p2.add(total_text);
        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        p2.add(total);
        p2.add(Box.createRigidArea(new Dimension(20, 0)));
        west.add(p2);

        // p3 -------------------------------------------------------
        JPanel p3 = new JPanel();
        p3.setBackground(new Color(10, 61, 98));
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel resp = new JLabel("المسؤول");
        resp.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_product.getPreferredSize().height + 11));
        Employee.edit_label(resp);

        resp_text = new JTextField(11);
        resp_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        resp_text.setFont(f1);
        resp_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        p3.add(resp_text);
        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        p3.add(resp);

        JLabel date = new JLabel("تاريخ الإدخال");
        date.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_product.getPreferredSize().height + 11));
        Employee.edit_label(date);

        date_text = new JDateChooser();
        date_text.setDateFormatString("yyyy-MM-d");
        JTextField dateEditor2 = (JTextField) date_text.getDateEditor().getUiComponent();

        dateEditor2.setFont(f1);
        dateEditor2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        date_text.setFont(f1);

        date_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        date_text.setPreferredSize(new Dimension(169, 0));

        total_text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        total_text.setFont(f1);
        total_text.setMaximumSize(new Dimension(Integer.MAX_VALUE, name_text.getPreferredSize().height + 11));

        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        p3.add(date_text);
        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        p3.add(date);
        p3.add(Box.createRigidArea(new Dimension(20, 0)));
        west.add(p3);

        //p4------------------------------------------------------------------
        JPanel p4 = new JPanel();
        p4.setBackground(new Color(10, 61, 98));
        p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
        p4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        ImageIcon icon2 = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/delete.png");
        JButton delete = new JButton("حذف البيان    ", icon2);

        delete.setFocusPainted(false);
        delete.setFont(new Font("Arial", Font.BOLD, 15));
        delete.setBackground(Color.white);
        delete.setForeground(Color.red);
        delete.setMaximumSize(new Dimension(170, 50));

        p4.add(Box.createRigidArea(new Dimension(200, 0)));
        p4.add(delete);

        ImageIcon icon3 = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/include.png");
        JButton add = new JButton("إضافة      ", icon3);

        add.setFocusPainted(false);
        add.setFont(new Font("Arial", Font.BOLD, 15));
        add.setBackground(Color.white);
        add.setForeground(Color.red);
        add.setMaximumSize(new Dimension(170, 50));

        p4.add(Box.createRigidArea(new Dimension(50, 0)));
        p4.add(add);
        west.add(Box.createRigidArea(new Dimension(0, 30)));

        west.add(p4);

        //p5--------------------------------------------------------------------
        JPanel p5 = new JPanel();
        p5.setBackground(new Color(10, 61, 98));
        p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
        p5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel head = new JLabel("متابعة المصروفات");
        edit_label(head);
        p5.add(Box.createRigidArea(new Dimension(300, 0)));
        p5.add(head);
        west.add(Box.createRigidArea(new Dimension(0, 60)));
        west.add(p5);

        // p6 -----------------------------------------------------------------
        JPanel p6 = new JPanel();
        p6.setBackground(new Color(10, 61, 98));
        p6.setLayout(new BoxLayout(p6, BoxLayout.X_AXIS));
        p6.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel begin = new JLabel("تاريخ البداية");
        edit_label(begin);
        JLabel end = new JLabel("تاريخ النهاية");
        edit_label(end);
        p6.add(Box.createRigidArea(new Dimension(100, 0)));

        p6.add(begin);
        p6.add(Box.createRigidArea(new Dimension(150, 0)));
        p6.add(end);
        west.add(Box.createRigidArea(new Dimension(0, 20)));
        west.add(p6);

        //p7-----------------------------------------------------------------
        JPanel p7 = new JPanel();
        p7.setBackground(new Color(10, 61, 98));
        p7.setLayout(new BoxLayout(p7, BoxLayout.X_AXIS));
        p7.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

         begin_text = new JDateChooser();
        JTextField dateEditor3 = (JTextField) begin_text.getDateEditor().getUiComponent();
        begin_text.setDateFormatString("yyyy-MM-d");

        dateEditor3.setFont(f1);
        dateEditor3.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        begin_text.setFont(f1);

        begin_text.setMaximumSize(new Dimension(250, name_text.getPreferredSize().height + 11));

        begin_text.setPreferredSize(new Dimension(250, 0));

        p7.add(Box.createRigidArea(new Dimension(70, 0)));
        p7.add(begin_text);

         end_text = new JDateChooser();
        end_text.setDateFormatString("yyyy-MM-d");
        JTextField dateEditor4 = (JTextField) end_text.getDateEditor().getUiComponent();

        dateEditor4.setFont(f1);
        dateEditor4.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        end_text.setFont(f1);

        end_text.setMaximumSize(new Dimension(250, name_text.getPreferredSize().height + 11));

        end_text.setPreferredSize(new Dimension(250, 0));
        p7.add(Box.createRigidArea(new Dimension(110, 0)));
        p7.add(end_text);
        p7.add(Box.createRigidArea(new Dimension(30, 0)));
        west.add(p7);

        // p8 --------------------------------------------------------------------
        ImageIcon icon5 = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/filter.png");

        JButton filtering = new JButton("تصفية      ", icon5);

        JPanel p8 = new JPanel();
        p8.setBackground(new Color(10, 61, 98));
        p8.setLayout(new BoxLayout(p8, BoxLayout.X_AXIS));
        p8.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        filtering.setFocusPainted(false);
        filtering.setFont(new Font("Arial", Font.BOLD, 15));
        filtering.setBackground(Color.white);
        filtering.setForeground(Color.red);
        filtering.setMaximumSize(new Dimension(170, 50));

        p8.add(Box.createRigidArea(new Dimension(300, 0)));
        p8.add(filtering);
        west.add(Box.createRigidArea(new Dimension(0, 30)));

        west.add(p8);

        // center --------------------------------------------------------------
        Font f2 = new Font("Arial", Font.BOLD, 15);
        model.addColumn("إجمالي");
        model.addColumn("سعر المنتج");
        model.addColumn("كمية المنتج");
        model.addColumn("تاريخ الإدخال");
        model.addColumn("إسم المنتج");
        model.addColumn("إسم المسؤول");
        model.addColumn("الرقم");
        update(true);

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

        JPanel center = new JPanel();

        center.setLayout(new BorderLayout());

        frame.add(center, BorderLayout.CENTER);

        center.add(scrollPane, BorderLayout.CENTER);
        Action(add, delete,filtering);

    }

}
