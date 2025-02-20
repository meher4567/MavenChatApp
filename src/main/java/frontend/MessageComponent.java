package frontend;

import backend.ChatClient;
import server.MessageDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageComponent extends JPanel implements MessageListener {

    private String friendName;
    private JPanel messagePanel; // Panel to hold message labels
    private JTextField messageField;
    private JLabel statusLabel; // Status label
    private PlaceholderTextField searchField; // Search field
    private List<Message> messages;
    private ChatClient chatClient;
    private String userName;
    MessageDAO messageDAO;
    private String Status;

    public MessageComponent(String userName, String friendName, ChatClient chatClient) {
        this.userName = userName;
        this.friendName = friendName;
        this.chatClient = chatClient;
        messages = new ArrayList<>();
        messageDAO = new MessageDAO();
        Status="offline";

        setLayout(new BorderLayout(10, 10)); // Add padding

        JLabel friendLabel = new JLabel(friendName);
        friendLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Increase font size and make bold

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(friendLabel, BorderLayout.NORTH);

        // Status label initialization
        statusLabel = new JLabel("Status: " + Status);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Font size for status
        headerPanel.add(statusLabel, BorderLayout.SOUTH);

        messagePanel = new JPanel(); // Initialize message panel
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS)); // Vertical layout
        JScrollPane scrollPane = new JScrollPane(messagePanel); // Add scroll pane to hold message panel
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Set custom scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new ThinScrollBarUI());

        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(200, 40)); // Adjust width and increase height
        messageField.setFont(new Font("Arial", Font.PLAIN, 16)); // Increase font size

        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 40)); // Adjust width and increase height
        sendButton.setFont(new Font("Arial", Font.PLAIN, 16)); // Increase font size
        sendButton.setBackground(Color.BLUE); // Change background color
        sendButton.setForeground(Color.WHITE); // Change text color

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5)); // Add padding
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Action listener for the send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Action listener for the message field to send message on Enter key press
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Set border and padding for the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Add this MessageComponent as a listener to the ChatClient
        if (this.chatClient != null) {
            this.chatClient.addMessageListener(this);
            System.out.println("**MessageComponent added as listener to ChatClient.");
        }

        // Register a window listener to remove the MessageListener when the window is
        // closed
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            ((JFrame) window).addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (chatClient != null) {
                        chatClient.removeMessageListener(MessageComponent.this);
                        System.out.println("**MessageComponent removed as listener from ChatClient.");
                    }
                }
            });
        }
    }

    public String getUsername() {
        return userName;
    }

    public void setStatus(String status) {
        Status = status;
        statusLabel.setText("Status: " + status);
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
        chatClient.addMessageListener(this);
        System.out.println("**ChatClient set in MessageComponent and listener added.");
    }

    // Method to send a message
    private void sendMessage() {
        String messageText = messageField.getText().trim();
        if (!messageText.isEmpty()) {
            System.out.println("Sending message to " + friendName + ": " + messageText); // Debug statement
            chatClient.sendMessage(friendName, messageText); // Send the message using ChatClient
            Message message = new Message(userName, friendName, messageText, "sent"); // Assuming sender is "Me"
            addMessage(message);
            try {
                int messageId = message.getMessageId();
                String receiver = message.getReceiver();
                if (receiver.equals("Me")) {
                    receiver = friendName;
                }
                String text = message.getText();
                Timestamp timeStamp = message.getTimestamp_exact();
                String messageType = message.getMessageType();
                messageDAO.addMessage(messageId, userName, receiver, text, timeStamp, messageType);
            } catch (Exception e) {
                System.out.println("Unable to send to Backend!");
                e.printStackTrace();
            }
            clearMessageField();
            scrollToBottom(); // Scroll to the bottom after adding a new message
        } else {
            System.out.println("Message text is empty, not sending."); // Debug statement for empty message
        }
    }

    // Method to add a message to the message panel
    public void addMessage(Message message) {
        messages.add(message);
        MessageLabel label = new MessageLabel(message, userName); // Create a message label for the new message
        messagePanel.add(label); // Add the message label to the message panel
        scrollToBottom();
        revalidate(); // Revalidate the message panel to update its layout
        repaint(); // Repaint the message panel to reflect the changes
        System.out.println("**Message added to MessageComponent: " + message.getSender() + ": " + message.getText()); // Debug
                                                                                                                      // statement
    }

    // Method to clear the message field after sending a message
    private void clearMessageField() {
        messageField.setText("");
    }

    // Method to scroll to the bottom of the scroll pane
    private void scrollToBottom() {
        JScrollBar verticalScrollBar = ((JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class,
                messagePanel)).getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    // Method to get the friend name associated with this message component
    public String getFriendName() {
        return friendName;
    }

    // Method to set focus on the message field
    public void setMessageFieldFocus() {
        messageField.requestFocus();
    }

    public void sendAllMessages() {
        MessageDAO messageDAO = new MessageDAO();
        for (Message message : messages) {
            int messageId = message.getMessageId();
            String sender = message.getSender();
            String receiver = message.getReceiver();
            String messageText = message.getText();
            String messageType = message.getMessageType();
            Timestamp timestamp = message.getTimestamp_exact();

            // Call addMessage method of MessageDAO to send the message to the backend
            boolean success = messageDAO.addMessage(messageId, sender, receiver, messageText, timestamp, messageType);

            // Handle success or failure based on the boolean returned by addMessage
            if (success) {
                System.out.println("Message sent successfully to the backend.");
            } else {
                System.out.println("Failed to send message to the backend.");
            }
        }
    }

    public void loadMessages() {
        System.out.println("<<<Getting Messages>>>");
        MessageDAO messageDAO = new MessageDAO();
        List<Message> loadedMessages1 = messageDAO.LoadMessages(userName, friendName, "sent");
        List<Message> loadedMessages2 = messageDAO.LoadMessages(friendName, userName, "received");
        List<Message> mergedMessages = new ArrayList<>(loadedMessages1);
        mergedMessages.addAll(loadedMessages2);
        Set<Message> messageSet = new HashSet<>(mergedMessages);
        mergedMessages.clear();
        mergedMessages.addAll(messageSet);

        // Sort the merged messages based on timestamp
        Collections.sort(mergedMessages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                // Compare timestamps and return the result
                return m1.getTimestamp_exact().compareTo(m2.getTimestamp_exact());
            }
        });
        for (Message message : mergedMessages) {
            System.out.println("Message ID: " + message.getMessageId());
            System.out.println("Sender: " + message.getSender());
            System.out.println("Receiver: " + message.getReceiver());
            System.out.println("Text: " + message.getText());
            System.out.println("Timestamp: " + message.getTimeStamp());
            System.out.println("Message Type: " + message.getMessageType());
            System.out.println("---------------------------");
        }

        for (Message message : mergedMessages) {
            addMessage(message);
        }
    }

    // Custom JTextField with placeholder text support

    @Override
    public void onMessageReceived(String sender, String message) {
        if (sender.equals(friendName) && !sender.equals(userName)) {
            System.out.println("**Message received in MessageComponent from " + sender + ": " + message);
            Message receivedMessage = new Message(sender, userName, message, "received");
            SwingUtilities.invokeLater(() -> addMessage(receivedMessage));
            try {
                int messageId = receivedMessage.getMessageId();
                String Sender = receivedMessage.getSender();
                String text = receivedMessage.getText();
                Timestamp timeStamp = receivedMessage.getTimestamp_exact();
                //messageDAO.addMessage(messageId, Sender, userName, text, timeStamp, "received");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewClientConnected(String client) {
        // Handle new client connection if necessary
    }

    @Override
    public String toString() {
        return "MessageComponent for " + userName + " chatting with " + friendName;
    }
}
