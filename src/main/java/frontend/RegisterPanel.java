package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import server.AllUserDAO;
import server.UserDAO;

public class RegisterPanel extends JPanel {

    private ChatApplicationWindow mainApp;
    private UserDAO userDAO;
    private AllUserDAO allUserDAO;

    public RegisterPanel(ChatApplicationWindow mainApp) {
        this.mainApp = mainApp;
        this.userDAO = new UserDAO();
        allUserDAO=new AllUserDAO();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JLabel emailLabel = new JLabel("Email:");
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JPasswordField confirmPasswordField = new JPasswordField(15);
        JTextField emailField = new JTextField(15);
        JButton registerButton = new JButton("Register");
        JButton switchToLoginButton = new JButton("Back to Login");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(emailLabel, gbc);

        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        add(registerButton, gbc);

        gbc.gridy = 5;
        add(switchToLoginButton, gbc);

        switchToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showPanel("login");
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String email = emailField.getText();

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterPanel.this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterPanel.this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    boolean success = userDAO.registerUser(username, password, email);
                    allUserDAO.addUser(username);
                    if (success) {
                        JOptionPane.showMessageDialog(RegisterPanel.this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        mainApp.showPanel("chat");
                    } else {
                        JOptionPane.showMessageDialog(RegisterPanel.this, "Username or email already registered", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterPanel.this, "An error occurred while registering. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
