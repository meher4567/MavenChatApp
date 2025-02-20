package backend;

import frontend.Message;
import server.MessageDAO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatServer {
    private static final int PORT = 12345;
    private static ConcurrentMap<String, PrintWriter> clients = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Boolean> userStatus = new ConcurrentHashMap<>();
    private static MessageDAO messageDAO = new MessageDAO();

    public static void main(String[] args) {
        System.out.println("The chat server is running...");
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(listener.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private String userName;
        private String receiver;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private boolean initialized = false;  // Flag to indicate client initialization

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Register the client
                userName = in.readLine();
                clients.put(userName, out);
                userStatus.put(userName, true);

                System.out.println(userName + " connected.");

                // Notify all users that this user has connected
                broadcast("Server", userName + " has joined the chat.");

                // Mark the client as initialized
                initialized = true;

                // Retrieve and send offline messages
                checkAndSendOfflineMessages();

                // Handle messages from this user
                String recipient;
                String message;
                while ((recipient = in.readLine()) != null && (message = in.readLine()) != null) {
                    System.out.println("Received message from " + userName + " to " + recipient + ": " + message);
                    if (userStatus.getOrDefault(recipient, false)) {
                        sendMessageToUser(recipient, userName, message);
                    } else {
                        // Store offline message
                        receiver=recipient;
                        messageDAO.addMessage(0, userName, recipient, message, new Timestamp(System.currentTimeMillis()), "offline");
                        System.out.println("Storing offline message from " + userName + " to " + recipient + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Remove the client from the map
                if (userName != null) {
                    clients.remove(userName);
                    userStatus.put(userName, false);
                    System.out.println(userName + " disconnected.");
                    broadcast("Server", userName + " has left the chat.");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendMessageToUser(String recipient, String sender, String message) {
            PrintWriter recipientOut = clients.get(recipient);
            if (recipientOut != null) {
                System.out.println("Sending message from " + sender + " to " + recipient + ": " + message);
                recipientOut.println(sender);
                recipientOut.println(message);
            } else {
                System.out.println("Failed to send message from " + sender + " to " + recipient + ": " + message + " (recipient not found)");
            }
        }

        private void broadcast(String sender, String message) {
            System.out.println("Broadcasting message from " + sender + ": " + message);
            for (PrintWriter client : clients.values()) {
                client.println(sender);
                client.println(message);
            }
        }

        private void checkAndSendOfflineMessages() {
            System.out.println("Checking for offline messages for " + userName);
            // Retrieve and send offline messages
            List<Message> offlineMessages = messageDAO.getMessages("Friend 2", userName);
            System.out.println("Found " + offlineMessages.size() + " offline messages for " + userName);
            for (Message message : offlineMessages) {
                System.out.println("Sending offline message from " + message.getSender() + " to " + userName + ": " + message.getText());
                out.println(message.getSender());
                out.println(message.getText());
            }
            messageDAO.deleteOfflineMessages(userName);
            System.out.println("Deleted offline messages for " + userName);
        }
    }
}
