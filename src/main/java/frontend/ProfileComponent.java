package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class ProfileComponent extends JPanel {

    private String username;
    private String about;
    private ImageIcon profileImage;

    public ProfileComponent(String username, String about, ImageIcon profileImage) {
        this.username = username;
        this.about = about;
        this.profileImage = profileImage;

        setPreferredSize(new Dimension(800, 600)); // Set preferred size to make the component non-resizable

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setOpaque(false);
        imagePanel.add(new JLabel(new ImageIcon(getCircularImage(profileImage.getImage(), 100, 100))));

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        usernameLabel.setForeground(Color.GREEN);

        JLabel aboutHeader = new JLabel("About:");
        aboutHeader.setFont(new Font("Arial", Font.BOLD, 16));
        aboutHeader.setForeground(Color.GREEN);

        JLabel aboutLabel = new JLabel(about);
        aboutLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        aboutLabel.setForeground(Color.GREEN);
        aboutLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel aboutPlaceholder = new JLabel();
        aboutPlaceholder.setPreferredSize(new Dimension(200, 20)); // Fixed size for "About" label

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setForeground(Color.GREEN);
        logoutButton.setBackground(Color.BLACK);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(imagePanel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(usernameLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        mainPanel.add(aboutHeader, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(aboutLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(aboutPlaceholder, gbc);

        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 0, 0, 20);
        mainPanel.add(logoutButton, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private Image getCircularImage(Image image, int width, int height) {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D.Double clip = new Ellipse2D.Double(0, 0, width, height);
        g2d.setClip(clip);
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return output;
    }
}
