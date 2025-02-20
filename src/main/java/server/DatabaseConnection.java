package server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final DataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/chatting_app");
        config.setUsername("root");
        config.setPassword("11054");

        // Configure other HikariCP properties as needed

        dataSource = new HikariDataSource(config);
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
