package application.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private Database() {}

    public static Connection openConnection() throws SQLException {
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (user == null || user.isBlank() || password == null) {
            throw new SQLException("Set DB_USER and DB_PASSWORD in Run Configurations > Environment.");
        }
        String url = System.getenv("DB_URL");
        if (url == null || url.isBlank()) {
            url = "jdbc:mysql://localhost:3306/wk10_lab";
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static void checkConnection() throws SQLException {
        try (Connection connection = openConnection()) {
            if (!connection.isValid(5)) {
                throw new SQLException("Database connection validation failed.");
            }
        }
    }
}
