package frontend;


import javax.swing.*;
import java.awt.*;

public class ChatPanelDemoFriend4 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chat Panel Demo - Friend 4");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            ChatPanel chatPanel = new ChatPanel("Friend 4");

            frame.add(chatPanel);
            frame.setVisible(true);
        });
    }
}
