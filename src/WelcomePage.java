
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class WelcomePage {

    static JFrame frame2;
    static JPanel frame;
    static int id;
    static CardLayout cardLayout;
    static JPanel cardPanel;
    

    WelcomePage(int x, int y, int id) {

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
       // Login2.frame.dispose();
        frame2 = new JFrame();
        frame = new JPanel();
        this.id = id;
        frame2.setSize(x, y);
        frame.setLayout(new BorderLayout());
        frame2.setLocationRelativeTo(null);
        creat_interfceGraphice(x, y);
        frame2.add(cardPanel);
        frame2.setVisible(true);

        count a = new count(cardLayout, cardPanel);
        Employee b = new Employee(cardLayout, cardPanel);
        Absence c = new Absence(cardLayout, cardPanel);
        Flow_Absence d = new Flow_Absence(cardLayout, cardPanel);
        Expenses e = new Expenses(cardLayout,cardPanel);
                           

        cardPanel.add(frame, "page1");
        cardPanel.add(a.frame, "page2");
        cardPanel.add(b.frame, "page3");
        cardPanel.add(c.frame, "page4");
        cardPanel.add(d.frame, "page5");
        cardPanel.add(e.frame, "page6");
        

        cardLayout.show(cardPanel, "page1");

    }

    public static void creat_interfceGraphice(int x, int y) {
        // Auter de Frere Mammeri

        JPanel gg = new JPanel();
        gg.setLayout(new BorderLayout());
        gg.setBackground(Color.ORANGE);
        gg.setPreferredSize(new Dimension(x, 55));

        JPanel p1 = new JPanel();
        Locale arabicLocale = Locale.FRANCE;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", arabicLocale);
        Date a = new Date();
        String arabicDate = sdf.format(a);
        p1.setLayout(new BorderLayout());
        p1.setPreferredSize(new Dimension(x, 50));
        p1.setBackground(Color.RED);

        JLabel l = new JLabel("SYSYEM D'INFORMATION");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);

        Font f = new Font("Times New Roman", Font.BOLD, 25);
        l.setFont(f);
        // Southe of Frame that is whit
        JPanel n = new JPanel();
        n.setLayout(new BorderLayout());
        n.setPreferredSize(new Dimension(x, 5));
        n.setBackground(Color.ORANGE);

        // Center of Frame
        JPanel c = new JPanel();
        c.setLayout(new BorderLayout());
        c.setBackground(Color.LIGHT_GRAY);

        JPanel imagePanel = new JPanel() {
            Image img = new ImageIcon("/home/mokhtar/Desktop/BME2/icons/welcompage.jpeg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        c.add(imagePanel, BorderLayout.CENTER);
        // WEST of Frame
        JPanel cc = new JPanel();
        cc.setPreferredSize(new Dimension(5, y));
        cc.setBackground(Color.ORANGE);

        JPanel v = new JPanel();
        v.setLayout(new BorderLayout());
        v.setPreferredSize(new Dimension(275, y));
        v.setBackground(Color.ORANGE);

        JPanel k = new JPanel();
        k.setPreferredSize(new Dimension(5, y));
        k.setBackground(Color.ORANGE);

        // Button in the west
        JPanel g = new JPanel();
        g.setLayout(new BorderLayout());

        JPanel Buttons = new JPanel();
        Buttons.setLayout(new GridLayout(6, 1, 10, 2));
        Buttons.setBackground(Color.RED);

        g.setPreferredSize(new Dimension(270, y));
        // g.setBackground(Color.GREEN);

        g.add(k, BorderLayout.EAST);
        v.add(g, BorderLayout.EAST);
        frame.add(v, BorderLayout.EAST);
        g.add(Buttons, BorderLayout.CENTER);

        // ADD compennts to frame
        frame.add(cc, BorderLayout.WEST);
        frame.add(c, BorderLayout.CENTER);
        frame.add(n, BorderLayout.SOUTH);
        
        JLabel today = new JLabel(arabicDate);
        today.setFont(f);

        p1.add(new JLabel("                                                               "),BorderLayout.WEST);
        p1.add(l, BorderLayout.CENTER);
        p1.add(today,BorderLayout.EAST);
        gg.add(p1, BorderLayout.NORTH);
        frame.add(gg, BorderLayout.NORTH);

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

    public static void creat_menuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);

        String[] menu = {"المساعدة", "الإعدادات", "الحسابات", "السجلات", "معلومات", "الإضافات"};
        JMenu menu_arr[] = new JMenu[6];
        menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuBar.setBackground(Color.WHITE); // خلفية خفيفة
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        menuBar.setPreferredSize(new Dimension(frame.getWidth(), 25));
        for (int i = 0; i < 6; i++) {
            menu_arr[i] = new JMenu(menu[i]);
            menuBar.add(menu_arr[i]);
        }
        frame2.setJMenuBar(menuBar);
    }

    public static void creat_Button(int y, JPanel Buttons) {
        String[] s = {"إدارة الحسابات", "إدارة العمال     ", "إدارة الحضور ", "متابعة الحضور ","إدارة المصاريف","السجلات"};
        ImageIcon icon2 [] = new  ImageIcon[6];
        String iconString[] = {"/home/mokhtar/Desktop/BME2/icons/counts.png","/home/mokhtar/Desktop/BME2/icons/employee.png","/home/mokhtar/Desktop/BME2/icons/absences.png","/home/mokhtar/Desktop/BME2/icons/Flowabsances.png","/home/mokhtar/Desktop/BME2/icons/absences.png","/home/mokhtar/Desktop/BME2/icons/absences.png"};
        JButton b[] = new JButton[11];
        Font f = new Font("Arial", Font.BOLD, 25);

        for (int i = 0; i < 6; i++) {
            icon2[i] = new ImageIcon(iconString[i]);
            JButton a = new JButton(s[i],icon2[i]);
            a.setFont(f);
            b[i] = a;
            //a.setPreferredSize(new Dimension(150, 10));
            a.setBackground(Color.BLACK);
            a.setForeground(Color.WHITE);

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
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == b[0]) {

                    cardLayout.show(cardPanel, "page2");

                } else {
                    if (e.getSource() == b[1]) {
                        Employee.imagePath="";
                        cardLayout.show(cardPanel, "page3");

                    } else {
                        if (e.getSource() == b[2]) {
                            Employee.imagePath="";
                            Absence.update();
                            cardLayout.show(cardPanel, "page4");
                        }
                        else
                        {
                            if(e.getSource()==b[3])
                            {
                                Employee.imagePath="";
                                Flow_Absence.a.setText("DA");
                                cardLayout.show(cardPanel, "page5");
                            }
                            else
                            {
                                if(e.getSource()==b[4])
                                {
                                    Expenses.update(true);
                                    cardLayout.show(cardPanel, "page6");
                                }    
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
        b[4].addActionListener(ActionListener);
    }

    public static void main(String args[]) {
        WelcomePage a = new WelcomePage(1800, 800, 6);
    }
}
