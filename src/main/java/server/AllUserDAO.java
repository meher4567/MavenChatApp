package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class AllUserDAO {

    public boolean addUser(String username) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return false;
            }

            String sql = "INSERT INTO allUsers (username) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean userExists(String username) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return false;
            }

            String sql = "SELECT COUNT(*) FROM allUsers WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
