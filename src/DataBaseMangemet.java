
import javax.swing.*;
import java.sql.*;

public class DataBaseMangemet
{
    static Connection conn = null;
    public  static Connection getConnection()
    {
        try
        {
            Class.forName("org.postgresql.Driver"); // <-- أضف هذا السطر هنا
            conn = DriverManager.getConnection("jdbc:postgresql://172.26.215.146:5432/project", "postgres", "2005");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            System.out.println("❌ Connection failed.رلاىرلاى");
        }
        return conn;
    }
    public static void ClosedConnection(){
        try {
            conn.close();
        }
        catch (SQLException e){}
    }
    public static boolean ExexcuteStatement(String Sql)
    {
        try
        {
            getConnection();
            PreparedStatement pstmt = conn.prepareStatement(Sql);
            pstmt.execute();
            return true;
            //Statement stmt = conn.createStatement();
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage(),"ExexcuteStatement Fonction ",JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    public static ResultSet select_Query(String Sql)
    {
        try
        {
            getConnection();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(Sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"select_Query Fonction",JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }

}
