package views;

import database.dao.BookingDao;
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
        if (room == null) {
            deleteButton.setEnabled(false); // Disable delete button if no room is selected
        }
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

                // Check if any field is blank
                if (areFieldsEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Check if a room with the entered room number already exists
                int enteredRoomNumber = Integer.parseInt(roomNumberField.getText());
                if (roomDao.existByNumber(enteredRoomNumber) && (room == null || room.getRoomNumber() != enteredRoomNumber)) {
                    JOptionPane.showMessageDialog(null, "A room with this number already exists", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // If room is not null, check if there are any changes to save
                if (room != null) {
                    int enteredAdultsCapacity = Integer.parseInt(adultsCapacityField.getText());
                    int enteredChildrenCapacity = Integer.parseInt(childrenCapacityField.getText());
                    float enteredPrice = Float.parseFloat(priceField.getText());

                    if (room.getRoomNumber() == enteredRoomNumber &&
                            room.getAdultsCapacity() == enteredAdultsCapacity &&
                            room.getChildrenCapacity() == enteredChildrenCapacity &&
                            room.getPrice() == enteredPrice) {
                        JOptionPane.showMessageDialog(null, "No changes to save", "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // If there are changes, update the room
                    room.setRoomNumber(enteredRoomNumber);
                    room.setAdultsCapacity(enteredAdultsCapacity);
                    room.setChildrenCapacity(enteredChildrenCapacity);
                    room.setPrice(enteredPrice);
                    roomDao.update(room);
                } else {
                    // If room is null, create a new room
                    Room newRoom = new Room(
                            enteredRoomNumber,
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
                clearForm();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(parentPanel, "Rooms"); // Go back to RoomListView
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (room != null) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this room?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        RoomDao roomDao = new RoomDao();
                        roomDao.delete(room);
                        RoomListView roomListView = (RoomListView) parentPanel.getComponent(1); // Get RoomListView
                        roomListView.refreshTable(); // Refresh the table
                        cardLayout.show(parentPanel, "Rooms"); // Go back to RoomListView
                    }
                }
            }
        });
    }

    public boolean areFieldsEmpty() {
        return roomNumberField.getText().trim().isEmpty() ||
                adultsCapacityField.getText().trim().isEmpty() ||
                childrenCapacityField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty();
    }

    public void clearForm() {
        roomNumberField.setText("");
        adultsCapacityField.setText("");
        childrenCapacityField.setText("");
        priceField.setText("");
    }
}