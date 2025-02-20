package frontend;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ChatPanelDemoFriend2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chat Panel Demo - Friend 2");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            ChatPanel chatPanel = new ChatPanel("Friend7");

            frame.add(chatPanel);
            frame.setVisible(true);
        });
    }
}
