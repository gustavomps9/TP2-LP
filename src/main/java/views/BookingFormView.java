package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookingFormView extends JPanel {
    private JTextField guestFirstNameField;
    private JTextField guestLastNameField;
    private JTextField checkInDateField;
    private JTextField checkOutDateField;
    private JSpinner numberOfAdultsSpinner;
    private JSpinner numberOfChildrenSpinner;
    private JButton submitButton;
    private JButton cancelButton;

    public BookingFormView(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Guest First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Guest First Name:"), gbc);

        gbc.gridx = 1;
        guestFirstNameField = new JTextField();
        add(guestFirstNameField, gbc);

        // Guest Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Guest Last Name:"), gbc);

        gbc.gridx = 1;
        guestLastNameField = new JTextField();
        add(guestLastNameField, gbc);

        // Check-In Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Check-In Date:"), gbc);

        gbc.gridx = 1;
        checkInDateField = new JTextField(); // Ideally use a date picker
        add(checkInDateField, gbc);

        // Check-Out Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Check-Out Date:"), gbc);

        gbc.gridx = 1;
        checkOutDateField = new JTextField(); // Ideally use a date picker
        add(checkOutDateField, gbc);

        // Number of Adults
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Number of Adults:"), gbc);

        gbc.gridx = 1;
        SpinnerNumberModel adultModel = new SpinnerNumberModel(1, 1, 10, 1);
        numberOfAdultsSpinner = new JSpinner(adultModel);
        add(numberOfAdultsSpinner, gbc);

        // Number of Children
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Number of Children:"), gbc);

        gbc.gridx = 1;
        SpinnerNumberModel childrenModel = new SpinnerNumberModel(0, 0, 10, 1);
        numberOfChildrenSpinner = new JSpinner(childrenModel);
        add(numberOfChildrenSpinner, gbc);

        // Submit and Cancel Buttons
        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2; // Make buttons span across both columns
        add(buttonPanel, gbc);

        // Button actions
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Booking Form Submitted");
                // Logic to handle booking submission
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Booking Form Cancelled");
                cardLayout.show(parentPanel, "Bookings"); // Return to BookingListView
            }
        });
    }
}
