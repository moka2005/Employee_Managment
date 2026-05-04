
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class count {
    JPanel frame;
    static CardLayout cardLayout;
    static JPanel cardPanel;
    DefaultTableModel model = new DefaultTableModel();
    
    count(CardLayout a,JPanel b) {
        // Frame frame = new JFrame("Simple Table Example");
        cardLayout = a;
        cardPanel = b;
        frame = new JPanel();
        frame.setLayout(new BorderLayout());
        creat_interfacGraphics();
       
    }

    public void creat_interfacGraphics()
    {
        JPanel head = new JPanel();
        head.setPreferredSize(new Dimension(frame.getWidth(),200));
        head.setLayout(new BorderLayout());
         
        JLabel label_head = new JLabel("الحسابات");
         
        label_head.setHorizontalAlignment(SwingConstants.CENTER);
        label_head.setVerticalAlignment(SwingConstants.CENTER);
       
        head.add(label_head,BorderLayout.CENTER);
        
       Font f1 = new Font("Arial", Font.BOLD, 35);
       label_head.setFont(f1);
       head.setBackground(Color.red);
       
        frame.add(head,BorderLayout.NORTH);
       
       

       
        model.addColumn("name");
        model.addColumn("pass");
        model.addColumn("id");
        
       

     
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
            
            return false;
            }
        };
         try {
            String selectSQL = "SELECT * FROM users";
            ResultSet rs = DataBaseMangemet.select_Query(selectSQL);
            
            // إضافة البيانات إلى الجدول 
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("FirstN")); // العمود الأول: ID
                row.add(rs.getString("pass")); // العمود الثاني: Name
                row.add(rs.getString("id")); // العمود الثالث: Email
                model.addRow(row);
            }
            
            DataBaseMangemet.conn.close();
        } catch (SQLException e2) {System.out.println(e2.getMessage());}
        
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
        frame.add(scrollPane,BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setPreferredSize(new Dimension(frame.getWidth(),200));
        
        ImageIcon insert_icon= new ImageIcon("/home/mokhtar/Desktop/BME2/icons/include.png");
        JButton insert = new JButton("إضافة حساب",insert_icon);
        
        ImageIcon delete_icon = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/delete.png");
        JButton Delete = new JButton("حذف حساب",delete_icon);
        insert.setBackground(Color.white);
        Delete.setBackground(Color.white);
        insert.setPreferredSize(new Dimension(200,40));
        Delete.setPreferredSize(new Dimension(200,40));
        //insert.setBorderPainted(false);
        insert.setFocusPainted(false);
        Delete.setFocusPainted(false);
        
        
        ImageIcon edit_icon= new ImageIcon("/home/mokhtar/Desktop/BME2/icons/edit.png");
        JButton changePass = new JButton("تغيير كلمة السر",edit_icon);
        
  
        changePass.setBackground(Color.white);
       
        changePass.setPreferredSize(new Dimension(200,40));
        //insert.setBorderPainted(false);
        
        changePass.setFocusPainted(false);
        
        
        Delete.setFont(f2);
        insert.setFont(f2);
        changePass.setFont(f2);
        
        
        
        ImageIcon home_icon= new ImageIcon("/home/mokhtar/Desktop/BME2/icons/home.png");
        JButton welcom_pag = new JButton("الصفحة الرئيسية",home_icon);

        
       welcom_pag.setBackground(Color.white);
        
       welcom_pag.setPreferredSize(new Dimension(200,40));
        
       
        welcom_pag.setFocusPainted(false);
  
        JButton b[] = {insert,Delete,changePass,welcom_pag};
        
        
        south.setLayout(new FlowLayout(FlowLayout.CENTER));
        south.setPreferredSize(new Dimension(frame.getWidth(),50));
        south.add(insert);
        south.add(Delete);
      
        south.add(changePass);
        south.add(welcom_pag);
        
        Action(b);
        frame.add(south,BorderLayout.SOUTH);
      
        frame.setVisible(true);
        
    }
    public void Action(JButton b[])
    {
        ActionListener ActionListener;
        ActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e )
            {
                if(e.getSource()==b[3])
                {
                    cardLayout.show(cardPanel, "page1");
                    
                }
                else
                {
                    if(e.getSource()==b[1])
                    {
  
                            JDialog dialog = new JDialog((Frame) null, "حذف حساب", true);
                            dialog.setLayout(new GridLayout(3,1,0,0));
                            JPanel l1 = new JPanel();
                            //l1.setLayout(new GridLayout(2));
                            JPanel l2 = new JPanel();
                             JPanel l3 = new JPanel();
                            
                            JLabel id_label = new JLabel("معرف الحساب");
                            JTextField id_text = new JTextField(15);
                            
                            JLabel err = new JLabel("");
                            err.setForeground(Color.red);
                            
                            
                            
                            JButton ok = new JButton(" Ok");
                            ok.setPreferredSize(new Dimension(70,30));
                            JButton cancel = new JButton("Cancel");
                            cancel.setPreferredSize(new Dimension(70,30));
                            cancel.setMargin(new Insets(0,0,0,0));
                            
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
                                    int id =0 ;

                                    if(!s.equals(""))
                                    {
                                       id = Integer.parseInt(s);
                                        try
                                        {
                                                String selectSQL = "SELECT id FROM USERS WHERE id = '" + id +"'";
                                            ResultSet rs = DataBaseMangemet.select_Query(selectSQL);
                                            if(rs.next())
                                            {

                                                if(JOptionPane.showConfirmDialog(null,"هل أنت متأكد ","تأكيد",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                                                {
                                                    dialog.dispose();
                                                    model.setRowCount(0); 

                                                    DataBaseMangemet.ExexcuteStatement("DELETE FROM USERS WHERE id = '" + id +"'  ");

                                                    ResultSet rs2 = DataBaseMangemet.select_Query("select * from users");
                                                    while (rs2.next()) {
                                                        Vector<String> row = new Vector<>();
                                                        row.add(rs2.getString("FirstN")); // العمود الأول: ID
                                                        row.add(rs2.getString("pass")); // العمود الثاني: Name
                                                        row.add(rs2.getString("id")); // العمود الثالث: Email
                                                        model.addRow(row);
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                if(id !=0)err.setText("هذا المعرف غير موجود");
                                            }    
                                        }
                                        catch (Exception e2) 
                                        {
                                            System.out.println("❌ Connection failed.546");
                                            e2.printStackTrace();

                                        } 

                                    }
                                    else
                                    {
                                        err.setText("يرجى ادخال المعرف");
                                    }   
                                }
                            }); 
                            cancel.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent e)
                                {
                                    dialog.dispose();
                                }
                            });
                             dialog.setVisible(true);
                    }
                    else
                    {
                        if(e.getSource()==b[0])
                        {
                            JDialog dialog = new JDialog((Frame) null, "أدخل بيانات الحساب", true);
                            dialog.setLayout(new GridLayout(4,1,0,0));
                            
                            
                            
                            JPanel l1 = new JPanel();
                            l1.setLayout(new FlowLayout(FlowLayout.LEFT));
                            
                            JPanel l2 = new JPanel();
                            l2.setLayout(new FlowLayout(FlowLayout.LEFT));
                            JPanel l3 = new JPanel();
                           
                            JPanel l4 = new JPanel();
                           
                            
                            JTextField usernameField = new JTextField(15);
                            JPasswordField passwordField = new JPasswordField(15);
                            
                            
                            JLabel name = new JLabel("إسم المستخدم");
                            JLabel pass = new JLabel("كلمة المرور       ");
                            
                            JLabel err = new JLabel("");
                            err.setForeground(Color.red);
                            
                            JButton ok = new JButton(" Ok");
                            ok.setPreferredSize(new Dimension(70,30));
                            JButton cancel = new JButton("Cancel");
                            cancel.setPreferredSize(new Dimension(70,30));
                            cancel.setMargin(new Insets(0,0,0,0));
                            
                            
                            l1.add(name);
                            l1.add(usernameField);
                            l2.add(pass);
                            l2.add(passwordField);
                            
                            l3.add(ok);
                            l3.add(cancel);
                            l4.add(err);
                           
                            
                            dialog.add(l1);
                           
                            dialog.add(l2);
                            dialog.add(l3);
                            dialog.add(l4);
                            dialog.pack();
                            dialog.setLocationRelativeTo(frame);

                            
                           
                            
                           ok.addActionListener(new ActionListener() {
                               // @Override
                                public void actionPerformed(ActionEvent e) {
                                     
                                    String username = usernameField.getText();
                                    String password = new String(passwordField.getPassword());
                                    if(username.equals("") || password.equals(""))
                                    {
                                        err.setText("يرجى ادخال كل المعلومات");
                                        
                                              
                                    }
                                    else
                                    {
                                        if(username.length()>10 || password.length()>10)err.setText("تجاوز الطول المسموح ");
                                        else
                                        {
                                       
                                        
                                            try
                                            {
                                                String selectSQL = "SELECT FirstN FROM users where FirstN = '" +username+"'";
                                                ResultSet rs = DataBaseMangemet.select_Query(selectSQL);
                                                if(rs.next()) err.setText(" إسم المستخدم مسعمل");
                                                else
                                                {
                                                    dialog.dispose();
                                                    DataBaseMangemet.ExexcuteStatement("INSERT INTO USERS (FirstN, pass) VALUES ('" + username + "', '" + password + "');");
                                                    model.setRowCount(0); 
                                                    ResultSet rs2 = DataBaseMangemet.select_Query("select * from users");
                                                    while (rs2.next()) {
                                                        Vector<String> row = new Vector<>();
                                                        row.add(rs2.getString("FirstN")); 
                                                        row.add(rs2.getString("pass")); 
                                                        row.add(rs2.getString("id")); 
                                                        model.addRow(row);
                                                    }
                                                }    
                                            }
                                            catch (Exception e2) 
                                            {
                                                System.out.println("❌ Connection failed.546");
                                                e2.printStackTrace();

                                            } 
                                        }    
                                        
                                    }    
                                }
                            });
                           cancel.addActionListener(new ActionListener() {
                               // @Override
                                public void actionPerformed(ActionEvent e) {
                                    dialog.dispose();
                                }
                           });    
                            dialog.setVisible(true);
                        }
                        else
                        {
                            if(e.getSource()==b[2])
                            {
                                
                                JDialog dialog = new JDialog((Frame) null, "أدخل بيانات الحساب", true);
                                dialog.setLayout(new GridLayout(4,1,0,0));
                            
                                JPanel p1 = new JPanel();
                                JPanel p2 = new JPanel();
                                JPanel p3 = new JPanel();
                                JPanel p4 = new JPanel();
                                
                                JLabel id_label = new JLabel("معرف الحساب      ");
                                JLabel pass_label = new JLabel("كلمة المرور الجديدة");
                                
                                JTextField id_text = new JTextField(15);
                                JTextField pass_text = new JTextField(15);
                                
                                JLabel err = new JLabel("");
                                err.setForeground(Color.red);
                            
                                JButton ok = new JButton(" Ok");
                                ok.setPreferredSize(new Dimension(70,30));
                                JButton cancel = new JButton("Cancel");
                                cancel.setPreferredSize(new Dimension(70,30));
                                cancel.setMargin(new Insets(0,0,0,0));
                                
                                p1.add(id_label);
                                p1.add(id_text);
                                
                                p2.add(pass_label);
                                p2.add(pass_text);
                                
                                p3.add(ok);
                                p3.add(cancel);        
                                      
                                
                                p4.add(err);
                                
                                dialog.add(p1);
                                dialog.add(p2);
                                dialog.add(p3);
                                dialog.add(p4);
                                
                                dialog.pack();
                                dialog.setLocationRelativeTo(frame);

                                
                                
                                
                                
                                JTextField id = new JTextField();
                                JPasswordField passwordField = new JPasswordField();
                            
                                 Object[] message = {
                                    "معرف الحساب", id,
                                    "كلمة المرور الجديدة", passwordField
                                };
                               ok.addActionListener(new ActionListener(){
                                    
                                    public void actionPerformed(ActionEvent ae) 
                                    {
                                        String id_ = id_text.getText();
                                        String password = new String(pass_text.getText());
                                        if(id_.equals("") || password.equals(""))err.setText("يرجى ادخال كل المعلومات");
                                        else
                                        {
                                            try
                                            {
                                                ResultSet rs2 = DataBaseMangemet.select_Query("select id from users where id = '"+id_+"'");
                                                if(rs2.next())
                                                {
                                                    if(password.length()>10)err.setText("تجاوز الطول المسموح ");
                                                    else
                                                    {
                                                        dialog.dispose();
                                                        DataBaseMangemet.ExexcuteStatement("update USERS set pass = '" + password + "' where id = '"+id_+"';");
                                                        model.setRowCount(0); 
                                                       rs2 = DataBaseMangemet.select_Query("select * from users");
                                                        while (rs2.next()) {
                                                            Vector<String> row = new Vector<>();
                                                            row.add(rs2.getString("FirstN")); 
                                                            row.add(rs2.getString("pass")); 
                                                            row.add(rs2.getString("id")); 
                                                            model.addRow(row);
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    err.setText("لايوجد خساب بهذا المعرف ");
                                                }   
                                            }
                                            catch (Exception e2) 
                                            {
                                                System.out.println("❌ Connection failed.546");
                                                e2.printStackTrace();

                                            } 
                                        
                                        }
                                  
                                    }
                                   
                               });
                               cancel.addActionListener(new ActionListener()
                               {
                                   public void actionPerformed(ActionEvent ae) 
                                    {
                                        dialog.dispose();
                                    }
                                    
                               });
                               dialog.setVisible(true);
      
                            }
                        }    
                    }    
                }
            }
        };
        b[0].addActionListener(ActionListener);
        b[1].addActionListener(ActionListener);
        b[2].addActionListener(ActionListener);
        b[3].addActionListener(ActionListener);
    }
}
