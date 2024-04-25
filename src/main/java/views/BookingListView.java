package views;

import database.dao.BookingDao;
import entities.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class BookingListView extends JPanel {
    private static final String[] columnNames = {"Guest First Name", "Guest Last Name", "Room", "Check-In", "Check-Out", "Status"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final JTable table;
    private final JButton addButton;

    public BookingListView(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new BorderLayout());
        add(new JLabel("Bookings List"), BorderLayout.NORTH);

        // Initialize table with non-editable model
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            // Override isCellEditable to always return false
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Initialize table with column names and existing data
        BookingDao bookingDao = new BookingDao();
        List<Booking> bookings = bookingDao.getAll();
        Object[][] data = new Object[bookings.size()][6];
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            data[i][0] = booking.getGuestFirstName();
            data[i][1] = booking.getGuestLastName();
            data[i][2] = (booking.getRoom() != null) ? booking.getRoom().getRoomNumber() : "";
            data[i][3] = dateFormat.format(booking.getCheckInDate());
            data[i][4] = dateFormat.format(booking.getCheckOutDate());
            data[i][5] = (booking.getStatus() != null) ? booking.getStatus().getState() : "";
        }

        table = new JTable(data, columnNames);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    // Open BookingFormView with selected booking
                    BookingFormView bookingFormView = new BookingFormView(cardLayout, parentPanel, bookings.get(row));
                    parentPanel.add(bookingFormView, "BookingForm");
                    cardLayout.show(parentPanel, "BookingForm");
                }
            }
        });

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

        refreshTable();
    }

    public void refreshTable() {
        BookingDao bookingDao = new BookingDao();
        List<Booking> bookings = bookingDao.getAll();

        // Initialize a DefaultTableModel
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        table.setModel(model);

        for (Booking booking : bookings) {
            Object[] rowData = {
                    booking.getGuestFirstName(),
                    booking.getGuestLastName(),
                    (booking.getRoom() != null) ? booking.getRoom().getRoomNumber() : "",
                    dateFormat.format(booking.getCheckInDate()),
                    dateFormat.format(booking.getCheckOutDate()),
                    (booking.getStatus() != null) ? booking.getStatus().getState() : ""
            };
            model.addRow(rowData);
        }
    }

}

