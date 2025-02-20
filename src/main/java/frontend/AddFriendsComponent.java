package frontend;

import server.AllUserDAO;
import server.FriendDAO;
import server.FriendRequestDAO;
import server.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddFriendsComponent extends JPanel {

    private JTextField searchField;
    private JButton searchButton;
    private JPanel resultsPanel;
    private FriendsList friendsList;
    private FriendDAO friendDAO;
    private FriendRequestDAO friendRequestDAO;
    private AllUserDAO userDAO;
    private String username;

    public AddFriendsComponent(FriendsList friendsList, String username) {
        this.friendsList = friendsList;
        this.username = username;
        this.friendDAO = new FriendDAO();
        this.friendRequestDAO = new FriendRequestDAO();
        this.userDAO = new AllUserDAO(); // Initialize UserDAO (allUserDAO)

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        setBackground(Color.BLACK);

        // Create the search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Create the results panel
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.BLACK);

        // Add the search bar and results panel to the main panel
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultsPanel), BorderLayout.CENTER);
    }

    private void performSearch() {
        // Clear previous results
        resultsPanel.removeAll();

        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (searchText.equals(username)) {
            JOptionPane.showMessageDialog(this, "You can't friend yourself.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the user is already a friend using FriendDAO
        boolean isAlreadyFriend = friendDAO.searchFriendByUsername(username, searchText);

        JPanel friendPanel = new JPanel(new BorderLayout());
        friendPanel.setBackground(Color.BLACK);
        friendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel friendLabel = new JLabel(searchText);
        friendLabel.setForeground(Color.GREEN);

        if (isAlreadyFriend) {
            JLabel alreadyFriendLabel = new JLabel("Already Friends");
            alreadyFriendLabel.setForeground(Color.GRAY);
            friendPanel.add(alreadyFriendLabel, BorderLayout.EAST);
        } else {
            // Check if the user exists in the allUsers table
            boolean userExists = userDAO.userExists(searchText);
            if (userExists) {
                JButton addButton = new JButton("Add");
                addButton.setForeground(Color.GREEN);
                addButton.setBackground(Color.BLACK);
                addButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addFriend(searchText);
                    }
                });

                friendPanel.add(addButton, BorderLayout.EAST);
            } else {
                JOptionPane.showMessageDialog(this, "No users found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        friendPanel.add(friendLabel, BorderLayout.CENTER);
        resultsPanel.add(friendPanel);

        // Refresh the results panel
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void addFriend(String friendName) {
        boolean success = friendRequestDAO.sendFriendRequest(username, friendName);
        if (success) {
            JOptionPane.showMessageDialog(this, "Friend request sent to " + friendName + "!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send friend request to " + friendName + ".", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
