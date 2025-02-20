package frontend;

import javax.swing.*;

public class EditFriendsTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Edit Friends");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                EditFriendsComponent editFriendsComponent = new EditFriendsComponent( "Friend1");
                frame.setContentPane(editFriendsComponent);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
