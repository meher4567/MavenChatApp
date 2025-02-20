package frontend;

import javafx.event.ActionEvent;

import javax.swing.*;

import backend.ChatClient;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import server.*;

public class ChatPanel extends JPanel {
    private JPanel messagePanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private static final String FULL_VIEW = "FullView";
    private static final String MINIMIZED_VIEW = "MinimizedView";
    private Map<String, MessageComponent> messageComponents;
    private String username;
    private ChatClient chatClient; // Single ChatClient instance
    private FriendDAO friendDAO;
    private JPanel friendListPanel;
    private JPanel messagingPanel;
    // Add these fields to the ChatPanel class
    private JDialog profileDialog;
    private JDialog addFriendsDialog;
    private JDialog acceptRequestsDialog;
    private JDialog editFriendsDialog;
    private List<String> MainfriendList;

    public ChatPanel(String username) {
        this.username = username;
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        this.messageComponents = new HashMap<>();
        MainfriendList = new ArrayList<>();
        this.chatClient = new ChatClient("localhost", 12345, username);
        friendDAO = new FriendDAO();
        initializeMessageComponents(); // Initialize message components for all friends
        loadedMessages();
        chatClient.start();

        JPanel fullViewPanel = createFullViewPanel();
        JPanel minimizedViewPanel = createMinimizedViewPanel();

        cardPanel.add(fullViewPanel, FULL_VIEW);
        cardPanel.add(minimizedViewPanel, MINIMIZED_VIEW);

        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() < 800) {
                    cardLayout.show(cardPanel, MINIMIZED_VIEW);
                } else {
                    cardLayout.show(cardPanel, FULL_VIEW);
                }
            }
        });
    }

    private void initializeMessageComponents() {
        // Assuming a list of friends is available
        List<String> friends = new ArrayList<>();
        friends = friendDAO.getFriends(username);
        for (String friend : friends) {
            MainfriendList.add(friend);
        }

        for (String friend : friends) {
            if (!friend.equals(username)) { // Skip if the friend name is the same as the username
                MessageComponent messageComponent = new MessageComponent(username, friend, chatClient);
                chatClient.addMessageListener(messageComponent); // Registering listener immediately
                chatClient.printListeners(); // Print listeners for debugging
                messageComponents.put(friend, messageComponent);
            }
        }
    }

    private void loadedMessages() {
        for (Map.Entry<String, MessageComponent> entry : messageComponents.entrySet()) {
            MessageComponent messageComponent = entry.getValue();
            messageComponent.loadMessages();
        }

    }

    private JPanel createFullViewPanel() {
        JPanel fullViewPanel = new JPanel(new BorderLayout());

        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setPreferredSize(new Dimension(50, getHeight()));

        JButton toggleButton = new JButton("☰");
        toggleButton.setPreferredSize(new Dimension(50, 50));
        toggleButton.addActionListener(e -> toggleNavigationMenu(navigationPanel));
        navigationPanel.add(toggleButton, BorderLayout.NORTH);

        JPanel navigationMenuPanel = new JPanel();
        navigationMenuPanel.setLayout(new BoxLayout(navigationMenuPanel, BoxLayout.Y_AXIS));
        JButton profileButton = new JButton("\uD83D\uDC64"); // Profile button
        profileButton.addActionListener(e -> showProfileComponent(profileButton));
        navigationMenuPanel.add(profileButton);
        navigationMenuPanel.add(Box.createVerticalStrut(10));

        JButton refreshButton = new JButton("\u21BA"); // Refresh button
        refreshButton.addActionListener(e -> refreshFriendList());
        navigationMenuPanel.add(refreshButton);
        navigationMenuPanel.add(Box.createVerticalStrut(10));

        JButton addFriendsButton = new JButton("\u271A"); // Add friends button
        addFriendsButton.addActionListener(e -> showAddFriendsComponent(addFriendsButton));
        navigationMenuPanel.add(addFriendsButton);
        navigationMenuPanel.add(Box.createVerticalStrut(10));

        JButton acceptRequestsButton = new JButton("\u2714"); // Accept friend requests button
        acceptRequestsButton.addActionListener(e -> showAcceptRequestsComponent(acceptRequestsButton));
        navigationMenuPanel.add(acceptRequestsButton);
        navigationMenuPanel.add(Box.createVerticalStrut(10));

        JButton editFriendsButton = new JButton("\u270D"); // Edit friends button
        editFriendsButton.addActionListener(e -> showEditFriendsComponent(editFriendsButton));
        navigationMenuPanel.add(editFriendsButton);

        navigationMenuPanel.setVisible(false);

        navigationPanel.add(navigationMenuPanel, BorderLayout.CENTER);

        JPanel searchAndFriendListPanel = new JPanel(new BorderLayout());

        JTextField searchField = new PlaceholderTextField("Search",messagingPanel);
        searchField.setPreferredSize(new Dimension(400, 50)); // Set size of search field
        searchField.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font size
        searchAndFriendListPanel.add(searchField, BorderLayout.EAST);
        searchAndFriendListPanel.add(searchField, BorderLayout.NORTH);

        friendListPanel = new JPanel();
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));

        List<String> friends = new ArrayList<>();
        try {
            friends = friendDAO.getFriends(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (friends == null) {
            System.out.println("No friends found or error retrieving friends.");
        }

        for (String friend : friends) {
            if (!friend.equals(username)) {
                JButton friendButton = new JButton(friend);
                friendButton.setMaximumSize(new Dimension(400, 50));
                friendButton.setPreferredSize(new Dimension(400, 50));
                friendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                friendButton.addActionListener(e -> {
                    showMessagesForFriend(friend);
                });
                friendListPanel.add(friendButton);
            }
            // Repaint the panel to reflect the new buttons
            friendListPanel.revalidate();
            friendListPanel.repaint();
        }

        JScrollPane friendScrollPane = new JScrollPane(friendListPanel);
        friendScrollPane.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        friendScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        friendScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        friendScrollPane.setPreferredSize(new Dimension(400, 8 * 50));

        searchAndFriendListPanel.add(friendScrollPane, BorderLayout.CENTER);

        messagePanel = new JPanel(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchAndFriendListPanel, BorderLayout.WEST);
        mainPanel.add(messagePanel, BorderLayout.CENTER);

        fullViewPanel.add(navigationPanel, BorderLayout.WEST);
        fullViewPanel.add(mainPanel, BorderLayout.CENTER);

        return fullViewPanel;
    }

    public void refreshFriendList() {
        List<String> updatedFriendList = friendDAO.getFriends(username);

        // Check for new friends
        for (String friend : updatedFriendList) {
            if (!MainfriendList.contains(friend)) {
                // New friend added, update UI
                addFriendButton(friend);
            }
        }

        // Check for removed friends
        for (String friend : MainfriendList) {
            if (!updatedFriendList.contains(friend)) {
                // Friend removed, update UI
                removeFriendButton(friend);
            }
        }

        // Update the friendList to reflect the changes
        MainfriendList = updatedFriendList;
    }

    private void addFriendButton(String friend) {
        JButton friendButton = createFriendButton(friend);
        friendListPanel.add(friendButton);
        // Repaint the panel to reflect the new button
        friendListPanel.revalidate();
        friendListPanel.repaint();
    }

    private void removeFriendButton(String friend) {
        Component[] components = friendListPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(friend)) {
                    // Remove the friend button
                    friendListPanel.remove(button);

                    // Remove the associated MessageComponent
                    MessageComponent messageComponent = messageComponents.get(friend);
                    if (messageComponent != null) {
                        chatClient.removeMessageListener(messageComponent); // Unregister listener
                        messageComponents.remove(friend);
                    }

                    // Repaint the panel to reflect the removed button
                    friendListPanel.revalidate();
                    friendListPanel.repaint();
                    break; // No need to continue searching once found and removed
                }
            }
        }
    }

    private JButton createFriendButton(String friend) {
        // Create MessageComponent and register it with chatClient
        MessageComponent messageComponent = new MessageComponent(username, friend, chatClient);
        chatClient.addMessageListener(messageComponent);
        chatClient.printListeners(); // For debugging
        messageComponents.put(friend, messageComponent);

        // Create and configure the friend button
        JButton friendButton = new JButton(friend);
        friendButton.setMaximumSize(new Dimension(400, 50));
        friendButton.setPreferredSize(new Dimension(400, 50));
        friendButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add action listener to the friend button
        friendButton.addActionListener(e -> {
            // Show messages for the friend
            showMessagesForFriend(friend);
        });

        return friendButton;
    }

    private JPanel createMinimizedViewPanel() {
        JPanel minimizedViewPanel = new JPanel(new BorderLayout());

        JPanel minimizedMessagePanel = new JPanel(new BorderLayout());

        JTextArea messageArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        minimizedMessagePanel.add(scrollPane, BorderLayout.CENTER);

        minimizedViewPanel.add(minimizedMessagePanel, BorderLayout.CENTER);

        return minimizedViewPanel;
    }

    private JButton createNavigationButton(String symbol) {
        JButton button = new JButton(symbol);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void toggleNavigationMenu(JPanel navigationPanel) {
        for (Component component : navigationPanel.getComponents()) {
            if (component instanceof JPanel) {
                component.setVisible(!component.isVisible());
            }
        }
        revalidate();
        repaint();
    }

    private void showMessagesForFriend(String friendName) {
        messagePanel.removeAll();

        MessageComponent messageComponent = messageComponents.get(friendName);
        if (messageComponent != null) {
            messagePanel.add(messageComponent, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    private void showProfileComponent(JButton profileButton) {
        if (profileDialog != null && profileDialog.isVisible()) {
            profileDialog.toFront();
            return;
        }

        profileDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Profile", Dialog.ModalityType.MODELESS);
        profileDialog.setSize(400, 300);
        profileDialog.setResizable(false);

        ProfileComponent profileComponent = new ProfileComponent(username, "it goes on..",
                new ImageIcon("src/images/download.png"));
        profileDialog.add(profileComponent);

        Point location = profileButton.getLocationOnScreen();
        profileDialog.setLocation(location.x + profileButton.getWidth(), location.y);

        profileDialog.setVisible(true);

        profileDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                profileDialog.dispose();
            }
        });

        profileComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
    }

    private void showAddFriendsComponent(JButton addFriendsButton) {
        if (addFriendsDialog != null && addFriendsDialog.isVisible()) {
            addFriendsDialog.toFront();
            return;
        }

        addFriendsDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add Friends",
                Dialog.ModalityType.MODELESS);
        addFriendsDialog.setSize(400, 300);
        addFriendsDialog.setResizable(false);

        FriendsList friendsList = new FriendsList();
        AddFriendsComponent addFriendsComponent = new AddFriendsComponent(friendsList, username);
        addFriendsDialog.add(addFriendsComponent);

        Point location = addFriendsButton.getLocationOnScreen();
        addFriendsDialog.setLocation(location.x + addFriendsButton.getWidth(), location.y);

        addFriendsDialog.setVisible(true);

        addFriendsDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addFriendsDialog.dispose();
            }
        });

        addFriendsDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
    }

    private void showAcceptRequestsComponent(JButton acceptRequestsButton) {
        if (acceptRequestsDialog != null && acceptRequestsDialog.isVisible()) {
            acceptRequestsDialog.toFront();
            return;
        }

        acceptRequestsDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Accept Requests",
                Dialog.ModalityType.MODELESS);
        acceptRequestsDialog.setSize(400, 300);
        acceptRequestsDialog.setResizable(false);

        FriendsList friendsList = new FriendsList();
        AcceptFriendRequestComponent acceptRequestsComponent = new AcceptFriendRequestComponent(friendsList, username);
        acceptRequestsDialog.add(acceptRequestsComponent);

        Point location = acceptRequestsButton.getLocationOnScreen();
        acceptRequestsDialog.setLocation(location.x + acceptRequestsButton.getWidth(), location.y);

        acceptRequestsDialog.setVisible(true);

        acceptRequestsDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                acceptRequestsDialog.dispose();
            }
        });

        acceptRequestsDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
    }

    private void showEditFriendsComponent(JButton editFriendsButton) {
        if (editFriendsDialog != null && editFriendsDialog.isVisible()) {
            editFriendsDialog.toFront();
            return;
        }

        editFriendsDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Friends",
                Dialog.ModalityType.MODELESS);
        editFriendsDialog.setSize(400, 300);
        editFriendsDialog.setResizable(false);

        EditFriendsComponent editFriendsComponent = new EditFriendsComponent(username);
        editFriendsDialog.add(editFriendsComponent);

        Point location = editFriendsButton.getLocationOnScreen();
        editFriendsDialog.setLocation(location.x + editFriendsButton.getWidth(), location.y);

        editFriendsDialog.setVisible(true);

        editFriendsDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editFriendsDialog.dispose();
            }
        });

        editFriendsDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
    }

    public static class PlaceholderTextField extends JTextField {

        private String placeholder;
        private JPanel messagePanel;  // This panel will be updated with the new panel
    
        public PlaceholderTextField(String placeholder, JPanel messagePanel) {
            this.placeholder = placeholder;
            this.messagePanel = messagePanel;
    
            addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String searchText = getText();
                    openMessageComponent(searchText);
                }
            });
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
    
            // Draw placeholder text if field is empty
            if (getText().isEmpty()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(getDisabledTextColor());
                g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                g2d.dispose();
            }
        }
    
        private void openMessageComponent(String searchText) {
            JPanel newPanel = new JPanel();
            newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
    
            JButton friendButton = new JButton(searchText);
            friendButton.setMaximumSize(new Dimension(400, 50));
            friendButton.setPreferredSize(new Dimension(400, 50));
            friendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            friendButton.addActionListener(e -> {
                // Define what happens when the friend button is clicked
                JOptionPane.showMessageDialog(this, "Button clicked for: " + searchText, "Info", JOptionPane.INFORMATION_MESSAGE);
            });
    
            newPanel.add(friendButton);
            
            // Update the message panel to show the new panel
            messagePanel.removeAll();
            messagePanel.add(newPanel, BorderLayout.CENTER);
            messagePanel.revalidate();
            messagePanel.repaint();
        }
    }
}
