package server;

import frontend.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public boolean addMessage(int messageId, String sender, String receiver, String messageText, Timestamp timestamp, String messageType) {
        String sql = "INSERT INTO messages (message_id, sender, receiver, message_text, message_date, message_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, messageId);
            statement.setString(2, sender);
            statement.setString(3, receiver);
            statement.setString(4, messageText);
            statement.setTimestamp(5, timestamp);
            statement.setString(6, messageType);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> getMessages(String username, String friendname) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) ORDER BY message_date";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, friendname);
            statement.setString(3, friendname);
            statement.setString(4, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int messageId = resultSet.getInt("message_id");
                    String sender = resultSet.getString("sender");
                    String receiver = resultSet.getString("receiver");
                    String text = resultSet.getString("message_text");
                    String timeStamp = resultSet.getString("message_date");
                    String messageType = resultSet.getString("message_type");
                    messages.add(new Message(messageId, sender, receiver, text,messageType,timeStamp));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> LoadMessages(String username, String friendname, String messageType) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) AND message_type = ? ORDER BY message_date";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, friendname);
            statement.setString(3, friendname);
            statement.setString(4, username);
            statement.setString(5, messageType);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int messageId = resultSet.getInt("message_id");
                    String sender = resultSet.getString("sender");
                    String receiver = resultSet.getString("receiver");
                    String text = resultSet.getString("message_text");
                    String timeStamp = resultSet.getString("message_date");
                    String MessageType = resultSet.getString("message_type");
                    messages.add(new Message(messageId, sender, receiver, text, MessageType, timeStamp));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    

    public List<Message> getOfflineMessages(String username) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE receiver = ? AND message_type = 'offline' ORDER BY message_date";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int messageId = resultSet.getInt("message_id");
                    String sender = resultSet.getString("sender");
                    String receiver = resultSet.getString("receiver");
                    String text = resultSet.getString("message_text");
                    String timeStamp = resultSet.getString("message_date");
                    String messageType = resultSet.getString("message_type");
                    messages.add(new Message(messageId, sender, receiver, text, messageType, timeStamp));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public boolean deleteOfflineMessages(String username) {
        String sql = "DELETE FROM messages WHERE receiver = ? AND message_type = 'offline'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateMessage(int messageId, String sender, String receiver, String newText, Timestamp timestamp, String messageType) {
        String sql = "UPDATE messages SET message_text = ?, message_date = ?, message_type = ? WHERE message_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newText);
            statement.setTimestamp(2, timestamp);
            statement.setString(3, messageType);
            statement.setInt(4, messageId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMessage(int messageId) {
        String sql = "DELETE FROM messages WHERE message_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, messageId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
