package frontend;

import javax.swing.*;
import java.awt.*;

public class TestAcceptFriendRequestComponent {

    public static void main(String[] args) {
        // Create a dummy FriendsList object for testing
        FriendsList friendsList = new FriendsList();

        // Create the AcceptFriendRequestComponent
        AcceptFriendRequestComponent acceptFriendRequestComponent = new AcceptFriendRequestComponent(friendsList, "Friend8");

        // Create the main frame
        JFrame frame = new JFrame("Accept Friend Requests Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // Add the component to the frame
        frame.add(acceptFriendRequestComponent, BorderLayout.CENTER);

        // Display the frame
        frame.setVisible(true);
    }
}

