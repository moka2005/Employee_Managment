public class SessionManager {
    private static int userId = 0;
    private static String username = "زائر";
    private static String role = "USER"; // "ADMIN" or "USER"

    public static void setSession(int id, String user, String userRole) {
        userId = id;
        username = user != null ? user : "مستخدم";
        role = userRole != null ? userRole.toUpperCase() : "USER";
    }

    public static void clearSession() {
        userId = 0;
        username = "زائر";
        role = "USER";
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(username);
    }

    public static String getRoleDisplay() {
        return isAdmin() ? "مدير النظام" : "مستخدم";
    }
}
