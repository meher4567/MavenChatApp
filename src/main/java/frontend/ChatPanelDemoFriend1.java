package frontend;


import javax.swing.*;

public class ChatPanelDemoFriend1 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chat Panel Demo - Friend 1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            ChatPanel chatPanel = new ChatPanel("Meher");

            frame.add(chatPanel);
            frame.setVisible(true);
        });
    }
}
