package views;

import database.dao.BookingDao;
import entities.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BookingListView extends JPanel {
    private static final String[] columnNames = {"Guest First Name", "Guest Last Name", "Room", "Check-In", "Check-Out", "Status"};
    private final JTable table;
    private final JButton addButton;

    public BookingListView(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new BorderLayout());
        add(new JLabel("Bookings List"), BorderLayout.NORTH);

        // Initialize table with column names and existing data
        BookingDao bookingDao = new BookingDao();
        List<Booking> bookings = bookingDao.getAll();
        Object[][] data = new Object[bookings.size()][6];
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            data[i][0] = booking.getGuestFirstName();
            data[i][1] = booking.getGuestLastName();
            data[i][2] = booking.getRoom().getRoomNumber();
            data[i][3] = booking.getCheckInDate();
            data[i][4] = booking.getCheckOutDate();
            data[i][5] = booking.getStatus();
        }

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

    public void refreshTable() {
        BookingDao bookingDao = new BookingDao();
        List<Booking> bookings = bookingDao.getAll();
        Object[][] data = new Object[bookings.size()][6];
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            data[i][0] = booking.getGuestFirstName();
            data[i][1] = booking.getGuestLastName();
            data[i][2] = booking.getRoom().getRoomNumber();
            data[i][3] = booking.getCheckInDate();
            data[i][4] = booking.getCheckOutDate();
            data[i][5] = booking.getStatus();
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setDataVector(data, columnNames);
    }
}
