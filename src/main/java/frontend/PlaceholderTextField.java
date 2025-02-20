package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderTextField extends JTextField {

    private String placeholder;
    private ImageIcon icon;
    private float scaleFactor = 1.5f; // Scale factor for width

    public PlaceholderTextField(final String placeholder) {
        this(placeholder, null);
    }

    public PlaceholderTextField(final String placeholder, ImageIcon icon) {
        this.placeholder = placeholder;
        this.icon = icon;
        setForeground(Color.GRAY);

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(Color.GRAY);
                }
            }
        });

        setText(placeholder);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Paint the icon if it exists
        if (icon != null) {
            int iconWidth = (int) (icon.getIconWidth() * scaleFactor); // Adjusted width
            int iconHeight = icon.getIconHeight();
            int x = 5; // Adjust position
            int y = (getHeight() - iconHeight) / 2;

            icon.paintIcon(this, g, x, y);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Adjust width to accommodate the icon
        int width = super.getPreferredSize().width;
        if (icon != null) {
            width += (int) (icon.getIconWidth() * scaleFactor) + 5; // Adjusted width
        }
        return new Dimension(width, 25); // Set height
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
        repaint(); // Repaint to reflect changes
    }
}
