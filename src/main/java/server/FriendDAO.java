package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {

    public boolean addFriend(String username, String friendName) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return false;
            }

            String sql = "INSERT INTO friends (username, friendname) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, friendName);
                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getFriends(String username) {
        List<String> friends = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return friends;
            }

            String sql = "SELECT friendname FROM friends WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String friendName = resultSet.getString("friendname");
                        friends.add(friendName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public boolean updateFriend(String oldUsername, String oldFriendName, String newUsername, String newFriendName) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return false;
            }
            
            String sql = "UPDATE friends SET username = ?, friendname = ? WHERE username = ? AND friendname = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, newUsername);
                statement.setString(2, newFriendName);
                statement.setString(3, oldUsername);
                statement.setString(4, oldFriendName);
                
                int rowsUpdated = statement.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFriend(String username, String friendName) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return false;
            }
            
            String sql = "DELETE FROM friends WHERE username = ? AND friendname = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, friendName);
                
                int rowsDeleted = statement.executeUpdate();
                return rowsDeleted > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countFriendsByUsername(String username) {
        int count = 0;
        String query = "SELECT COUNT(*) AS friendCount FROM friends WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt("friendCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // You can log the exception and handle it according to your application's requirements
        }
        return count;
    }

    public boolean searchFriendByUsername(String username, String friendname) {
        boolean friendExists = false;
        String query = "SELECT COUNT(*) AS friendCount FROM friends WHERE username = ? AND friendname = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, friendname);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int friendCount = resultSet.getInt("friendCount");
                    friendExists = (friendCount > 0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // You can log the exception and handle it according to your application's requirements
        }
        return friendExists;
    }
}
