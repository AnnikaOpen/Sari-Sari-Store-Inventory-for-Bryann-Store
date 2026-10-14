package sarisaristore;

import java.sql.*;

/**
 * Connects to the team's MySQL database "sari_sari_store" (run the .sql file in
 * phpMyAdmin first). The app only adds one small table, LOGIN_ACCOUNT, for the login screen.
 */
public class DBConnection {
    // ---- Change these only if your MySQL settings are different ----
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB   = "sari_sari_store";
    private static final String USER = "root";
    private static final String PASS = "";      // XAMPP default password is empty

    private static boolean ready = false;

    public static Connection getConnection() throws SQLException {
        if (!ready) init();
        return open();
    }

    private static Connection open() throws SQLException {
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try {
            return DriverManager.getConnection(url, USER, PASS);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) {   // unknown database
                throw new SQLException("Database '" + DB + "' was not found.\n"
                        + "Run the .sql file in phpMyAdmin first.", e);
            }
            throw e;
        }
    }

    private static synchronized void init() throws SQLException {
        if (ready) return;
        try (Connection c = open()) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS LOGIN_ACCOUNT ("
                        + "Username VARCHAR(50) PRIMARY KEY, "
                        + "Password VARCHAR(64) NOT NULL)");
            }
            boolean empty;
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM LOGIN_ACCOUNT")) {
                rs.next();
                empty = rs.getInt(1) == 0;
            }
            if (empty) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("INSERT INTO LOGIN_ACCOUNT (Username, Password) "
                            + "VALUES ('admin', SHA2('admin123', 256))");
                }
            }
        }
        ready = true;
    }
}
