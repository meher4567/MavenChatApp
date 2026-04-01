package server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final DataSource dataSource;
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/chatting_app";

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(readConfig("chat.db.url", "CHAT_DB_URL", DEFAULT_DB_URL));
        config.setUsername(requireConfig("chat.db.user", "CHAT_DB_USER"));
        config.setPassword(requireConfig("chat.db.password", "CHAT_DB_PASSWORD"));

        // Configure other HikariCP properties as needed

        dataSource = new HikariDataSource(config);
    }

    private static String readConfig(String propertyKey, String envKey, String defaultValue) {
        String fromProperty = System.getProperty(propertyKey);
        if (fromProperty != null && !fromProperty.trim().isEmpty()) {
            return fromProperty.trim();
        }

        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.trim().isEmpty()) {
            return fromEnv.trim();
        }

        return defaultValue;
    }

    private static String requireConfig(String propertyKey, String envKey) {
        String value = readConfig(propertyKey, envKey, null);
        if (value == null) {
            throw new IllegalStateException(
                    "Missing database configuration. Set JVM property '" + propertyKey
                            + "' or environment variable '" + envKey + "'.");
        }
        return value;
    }

    // Method to get the connection
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Method to close the connection
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
