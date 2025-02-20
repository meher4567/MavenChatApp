package frontend;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ProfileComponentTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Sample data
            String username = "John Doe";
            String about = "Lorem ipsum dolor sit amet, consectetur.";
            ImageIcon profileImage = createPlaceholderImageIcon();

            // Create profile component
            JFrame frame = new JFrame("Profile Component Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ProfileComponent profileComponent = new ProfileComponent(username, about, profileImage);
            frame.add(profileComponent);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static ImageIcon createPlaceholderImageIcon() {
        // Specify the path to your image file
        String imagePath = "src/images/download.png";

        try {
            // Load the image from file
            Image image = new ImageIcon(imagePath).getImage();
            // Resize the image to fit the component
            Image scaledImage = image.getScaledInstance(200, 400, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
