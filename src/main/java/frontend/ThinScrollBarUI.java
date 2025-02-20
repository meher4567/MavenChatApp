package frontend;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ThinScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new Color(128, 128, 128);
    }

    @Override
    protected Dimension getMaximumThumbSize() {
        return new Dimension(8, Integer.MAX_VALUE);
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(8, 8);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
}
