package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MessageLabel extends JLabel {

    private Message message;
    private String userName;

    public MessageLabel(Message message,String userName) {
        this.message = message;
        this.userName=userName;
        formatLabel();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    handleSelection(e);
                }
            }
        });
    }

    private void formatLabel() {
        // Format the label appearance
        setOpaque(true); // Make the label opaque
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding
        setHorizontalAlignment(SwingConstants.LEFT); // Align text to the left
        setVerticalAlignment(SwingConstants.TOP); // Align text to the top
        setFont(new Font("Arial", Font.PLAIN, 14)); // Set font and font size

        // Set background color based on sender
        if (message.getSender().equals(userName)) {
            setBackground(new Color(0xE1FFC7)); // Light green background color for own messages
        } 
        else if(message.getMessageType().equals("replied")) {
            setBackground(new Color(0xFFC7C7)); // Light red background color for starred messages
        }
        else {
            setBackground(new Color(0xC7D9FF)); // Light blue background color for received messages
        }

        // Format the label text and timestamp
        String labelText = message.getText() + " " + message.getTimeStamp();
        setText(labelText);
    }

    private void handleSelection(MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem replyItem = new JMenuItem("Reply");
        JMenuItem forwardItem = new JMenuItem("Forward");
        JMenuItem copyItem = new JMenuItem("Copy");

        // Add action listeners
        editItem.addActionListener(actionEvent -> {
            // Handle edit action
            JOptionPane.showMessageDialog(this, "Edit message");
        });
        deleteItem.addActionListener(actionEvent -> {
            // Handle delete action
            JOptionPane.showMessageDialog(this, "Delete message");
        });
        replyItem.addActionListener(actionEvent -> {
            // Handle reply action
            JOptionPane.showMessageDialog(this, "Reply to message");
        });
        forwardItem.addActionListener(actionEvent -> {
            // Handle forward action
            JOptionPane.showMessageDialog(this, "Forward message");
        });
        copyItem.addActionListener(actionEvent -> {
            // Handle copy action
            JOptionPane.showMessageDialog(this, "Copy message");
        });

        // Add items to popup menu
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.add(replyItem);
        popupMenu.add(forwardItem);
        popupMenu.add(copyItem);

        // Show popup menu at mouse location
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
}
