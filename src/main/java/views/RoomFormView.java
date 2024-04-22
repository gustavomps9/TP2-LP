package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomFormView extends JPanel {
    private JTextField roomNumberField;
    private JTextField adultsCapacityField;
    private JTextField childrenCapacityField;
    private JTextField priceField;
    private JButton submitButton;
    private JButton cancelButton;

    public RoomFormView(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new GridBagLayout()); // Using GridBagLayout for better alignment
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Room Number
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        gbc.weightx = 0; // No extra horizontal growth
        add(new JLabel("Room Number:"), gbc);

        gbc.gridx = 1; // Second column
        roomNumberField = new JTextField(15);
        add(roomNumberField, gbc);

        // Adults Capacity
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Adults Capacity:"), gbc);

        gbc.gridx = 1;
        adultsCapacityField = new JTextField();
        add(adultsCapacityField, gbc);

        // Children Capacity
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Children Capacity:"), gbc);

        gbc.gridx = 1;
        childrenCapacityField = new JTextField();
        add(childrenCapacityField, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        priceField = new JTextField();
        add(priceField, gbc);

        // Submit and Cancel Buttons
        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Make buttons span across both columns
        add(buttonPanel, gbc);

        // Button actions
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Room Form Submitted");
                // Logic for form submission
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Room Form Cancelled");
                cardLayout.show(parentPanel, "Rooms"); // Go back to RoomListView
            }
        });
    }
}