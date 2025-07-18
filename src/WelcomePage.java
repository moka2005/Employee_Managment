import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class WelcomePage {
    static JFrame frame2;
    static JPanel frame;
    static int  id ;  
    static CardLayout cardLayout;
    static JPanel cardPanel;
    WelcomePage(int x,int y,int id) {
        
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        //Login2.frame.dispose();
        frame2 = new JFrame();
        frame = new JPanel();
        this.id = id;
        frame2.setSize(x, y);
        frame.setLayout(new BorderLayout());
        frame2.setLocationRelativeTo(null);
        creat_interfceGraphice(x,y);
        frame2.add(cardPanel);
        frame2.setVisible(true);
        
        count c = new count(cardLayout,cardPanel);
        Employee e = new Employee(cardLayout,cardPanel);
        //Employee e = new Employee(cardLayout,cardPanel);
        
        cardPanel.add(frame, "page1");
        cardPanel.add(c.frame, "page2");
        cardPanel.add(e.frame, "page3");
        
        cardLayout.show(cardPanel, "page1");
         
    }
    
  
    public static void creat_interfceGraphice(int x , int y)
    {
            // Auter de Frere Mammeri

        JPanel gg = new JPanel();
        gg.setLayout(new BorderLayout());
        gg.setBackground(Color.ORANGE);
        gg.setPreferredSize(new Dimension(x,55));

        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.setPreferredSize(new Dimension(x,50));
        p1.setBackground(Color.RED);
       

        JLabel l = new JLabel("SYSYEM D'INFORMATION");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);

        Font f = new Font( "Times New Roman", Font.BOLD, 25);
        l.setFont(f);
         // Southe of Frame that is whit
        JPanel n= new JPanel();
        n.setLayout(new BorderLayout());
        n.setPreferredSize(new Dimension(x,5));
        n.setBackground(Color.ORANGE);
        
         

        // Center of Frame
        JPanel c = new JPanel();
        c.setLayout(new BorderLayout());
        c.setBackground(Color.LIGHT_GRAY);
        
        JPanel imagePanel = new JPanel() {
            Image img = new ImageIcon("/home/mokhtar-mammeri/Desktop/BME2/src/Logo3.jpeg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        c.add(imagePanel, BorderLayout.CENTER);
        // WEST of Frame
        JPanel cc = new JPanel();
        cc.setPreferredSize(new Dimension(5,y));
        cc.setBackground(Color.ORANGE);
       
        
        
        JPanel v = new JPanel();    
        v.setLayout(new BorderLayout());
        v.setPreferredSize(new Dimension(205,y));
        v.setBackground(Color.ORANGE);

        JPanel k = new JPanel();
        k.setPreferredSize(new Dimension(5,y));
        k.setBackground(Color.ORANGE);
        
        
        // Button in the west
        JPanel g = new JPanel();
        g.setLayout(new BorderLayout());
        
        JPanel Buttons = new JPanel();
        Buttons.setLayout(new GridLayout(5,1,10,2));
        Buttons.setBackground(Color.RED);
        
        g.setPreferredSize(new Dimension(200,y));
       // g.setBackground(Color.GREEN);
        
        g.add(k,BorderLayout.EAST);
        v.add(g,BorderLayout.EAST);
        frame.add(v,BorderLayout.EAST);
        g.add(Buttons,BorderLayout.CENTER);

        // ADD compennts to frame
        frame.add(cc,BorderLayout.WEST);
        frame.add(c,BorderLayout.CENTER);
        frame.add(n,BorderLayout.SOUTH);
        

        p1.add(l,BorderLayout.CENTER);
        gg.add(p1,BorderLayout.NORTH);
        frame.add(gg,BorderLayout.NORTH);
                 

  

       /* Vector myb=new Vector();

        //--------------------
        try {
                
                String selectSQL = "SELECT * FROM users" ;
                ResultSet rs = DataBaseMangemet.select_Query(selectSQL);
                

                while (rs.next()) {
                    JOptionPane.showMessageDialog(null, rs.getString("name"));
                    myb.add(rs.getString("name"));
                                        
                }
                JList b = new JList(myb);
                c.add(b);

                DataBaseMangemet.conn.close();
            } catch (Exception e2) {
                System.out.println("❌ Connection failed.546");
                e2.printStackTrace();
            }
        //--------------------*/
                  
        
                
        creat_Button(y, Buttons);
        creat_menuBar();
        
    }
      public static void creat_menuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        
        String[] menu= { "المساعدة", "الإعدادات", "الحسابات", "السجلات", "معلومات", "الإضافات" };
        JMenu menu_arr [] = new JMenu [6];
        menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuBar.setBackground(Color.WHITE); // خلفية خفيفة
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        menuBar.setPreferredSize(new Dimension(frame.getWidth(), 25));
        for(int i =0;i<6;i++)
        {
           menu_arr[i] = new JMenu(menu[i]);
           menuBar.add(menu_arr[i]);
        }
        frame2.setJMenuBar(menuBar);
    }
    public static void creat_Button(int y , JPanel Buttons)
    {
        String []s = {"إدارة الحسابات","العمال","جدول الغيابات","g","v"};
        JButton b[] = new JButton[11];
        Font f = new Font("Arial", Font.BOLD, 25);
        
        for(int i =0;i<5;i++)
        {
            JButton a = new JButton(s[i]);
            a.setFont(f);
            b[i] = a;
            //a.setPreferredSize(new Dimension(150, 10));
            a.setBackground(Color.BLACK);
            a.setForeground(Color.WHITE);
            a.setBorderPainted(false);
            a.setFocusPainted(false);
            Buttons.add(a);
        }
        Actions(b);
  
    }

  /*  public static void main(String[] args) {
        new WelcomePage(800,550);
    }*/
    public static void Actions(JButton b[]) {
    
       frame2.addWindowListener(new WindowListener() {
    
            @Override
            public void windowClosing(WindowEvent e) {
                //JOptionPane.showMessageDialog(frame, "Event: Window Closing");
                 frame2.dispose();

            }

           @Override
           public void windowOpened(WindowEvent we) {
               //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
           }

           @Override
           public void windowClosed(WindowEvent we) {
               //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
               frame2.dispose();
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
            public void actionPerformed(ActionEvent e )
            {
                if(e.getSource()==b[0])
                {
                   
                       cardLayout.show(cardPanel, "page2");
                    
                }
                else
                {
                    if(e.getSource()==b[1])
                    {
                        cardLayout.show(cardPanel, "page3");
                        
                    }
                }    
                    
            }
        };
        b[0].addActionListener(ActionListener);
        b[1].addActionListener(ActionListener);
       
    }
    public static void main(String args[])
    {
         WelcomePage a = new WelcomePage(1000,700,6);
    }
}

