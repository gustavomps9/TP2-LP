package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomListView extends JPanel {
    private JTable table;
    private JButton addButton;

    public RoomListView(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new BorderLayout());
        add(new JLabel("Rooms List"), BorderLayout.NORTH);

        // Initialize table with column names and existing data
        String[] columnNames = {"Room Number", "Adults", "Children", "Price"};
        Object[][] data = {
                {"101", 2, 0, "50€"},
                {"102", 2, 1, "70€"},
                {"103", 4, 2, "100€"},
                // Add more data as needed
        };

        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Add Button to open RoomFormView
        addButton = new JButton("Add Room");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch to RoomFormView
                cardLayout.show(parentPanel, "RoomForm");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
