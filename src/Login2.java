
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.sql.ResultSet;

class Login2 {

    static JFrame frame;
    static JCheckBox pass_show = new JCheckBox("إظهار كلمة المرور");
    static JButton Login_L = new JButton("تسجيل الدخول ");
    static JButton Login_C = new JButton("إلغاء");
    static JPasswordField passField = new JPasswordField(20);
    static JTextField namField = new JTextField(20);
    

    Login2(int x, int y) {
        frame = new JFrame();
        frame.setSize(x, y);
        //frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        creat_interfceGraphice();
        Actions();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }

    public static void creat_interfceGraphice() {
        JPanel Login_name = new JPanel();
        Login_name.setLayout(new BorderLayout());
        Login_name.setPreferredSize(new Dimension(frame.getWidth(),200));
        Login_name.setBackground(Color.RED);

        JLabel name = new JLabel(" تسجيل الدخول ");
        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setVerticalAlignment(SwingConstants.CENTER);

        Font f1 = new Font("Arial", Font.BOLD, 35);
        Font f2 = new Font("Arial", Font.BOLD, 18);
        name.setFont(f1);
        Login_name.add(name);

        JPanel comp = new JPanel();
        
        comp.setLayout(new BorderLayout());
        JPanel vid = new JPanel();
        vid.setPreferredSize(new Dimension(frame.getWidth(),75));
        comp.add(vid,BorderLayout.NORTH); 
        vid.setBackground(Color.decode("#330033"));

        JPanel But_textF = new JPanel();
        But_textF.setLayout(new GridLayout(6,1,0,0));
        But_textF.setBackground(Color.decode("#330033"));

        JPanel id_person = new JPanel();
        id_person.setLayout(new FlowLayout(FlowLayout.CENTER,30,0));
      

        JLabel namLabel = new JLabel("اسم المستخدم");
        id_person.setBackground(Color.decode("#330033"));
        namLabel.setPreferredSize(new Dimension(200,40));
   
        namLabel.setForeground(Color.WHITE);
        namLabel.setFont(f2);
      
        JLabel pass_show2 = new JLabel("");
        pass_show2.setForeground(Color.WHITE);
        pass_show2.setPreferredSize(new Dimension(200,40));
        pass_show2.setFont(f2);
       
        namField.setFont(f2); 
  
        id_person.add( pass_show2);
        id_person.add(namField);
        id_person.add(namLabel);
        
        
        JPanel a = new JPanel();
      
        a.setBackground(Color.decode("#330033"));
        
        
        But_textF.add(a);
     
        But_textF.add(id_person);
        JPanel pass_person = new JPanel();
        pass_person.setLayout(new FlowLayout(FlowLayout.CENTER,30,0));
        pass_person.setBackground(Color.decode("#330033"));
        

        JLabel passLabel = new JLabel("كلمة المرور ");
        passLabel.setForeground(Color.WHITE);
        passLabel.setPreferredSize(new Dimension(200,40));

        passLabel.setFont(f2);

        
        
        passField.setFont(f2);
      
        pass_show.setForeground(Color.WHITE);
        pass_show.setBackground(Color.decode("#330033"));
        pass_show.setPreferredSize(new Dimension(200,40));
        pass_show.setFont(f2);
        pass_show.setBorderPainted(false);
        pass_show.setFocusPainted(false);

        pass_person.add(pass_show);
        
        pass_person.add(passField);
        pass_person.add(passLabel);
        But_textF.add(pass_person);


       

        JPanel Buttons = new JPanel();
        Buttons.setLayout(new FlowLayout(FlowLayout.CENTER,30,0));
         Buttons.setBackground(Color.decode("#330033"));

        
 

        
        Login_L.setPreferredSize(new Dimension(200,40));
        
        Login_C.setPreferredSize(new Dimension(200,40));

       
        Login_C.setFont(f2);
        Login_L.setFont(f2);
      
        Login_C.setBorderPainted(false);
        Login_C.setFocusPainted(false);
        
        Login_L.setBorderPainted(false);
        Login_L.setFocusPainted(false);
        
        Buttons.add(Login_L);
        Buttons.add(Login_C);
        But_textF.add(Buttons);

         

        comp.add(But_textF,BorderLayout.CENTER);
        frame.add(Login_name,BorderLayout.NORTH);
        frame.add(comp,BorderLayout.CENTER);

        
    
        

    }

    public static void Actions() {
    
       frame.addWindowListener(new WindowListener() {
    
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(frame, "Event: Window Closing");
                frame.dispose();

            }

           @Override
           public void windowOpened(WindowEvent we) {
               //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
           }

           @Override
           public void windowClosed(WindowEvent we) {
               //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
           }

           @Override
           public void windowIconified(WindowEvent we) {
               //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
           }

           @Override
           public void windowDeiconified(WindowEvent we) {
               //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
           }

           @Override
           public void windowActivated(WindowEvent we) {
               //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
           }

           @Override
           public void windowDeactivated(WindowEvent we) {
               //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
           }
 
        }); 
      

        ActionListener ActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == pass_show) {
                    if (pass_show.getText().equals("اخفاء كلمة المرور")) {
                        passField.setEchoChar('\u2022');
                        pass_show.setText("إظهار كلمة المرور");
                    } else {
                       passField.setEchoChar((char) 0);
                  
                        pass_show.setText("اخفاء كلمة المرور");
                    }
                } else {
                    if (e.getSource() == Login_C) {
                        int c = JOptionPane.showConfirmDialog(null, "هل أنت متأكد ؟ ", "تأكيد", JOptionPane.YES_NO_OPTION);
                        if (c == 0) {
                            System.exit(0);
                        }
                    }
                    if (e.getSource() == Login_L) {

                        try {
                            /*  if (name_TextField.getText().isEmpty() || pass_Passwordfield.getText().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "⚠️ الرجاء ملء كل الحقول!");
                                return;
                            }*/
                            int id=0;
                            String name = namField.getText();
                            String pass = passField.getText();
                            String selectSQL = "SELECT FirstN, pass,id FROM users where FirstN = '" + name + "' and pass = '" + pass + "'";
                            ResultSet rs = DataBaseMangemet.select_Query(selectSQL);
                            name = "";
                            pass = "";

                            // طباعة النتائج
                            while (rs.next()) {

                                name = rs.getString("FirstN");
                                pass = rs.getString("pass");
                                id = rs.getInt("id");
                            }
                          

                            if (passField.getText().isEmpty() && namField.getText().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "الرجاء التأكد من أسم المستخدم أو كلمة المرور");
                            } else {
                                if ( namField.getText().equals(name)&&passField.getText().equals(pass) ) {
                                    //.exit(0);
                                    //frame.setVisible(false);
                                    WelcomePage W = new WelcomePage(frame.getWidth(), frame.getHeight(),id);
                                } else {
                                    JOptionPane.showMessageDialog(null, "الرجاء التأكد من أسم المستخدم أو كلمة المرور");
                                }
                            }
                            DataBaseMangemet.conn.close();
                        } catch (Exception e2) {
                            System.out.println("❌ Connection failed.546");
                            e2.printStackTrace();
                        }
                    }
                }
            }
        };
           Login_L.addActionListener(ActionListener);
            Login_C.addActionListener(ActionListener);
            pass_show.addActionListener(ActionListener);
    }        


}


