import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Close the SQLite singleton connection cleanly when the JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(DBManager::closeAll, "sqlite-shutdown"));

        SwingUtilities.invokeLater(() -> {
            try {
                // CrossPlatform Look and Feel ensures complete visual consistency across OS platforms
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                UITheme.applyThemeToUIManager();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Initialize DB configuration & connection
            DBConfig.loadConfig();
            DBManager.initializeDatabase();

            // Launch modern Login frame
            new LoginFrame(1000, 650);
        });
    }
}
