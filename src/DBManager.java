import javax.swing.*;
import java.sql.*;

public class DBManager {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DBConfig.getJdbcUrl(),
            DBConfig.getUser(),
            DBConfig.getPassword()
        );
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static void initializeDatabase() {
        String[] createTableStatements = {
            "CREATE TABLE IF NOT EXISTS users (" +
            "    id     SERIAL       PRIMARY KEY," +
            "    FirstN VARCHAR(50)  NOT NULL UNIQUE," +
            "    pass   VARCHAR(100) NOT NULL," +
            "    role   VARCHAR(20)  DEFAULT 'USER'" +
            ");",

            "ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'USER';",

            "CREATE TABLE IF NOT EXISTS employee (" +
            "    id_employee    VARCHAR(20) PRIMARY KEY," +
            "    nom            VARCHAR(50) NOT NULL," +
            "    prenom         VARCHAR(50) NOT NULL," +
            "    date_naissance DATE," +
            "    date_embauche  DATE," +
            "    date_depart    DATE DEFAULT '2030-12-12'," +
            "    telephone      VARCHAR(20) UNIQUE," +
            "    post           VARCHAR(50)," +
            "    salaire        INTEGER," +
            "    etat           VARCHAR(20)," +
            "    activ_emp      SMALLINT DEFAULT 1" +
            ");",

            "CREATE TABLE IF NOT EXISTS date_emp (" +
            "    date_id       SERIAL      PRIMARY KEY," +
            "    id_employee   VARCHAR(20) NOT NULL REFERENCES employee(id_employee) ON DELETE CASCADE," +
            "    date_embauche DATE        NOT NULL," +
            "    \"date_départ\" DATE        DEFAULT '2030-12-12'" +
            ");",

            "CREATE TABLE IF NOT EXISTS photo_path (" +
            "    photo_id     SERIAL      PRIMARY KEY," +
            "    employee_id  VARCHAR(20) NOT NULL UNIQUE REFERENCES employee(id_employee) ON DELETE CASCADE," +
            "    path         TEXT        NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS absences (" +
            "    absence_id    SERIAL      PRIMARY KEY," +
            "    id_employee   VARCHAR(20) NOT NULL REFERENCES employee(id_employee) ON DELETE CASCADE," +
            "    absence_date  DATE        NOT NULL," +
            "    reason        VARCHAR(255)," +
            "    state         VARCHAR(20) NOT NULL," +
            "    paying_state  VARCHAR(20) NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS expense (" +
            "    product_id     SERIAL       PRIMARY KEY," +
            "    num            INTEGER," +
            "    product_name   VARCHAR(50)  NOT NULL," +
            "    product_amount VARCHAR(20)  NOT NULL," +
            "    product_price  VARCHAR(20)  NOT NULL," +
            "    total          VARCHAR(20)  NOT NULL," +
            "    input_date     DATE         NOT NULL," +
            "    name_expenses  VARCHAR(50)  NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS activity_log (" +
            "    log_id      SERIAL PRIMARY KEY," +
            "    username    VARCHAR(50) NOT NULL," +
            "    action_type VARCHAR(50) NOT NULL," +
            "    description TEXT NOT NULL," +
            "    log_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");",

            "INSERT INTO users (FirstN, pass, role) VALUES ('admin', 'admin', 'ADMIN') ON CONFLICT (FirstN) DO UPDATE SET role = 'ADMIN';"
        };

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : createTableStatements) {
                try {
                    stmt.execute(sql);
                } catch (SQLException ex) {
                    System.err.println("Notice executing schema: " + ex.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Database auto-init failed: " + e.getMessage());
        }
    }

    public static boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "خطأ في قاعدة البيانات: " + e.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static boolean execute(String sql) {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "خطأ في قاعدة البيانات: " + e.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing DB Connection...");
        boolean connected = testConnection();
        System.out.println("Connection status: " + (connected ? "CONNECTED ✅" : "FAILED ❌"));
        if (connected) {
            System.out.println("Initializing Database Schema...");
            initializeDatabase();
            System.out.println("Database Schema Initialized Successfully ✅");
        }
    }
}
