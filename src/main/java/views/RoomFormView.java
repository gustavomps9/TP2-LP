package views;

import database.dao.RoomDao;
import entities.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomFormView extends JPanel {
    private final JTextField roomNumberField;
    private final JTextField adultsCapacityField;
    private final JTextField childrenCapacityField;
    private final JTextField priceField;
    private final JButton submitButton;
    private final JButton cancelButton;
    private final JButton deleteButton;

    public RoomFormView(CardLayout cardLayout, JPanel parentPanel, Room room) {
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

        // Delete and Cancel Buttons
        deleteButton = new JButton("Delete");
        deleteButton.setForeground(Color.RED); // Set text color to red
        cancelButton = new JButton("Cancel");

        GridBagLayout buttonLayout = new GridBagLayout();
        GridBagConstraints buttonGbc = new GridBagConstraints();
        JPanel buttonPanel = new JPanel(buttonLayout);

        buttonGbc.gridx = 0;
        buttonGbc.gridy = 0;
        buttonGbc.weightx = 1.0; // Allow horizontal growth
        buttonGbc.anchor = GridBagConstraints.EAST; // Align to the right
        buttonGbc.insets = new Insets(0, 0, 0, 5); // Add some space to the right of the delete button
        buttonPanel.add(deleteButton, buttonGbc);

        buttonGbc.gridx = 1;
        buttonGbc.gridy = 0;
        buttonGbc.weightx = 0; // No extra horizontal growth
        buttonGbc.anchor = GridBagConstraints.WEST; // Align to the left
        buttonGbc.insets = new Insets(0, 5, 0, 0); // Add some space to the left of the cancel button
        buttonPanel.add(cancelButton, buttonGbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Make buttons span across both columns
        gbc.anchor = GridBagConstraints.LINE_END; // Align to the right end of the cell
        add(buttonPanel, gbc);

        // Submit Button
        submitButton = new JButton("Submit");

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END; // Align to the right end of the cell
        add(submitButton, gbc);


        // If room is not null, populate the form fields with the room data
        if (room != null) {
            roomNumberField.setText(String.valueOf(room.getRoomNumber()));
            adultsCapacityField.setText(String.valueOf(room.getAdultsCapacity()));
            childrenCapacityField.setText(String.valueOf(room.getChildrenCapacity()));
            priceField.setText(String.valueOf(room.getPrice()));
        } else {
            clearForm();
        }

        // Button actions
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RoomDao roomDao = new RoomDao();
                // If room is not null, update the existing room
                if (room != null) {
                    room.setRoomNumber(Integer.parseInt(roomNumberField.getText()));
                    room.setAdultsCapacity(Integer.parseInt(adultsCapacityField.getText()));
                    room.setChildrenCapacity(Integer.parseInt(childrenCapacityField.getText()));
                    room.setPrice(Float.parseFloat(priceField.getText()));
                    roomDao.update(room);
                } else {
                    // If room is null, create a new room
                    Room newRoom = new Room(
                            Integer.parseInt(roomNumberField.getText()),
                            Integer.parseInt(adultsCapacityField.getText()),
                            Integer.parseInt(childrenCapacityField.getText()),
                            Float.parseFloat(priceField.getText())
                    );
                    roomDao.save(newRoom);
                }

                RoomListView roomListView = (RoomListView) parentPanel.getComponent(1); // Get RoomListView
                roomListView.refreshTable(); // Refresh the table
                cardLayout.show(parentPanel, "Rooms"); // Go back to RoomListView
                System.out.println("Room was successfully saved/updated");
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(parentPanel, "Rooms"); // Go back to RoomListView
                System.out.println("Room form cancelled");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (room != null) {
                    RoomDao roomDao = new RoomDao();
                    roomDao.delete(room);
                    RoomListView roomListView = (RoomListView) parentPanel.getComponent(1); // Get RoomListView
                    roomListView.refreshTable(); // Refresh the table
                    cardLayout.show(parentPanel, "Rooms"); // Go back to RoomListView
                }
            }
        });
    }

    public void clearForm() {
        roomNumberField.setText("");
        adultsCapacityField.setText("");
        childrenCapacityField.setText("");
        priceField.setText("");
    }
}