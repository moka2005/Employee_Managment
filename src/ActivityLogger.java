import java.sql.Connection;
import java.sql.PreparedStatement;

public class ActivityLogger {
    public static void log(String actionType, String description) {
        String username = SessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            username = "admin";
        }

        String sql = "INSERT INTO activity_log (username, action_type, description) VALUES (?, ?, ?)";
        try (Connection conn = DBManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, actionType != null ? actionType : "عملية عامة");
            pstmt.setString(3, description != null ? description : "");
            pstmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("Notice writing to activity log: " + ex.getMessage());
        }
    }
}
