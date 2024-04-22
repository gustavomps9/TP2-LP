package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookingListView extends JPanel {
    private JTable table;
    private JButton addButton;

    public BookingListView(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new BorderLayout());
        add(new JLabel("Bookings List"), BorderLayout.NORTH);

        // Initialize table with column names and existing data
        String[] columnNames = {"Guest First Name", "Guest Last Name", "Room", "Check-In", "Check-Out", "Status"};
        Object[][] data = {
                {"Alice", "Smith", "101", "2021-10-01", "2021-10-03", "Confirmed"},
                {"Bob", "Johnson", "102", "2021-10-02", "2021-10-05", "Pending"},
                {"Charlie", "Williams", "103", "2021-10-03", "2021-10-06", "Confirmed"},
                // Add more data as needed
        };

        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Add components like table, buttons, etc.
        addButton = new JButton("Add Booking");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch to BookingFormView
                cardLayout.show(parentPanel, "BookingForm");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
