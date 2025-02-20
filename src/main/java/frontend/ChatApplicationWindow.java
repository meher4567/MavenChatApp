package frontend;

import javax.swing.*;
import java.awt.*;

public class ChatApplicationWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    public ChatApplicationWindow() {
        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout); // Use CardLayout for mainPanel

        mainPanel.setBackground(Color.BLACK);

        // Create login and register panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);

        // Add login and register panels to the main panel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");

        // Show login panel initially
        cardLayout.show(mainPanel, "login");

        add(mainPanel);
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatApplicationWindow mainWindow = new ChatApplicationWindow();
            mainWindow.setVisible(true);
        });
    }
}
