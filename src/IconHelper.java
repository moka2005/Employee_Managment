import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class IconHelper {
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();

    public static ImageIcon getIcon(String name, int width, int height) {
        String key = name + "_" + width + "_" + height;
        if (iconCache.containsKey(key)) {
            return iconCache.get(key);
        }

        ImageIcon icon = loadRawIcon(name);
        if (icon != null && icon.getImage() != null) {
            Image img = icon.getImage();
            if (width > 0 && height > 0) {
                Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaled);
            }
            iconCache.put(key, icon);
            return icon;
        }

        // Return generated placeholder icon if not found
        ImageIcon placeholder = createPlaceholderIcon(name, width > 0 ? width : 24, height > 0 ? height : 24);
        iconCache.put(key, placeholder);
        return placeholder;
    }

    public static ImageIcon getIcon(String name) {
        return getIcon(name, -1, -1);
    }

    private static ImageIcon loadRawIcon(String name) {
        if (name == null || name.trim().isEmpty()) return null;

        // Try direct file path if name is already an absolute or relative path that exists
        File f = new File(name);
        if (f.exists() && f.isFile()) {
            return new ImageIcon(f.getAbsolutePath());
        }

        // Extract just the filename if a path was passed
        String filename = new File(name).getName();

        // 1. Try local icons/ folder
        File localFile = new File("icons/" + filename);
        if (localFile.exists()) {
            return new ImageIcon(localFile.getAbsolutePath());
        }

        // 2. Try src/resources/icons/
        File srcFile = new File("src/resources/icons/" + filename);
        if (srcFile.exists()) {
            return new ImageIcon(srcFile.getAbsolutePath());
        }

        // 3. Try ClassPath resource
        java.net.URL resUrl = IconHelper.class.getResource("/resources/icons/" + filename);
        if (resUrl != null) {
            return new ImageIcon(resUrl);
        }
        resUrl = IconHelper.class.getResource("/icons/" + filename);
        if (resUrl != null) {
            return new ImageIcon(resUrl);
        }
        resUrl = IconHelper.class.getResource("/" + filename);
        if (resUrl != null) {
            return new ImageIcon(resUrl);
        }

        // 4. Try Downloads/BME2 fallback if exists
        File dlFile = new File("/home/mokhtar-mammeri/Downloads/BME2/icons/" + filename);
        if (dlFile.exists()) {
            return new ImageIcon(dlFile.getAbsolutePath());
        }

        return null;
    }

    private static ImageIcon createPlaceholderIcon(String label, int w, int h) {
        BufferedImage img = new BufferedImage(Math.max(w, 16), Math.max(h, 16), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(79, 70, 229, 180));
        g2.fillRoundRect(2, 2, w - 4, h - 4, 6, 6);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, h / 2)));
        String initial = label != null && !label.isEmpty() ? label.substring(0, 1).toUpperCase() : "•";
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(initial)) / 2;
        int y = ((h - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(initial, x, y);
        g2.dispose();
        return new ImageIcon(img);
    }
}
