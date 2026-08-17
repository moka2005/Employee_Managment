import javax.swing.*;
import java.io.File;
import java.sql.*;

public class DBManager {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver notice: " + e.getMessage());
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL Driver notice: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (DBConfig.isSqlite()) {
            File dbFile = new File(DBConfig.getSqlitePath());
            if (dbFile.getParentFile() != null && !dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }
            return DriverManager.getConnection(DBConfig.getJdbcUrl());
        } else {
            return DriverManager.getConnection(
                DBConfig.getJdbcUrl(),
                DBConfig.getUser(),
                DBConfig.getPassword()
            );
        }
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
        if (DBConfig.isSqlite()) {
            initSqliteSchema();
        } else {
            initPostgresSchema();
        }
    }

    private static void initSqliteSchema() {
        String[] stmts = {
            "CREATE TABLE IF NOT EXISTS users (" +
            "    id       INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    FirstN   TEXT    NOT NULL UNIQUE," +
            "    pass     TEXT    NOT NULL," +
            "    role     TEXT    DEFAULT 'USER'" +
            ");",

            "CREATE TABLE IF NOT EXISTS employee (" +
            "    id_employee    TEXT PRIMARY KEY," +
            "    nom            TEXT NOT NULL," +
            "    prenom         TEXT NOT NULL," +
            "    date_naissance DATE," +
            "    date_embauche  DATE," +
            "    date_depart    DATE DEFAULT '2030-12-12'," +
            "    telephone      TEXT UNIQUE," +
            "    post           TEXT," +
            "    salaire        INTEGER," +
            "    etat           TEXT," +
            "    activ_emp      INTEGER DEFAULT 1" +
            ");",

            "CREATE TABLE IF NOT EXISTS date_emp (" +
            "    date_id       INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    id_employee   TEXT NOT NULL REFERENCES employee(id_employee) ON DELETE CASCADE," +
            "    date_embauche DATE NOT NULL," +
            "    \"date_départ\" DATE DEFAULT '2030-12-12'" +
            ");",

            "CREATE TABLE IF NOT EXISTS photo_path (" +
            "    photo_id     INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    employee_id  TEXT NOT NULL UNIQUE REFERENCES employee(id_employee) ON DELETE CASCADE," +
            "    path         TEXT NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS absences (" +
            "    absence_id    INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    id_employee   TEXT NOT NULL REFERENCES employee(id_employee) ON DELETE CASCADE," +
            "    absence_date  DATE NOT NULL," +
            "    reason        TEXT," +
            "    state         TEXT NOT NULL," +
            "    paying_state  TEXT NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS expense (" +
            "    product_id     INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    num            INTEGER," +
            "    product_name   TEXT NOT NULL," +
            "    product_amount TEXT NOT NULL," +
            "    product_price  TEXT NOT NULL," +
            "    total          TEXT NOT NULL," +
            "    input_date     DATE NOT NULL," +
            "    name_expenses  TEXT NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS activity_log (" +
            "    log_id      INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    username    TEXT NOT NULL," +
            "    action_type TEXT NOT NULL," +
            "    description TEXT NOT NULL," +
            "    log_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");",

            "INSERT OR IGNORE INTO users (FirstN, pass, role) VALUES ('admin', 'admin', 'ADMIN');"
        };

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : stmts) {
                try {
                    stmt.execute(sql);
                } catch (SQLException ex) {
                    System.err.println("Notice executing SQLite schema: " + ex.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLite auto-init failed: " + e.getMessage());
        }
    }

    private static void initPostgresSchema() {
        String[] stmts = {
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
            for (String sql : stmts) {
                try {
                    stmt.execute(sql);
                } catch (SQLException ex) {
                    System.err.println("Notice executing PostgreSQL schema: " + ex.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("PostgreSQL auto-init failed: " + e.getMessage());
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
            UITheme.showThemedMessage(null, "خطأ في قاعدة البيانات: " + e.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static boolean execute(String sql) {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            UITheme.showThemedMessage(null, "خطأ في قاعدة البيانات: " + e.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
