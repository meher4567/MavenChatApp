package frontend;

import server.FriendDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EditFriendsComponent extends JPanel {

    private FriendsList friendsList;
    private JPanel friendsPanel;
    private FriendDAO friendDAO;
    private String username;

    public EditFriendsComponent(String username) {
        this.username = username;
        this.friendDAO = new FriendDAO();
        this.friendsList = new FriendsList(); // Initialize FriendsList internally

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        setBackground(Color.BLACK);

        // Create the friends panel
        friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.Y_AXIS));
        friendsPanel.setBackground(Color.BLACK);

        // Add the friends panel to the main panel
        add(new JScrollPane(friendsPanel), BorderLayout.CENTER);

        // Display the friends
        displayFriends();
    }

    private void displayFriends() {
        // Clear previous results
        friendsPanel.removeAll();

        // Fetch friends from the database
        List<String> friends = friendDAO.getFriends(username);
        // friendsList.getFriends().clear(); // Clear current list before setting new
        // data
        for (String friendName : friends) {
            System.out.println(friendName);
        }
        for (String friendName : friends) {
            friendsList.addFriend(new Friend(friendName));
        }

        for (Friend friend : friendsList.getList()) {
            JPanel friendPanel = new JPanel(new BorderLayout());
            friendPanel.setBackground(Color.BLACK);
            friendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JLabel friendLabel = new JLabel(friend.getName());
            friendLabel.setForeground(Color.GREEN);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.BLACK);

            JButton deleteButton = new JButton("Delete");
            deleteButton.setForeground(Color.GREEN);
            deleteButton.setBackground(Color.BLACK);
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean success1 = friendDAO.deleteFriend(username, friend.getName());
                    boolean success2 = friendDAO.deleteFriend(friend.getName(), username);
                    if (success1 && success2) {
                        friendsList.removeFriend(friend);
                        displayFriends();
                    } else {
                        JOptionPane.showMessageDialog(EditFriendsComponent.this, "Failed to delete friend.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            buttonPanel.add(deleteButton);

            friendPanel.add(friendLabel, BorderLayout.CENTER);
            friendPanel.add(buttonPanel, BorderLayout.EAST);

            friendsPanel.add(friendPanel);
        }

        // Refresh the friends panel
        friendsPanel.revalidate();
        friendsPanel.repaint();
    }
}
