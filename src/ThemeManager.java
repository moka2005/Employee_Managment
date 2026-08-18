import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ThemeManager {
    private static final String THEME_FILE = "theme.properties";
    private static boolean darkMode = false;
    private static final List<Runnable> listeners = new ArrayList<>();

    static {
        loadTheme();
    }

    public static void loadTheme() {
        File f = new File(THEME_FILE);
        if (f.exists()) {
            Properties p = new Properties();
            try (InputStream in = new FileInputStream(f)) {
                p.load(in);
                darkMode = Boolean.parseBoolean(p.getProperty("theme.dark", "false"));
            } catch (IOException ignored) {}
        }
    }

    public static void saveTheme() {
        Properties p = new Properties();
        p.setProperty("theme.dark", String.valueOf(darkMode));
        try (OutputStream out = new FileOutputStream(THEME_FILE)) {
            p.store(out, "UI Theme Configuration (Dark / Light Mode)");
        } catch (IOException ignored) {}
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean dark) {
        darkMode = dark;
        saveTheme();
        IconHelper.clearCache();
        notifyListeners();
    }

    public static void toggleTheme() {
        setDarkMode(!darkMode);
    }

    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private static void notifyListeners() {
        for (Runnable r : listeners) {
            try {
                r.run();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
