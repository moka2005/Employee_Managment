
import java.sql.ResultSet;

import java.awt.Color;
import java.awt.event.*;
import java.awt.Font;

import javax.swing.*;

/**
 *
 * @author mokht
 */
public class Login extends JFrame implements ActionListener 
{
    JButton Login_BU = new JButton("تسجيل الدخول");
    JButton cancel_BU = new JButton("الغاء");
    JLabel name_Label = new JLabel("اسم المستخدم");
    JLabel pass_Label = new JLabel("كلمة المرور");
    JTextField name_TextField = new JTextField();
    JPasswordField pass_Passwordfield = new JPasswordField();
    JCheckBox show_checkbox = new JCheckBox("إظهار كلمة المرور");
    JLabel Login_Label = new JLabel("تسجيل الدخول");
    JPanel panel = new JPanel(null);
    Login()
    {
        setVisible(true);
        this.setTitle("تسجيل الدخول");
        this.setSize(800,550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        
        
        panel.setBounds(0,0,800,140);
        Login_Label.setBounds(300,50,500,40);
        name_Label.setBounds(620,200,200,35);
        pass_Label.setBounds(620,250,100,35);
        name_TextField.setBounds(250,200,350,35);
        pass_Passwordfield.setBounds(250,250, 350, 35);
        Login_BU.setBounds(250,330,150,35);
        cancel_BU.setBounds(450,330,150,35);
        show_checkbox.setBounds(100,260,125,20);
        
        this.add(Login_BU);
        this.add(pass_Label);
        this.add(pass_Passwordfield);
        this.add(name_TextField);
        this.add(name_Label);
        panel.add(Login_Label);
        this.add(show_checkbox);
        add(Login_Label);
        this.add(cancel_BU);
        add(panel);

        // Background Color
        panel.setBackground(Color.red);
        getContentPane().setBackground(Color.decode("#330033"));
        Login_Label.setBackground(Color.black);

        // Type of writing
       Login_Label.setFont(new Font("NewTimeRoman", Font.BOLD, 32));
       Font f1 = new Font("NewTimeRoman",Font.BOLD,18);

        name_Label.setFont(f1);
        pass_Label.setFont(f1);
        Login_BU.setFont(f1);
        cancel_BU.setFont(f1);
        pass_Passwordfield.setFont(f1);
        name_TextField.setFont(f1);

        // Color of Writing
        name_Label.setForeground(Color.WHITE);
        pass_Label.setForeground(Color.WHITE);
        show_checkbox.setBackground(Color.decode("#330033"));
        show_checkbox.setForeground(Color.WHITE);


        cancel_BU.addActionListener(this);
        Login_BU.addActionListener(this);
        cancel_BU.addActionListener(this);
        show_checkbox.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==show_checkbox)
        {
                if(show_checkbox.getText() == "اخفاء كلمة السر ")
                {
                    pass_Passwordfield.setEchoChar('\u2022');
                    show_checkbox.setText("إظهار كلمة المرور");
                }
                else
                {
                    pass_Passwordfield.setEchoChar('\0');
                    show_checkbox.setText("اخفاء كلمة السر ");
                }
        }
        else
        {
            if (e.getSource()==cancel_BU)
            {
                int c = JOptionPane.showConfirmDialog(null,"هل أنت متأكد ؟ ","تأكيد",JOptionPane.YES_NO_OPTION);
                if(c==0)System.exit(0);
            }
            if(e.getSource()==Login_BU)
            {
                try {
                  /*  if (name_TextField.getText().isEmpty() || pass_Passwordfield.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "⚠️ الرجاء ملء كل الحقول!");
                        return;
                    }*/


                    String id = name_TextField.getText();
                    String pass = pass_Passwordfield.getText();
                    String selectSQL = "SELECT  id,pass FROM users where id = '" +id+"' and pass = '"+pass+"'";
                    ResultSet rs = DataBaseMangemet.select_Query(selectSQL);
                    id ="";
                    pass="";

                    // طباعة النتائج
                    while (rs.next()) {

                        id = rs.getString("id");
                        pass = rs.getString("pass");
                    }
                    
                    if(pass_Passwordfield.getText().isEmpty() && name_TextField.getText().isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"الرجاء التأكد من أسم المستخدم أو كلمة المرور");
                    }
                    else
                    {
                        if(name_TextField.getText().equals(id)&& pass_Passwordfield.getText().equals(pass)){
                            //.exit(0);
                            this.setVisible(false);
                            WelcomePage W =  new WelcomePage(this.getWidth(),this.getHeight());
                        }
                        else JOptionPane.showMessageDialog(null,"الرجاء التأكد من أسم المستخدم أو كلمة المرور");
                    }
                    DataBaseMangemet.conn.close();
                } catch (Exception e2) {
                    System.out.println("❌ Connection failed.546");
                    e2.printStackTrace();
                }
            }
        }
    }
    
}

