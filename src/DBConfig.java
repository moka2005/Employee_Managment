import java.io.*;
import java.util.Properties;

public class DBConfig {
    private static final String CONFIG_FILE = "db.properties";
    private static Properties props = new Properties();

    static {
        loadConfig();
    }

    public static void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            // Default configuration
            props.setProperty("db.host", "localhost");
            props.setProperty("db.port", "5432");
            props.setProperty("db.name", "Employee_Managment");
            props.setProperty("db.user", "postgres");
            props.setProperty("db.password", "2005");
            saveConfig();
        } else {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Failed to load db.properties: " + e.getMessage());
            }
        }
    }

    public static void saveConfig() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Database Connection Settings for Employee Management System");
        } catch (IOException e) {
            System.err.println("Failed to save db.properties: " + e.getMessage());
        }
    }

    public static String getHost() {
        return props.getProperty("db.host", "localhost");
    }

    public static String getPort() {
        return props.getProperty("db.port", "5432");
    }

    public static String getDbName() {
        return props.getProperty("db.name", "Employee_Managment");
    }

    public static String getUser() {
        return props.getProperty("db.user", "postgres");
    }

    public static String getPassword() {
        return props.getProperty("db.password", "2005");
    }

    public static String getJdbcUrl() {
        return "jdbc:postgresql://" + getHost() + ":" + getPort() + "/" + getDbName();
    }

    public static void update(String host, String port, String dbName, String user, String password) {
        props.setProperty("db.host", host);
        props.setProperty("db.port", port);
        props.setProperty("db.name", dbName);
        props.setProperty("db.user", user);
        props.setProperty("db.password", password);
        saveConfig();
    }
}
