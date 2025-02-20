package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDAO {

    public boolean sendFriendRequest(String sender, String receiver) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return false;
            }

            String sql = "INSERT INTO friend_requests (sender, receiver) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, sender);
                statement.setString(2, receiver);
                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getPendingRequests(String receiver) {
        List<String> requests = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return requests;
            }

            String sql = "SELECT sender FROM friend_requests WHERE receiver = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, receiver);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        requests.add(resultSet.getString("sender"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public boolean acceptFriendRequest(String sender, String receiver) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                return false;
            }

            String deleteSql = "DELETE FROM friend_requests WHERE sender = ? AND receiver = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                deleteStatement.setString(1, sender);
                deleteStatement.setString(2, receiver);
                int rowsDeleted = deleteStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    FriendDAO friendDAO = new FriendDAO();
                    friendDAO.addFriend(sender, receiver);
                    return friendDAO.addFriend(receiver, sender);
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
