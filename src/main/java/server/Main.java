package server;

import frontend.Message;

import java.sql.Timestamp;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Test adding messages
        MessageDAO messageDAO = new MessageDAO();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean added1 = messageDAO.addMessage(1, "user1", "friend1", "Hello, friend!", timestamp, "sent");
        boolean added2 = messageDAO.addMessage(2, "friend1", "user1", "Hi, user1!", timestamp, "received");
        System.out.println("Messages added: " + added1 + ", " + added2);

        // Test updating a message
        boolean updated = messageDAO.updateMessage(1, "user1", "friend1", "Hello, friend! (Updated)", timestamp, "sent");
        System.out.println("Message updated: " + updated);

        // Test deleting a message
        boolean deleted = messageDAO.deleteMessage(2);
        System.out.println("Message deleted: " + deleted);

        // Test retrieving all messages
        List<Message> messages = messageDAO.getMessages("user1", "friend1");
        System.out.println("All Messages:");
        for (Message message : messages) {
            System.out.println(message);
        }
    }
}
