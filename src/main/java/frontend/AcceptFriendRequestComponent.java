package frontend;

import server.FriendRequestDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AcceptFriendRequestComponent extends JPanel {

    private JPanel requestsPanel;
    private FriendsList friendsList;
    private List<String> pendingRequests;
    private JButton acceptAllButton;
    private FriendRequestDAO friendRequestDAO;
    private String username;

    public AcceptFriendRequestComponent(FriendsList friendsList, String username) {
        this.friendsList = friendsList;
        this.username = username;
        this.friendRequestDAO = new FriendRequestDAO();
        this.pendingRequests = new ArrayList<>();

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        setBackground(Color.BLACK);

        // Create the requests panel
        requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        requestsPanel.setBackground(Color.BLACK);

        // Add the requests panel to the main panel
        add(new JScrollPane(requestsPanel), BorderLayout.CENTER);

        // Create and add the Accept All button
        acceptAllButton = new JButton("Accept All");
        acceptAllButton.setForeground(Color.GREEN);
        acceptAllButton.setBackground(Color.BLACK);
        acceptAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptAllRequests();
            }
        });
        add(acceptAllButton, BorderLayout.SOUTH);

        displayRequests();
    }

    private void displayRequests() {
        // Clear previous requests
        requestsPanel.removeAll();

        // Fetch pending requests from the server
        pendingRequests = friendRequestDAO.getPendingRequests(username);

        for (String request : pendingRequests) {
            JPanel requestPanel = new JPanel(new BorderLayout());
            requestPanel.setBackground(Color.BLACK);
            requestPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JLabel requestLabel = new JLabel("Request from " + request);
            requestLabel.setForeground(Color.GREEN);

            JButton acceptButton = new JButton("Accept");
            acceptButton.setForeground(Color.GREEN);
            acceptButton.setBackground(Color.BLACK);
            acceptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    acceptRequest(request);
                }
            });

            requestPanel.add(requestLabel, BorderLayout.CENTER);
            requestPanel.add(acceptButton, BorderLayout.EAST);

            requestsPanel.add(requestPanel);
        }

        // Refresh the requests panel
        requestsPanel.revalidate();
        requestsPanel.repaint();
    }

    private void acceptRequest(String request) {
        String friendName = request.replace("Request from ", ""); // Extract friend name from request string
        boolean success = friendRequestDAO.acceptFriendRequest(friendName, username); // Use FriendRequestDAO to accept request
        if (success) {
            JOptionPane.showMessageDialog(this, friendName + " added as a friend!");
            displayRequests(); // Refresh the list of pending requests
        } else {
            JOptionPane.showMessageDialog(this, "Failed to accept friend request from " + friendName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acceptAllRequests() {
        boolean allSuccess = true;
        List<String> acceptedRequests = new ArrayList<>();

        for (String request : pendingRequests) {
            String friendName = request.replace("Request from ", "");
            boolean success = friendRequestDAO.acceptFriendRequest(friendName, username);
            if (success) {
                acceptedRequests.add(request);
            } else {
                allSuccess = false;
            }
        }

        pendingRequests.removeAll(acceptedRequests);
        if (allSuccess) {
            JOptionPane.showMessageDialog(this, "All friend requests accepted!");
        } else {
            JOptionPane.showMessageDialog(this, "Some friend requests could not be accepted.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        displayRequests();
    }
}
