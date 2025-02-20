package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import server.UserDAO;

public class LoginPanel extends JPanel {

    private ChatApplicationWindow mainApp;
    private JTextField usernameField;
    private UserDAO userDAO;

    public LoginPanel(ChatApplicationWindow mainApp) {
        this.mainApp = mainApp;
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout());

        // Create a panel for login components
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Border around the login panel
        loginPanel.setBackground(Color.WHITE); // Set background color of the login panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton switchToRegisterButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        loginPanel.add(switchToRegisterButton, gbc);

        // Add login panel to the center of the main panel
        add(loginPanel, BorderLayout.CENTER);

        // Set background color of the main panel to black
        setBackground(Color.BLACK);

        switchToRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showPanel("register");
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = getUsername();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Both fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    if (userDAO.usernameExists(username)) {
                        if (userDAO.loginUser(username, password)) {
                            JOptionPane.showMessageDialog(LoginPanel.this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            showChatPanel(username);
                        } else {
                            JOptionPane.showMessageDialog(LoginPanel.this, "Incorrect password", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginPanel.this, "Incorrect credentials", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginPanel.this, "An error occurred while logging in. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void showChatPanel(String username) {
        ChatPanel chatPanel = new ChatPanel(username);
        mainApp.getContentPane().removeAll();
        mainApp.getContentPane().add(chatPanel);
        mainApp.revalidate();
        mainApp.repaint();
    }

    public String getUsername() {
        return usernameField.getText();
    }
}
