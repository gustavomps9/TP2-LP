package views;

import entities.Room;
import database.dao.RoomDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class RoomListView extends JPanel {
    private final static String[] columnNames = {"Room Number", "Adults", "Children", "Price"};
    private final JTable table;
    private final JButton addButton;
    private final List<Room> rooms;

    public RoomListView(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new BorderLayout());
        add(new JLabel("Rooms List"), BorderLayout.NORTH);

        // Initialize table with column names and existing data
        RoomDao roomDao = new RoomDao();
        rooms = roomDao.getAll();
        rooms.sort(Comparator.comparingInt(Room::getNumber));
        Object[][] data = new Object[rooms.size()][4];
        rooms.forEach(room -> {
            data[rooms.indexOf(room)] = new Object[]{room.getNumber(), room.getAdultsCapacity(), room.getChildrenCapacity(), room.getPrice()};
        });

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);

        // add dividers between rows
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {;
                    // Open RoomFormView with selectedRoom
                    RoomFormView roomFormView = new RoomFormView(cardLayout, parentPanel, rooms.get(row));
                    parentPanel.add(roomFormView, "RoomForm");
                    cardLayout.show(parentPanel, "RoomForm");
                }
            }
        });

        // Add Button to open RoomFormView
        addButton = new JButton("Add Room");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RoomFormView roomFormView = new RoomFormView(cardLayout, parentPanel, null);
                parentPanel.add(roomFormView, "RoomForm");
                // Switch to RoomFormView
                cardLayout.show(parentPanel, "RoomForm");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        // Refresh the table with updated data
        RoomDao roomDao = new RoomDao();
        rooms.clear();
        rooms.addAll(roomDao.getAll());
        // Sort rooms by room number
        rooms.sort(Comparator.comparingInt(Room::getNumber));
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear the table
        rooms.forEach(room -> {
            model.addRow(new Object[]{room.getNumber(), room.getAdultsCapacity(), room.getChildrenCapacity(), room.getPrice()});
        });
    }

}
