package frontend;


import javax.swing.*;

public class AddFriendsTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String username= "Friend1";
                JFrame frame = new JFrame("Add Friends");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                FriendsList friendsList = new FriendsList();
                frame.setContentPane(new AddFriendsComponent(friendsList,username));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
