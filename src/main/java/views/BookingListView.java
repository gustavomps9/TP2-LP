package views;

import database.dao.BookingDao;
import database.dao.StatusDao;
import entities.Booking;
import entities.Status;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingListView extends JPanel {
    private static final String[] columnNames = {"Guest First Name", "Guest Last Name", "Room", "Check-In", "Check-Out", "Status"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final JTable table;
    private final JButton addButton;
    private final JTextField guestNameField;
    private final JButton searchButton;
    private final JComboBox statusComboBox;
    private final DefaultTableModel model;

    public BookingListView(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new BorderLayout());
        add(new JLabel("Bookings List"), BorderLayout.NORTH);

        // Initialize search components
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guestNameField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search by Guest Name:"));
        searchPanel.add(guestNameField);
        //searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Filter by Status:"));
        // Initialize status combo box
        StatusDao statusDao = new StatusDao();
        List<Status> statuses = new ArrayList<>();
        statuses.add(new Status("All"));
        statuses.addAll(statusDao.getAll());
        statusComboBox = new JComboBox(statuses.toArray());
        searchPanel.add(statusComboBox);
        add(searchPanel, BorderLayout.NORTH);

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

        // Initialize table
        model = new MyDefaultTableModel(columnNames, 0);
        table = new JTable(model); // Use MyDefaultTableModel
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    // Retrieve the Booking object from the bookings list
                    Booking booking = bookings.get(row);
                    // Open BookingFormView with selected booking
                    BookingFormView bookingFormView = new BookingFormView(cardLayout, parentPanel, booking);
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
                BookingFormView bookingFormView = new BookingFormView(cardLayout, parentPanel, null);
                parentPanel.add(bookingFormView, "BookingForm");
                cardLayout.show(parentPanel, "BookingForm");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add listeners to search
        guestNameField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateTable();
            }
            public void removeUpdate(DocumentEvent e) {
                updateTable();
            }
            public void insertUpdate(DocumentEvent e) {
                updateTable();
            }

            public void updateTable() {
                String guestName = guestNameField.getText();
                Object status = statusComboBox.getSelectedItem();
                refreshTableWithFilters(guestName, status);
            }
        });

        // Add ItemListener to statusComboBox
        statusComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String guestName = guestNameField.getText();
                    Object status = statusComboBox.getSelectedItem();
                    refreshTableWithFilters(guestName, status);
                }
            }
        });

        refreshTable();
    }

    public void refreshTable() {
        BookingDao bookingDao = new BookingDao();
        List<Booking> bookings = bookingDao.getAll();
        model.setRowCount(0);

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

    public void refreshTableWithFilters(String guestName, Object status) {
        BookingDao bookingDao = new BookingDao();
        List<Booking> bookings;

        String state = status != null ? status.toString() : "";

        if ("All".equals(state)) {
            if (!guestName.isEmpty()) {
                bookings = bookingDao.getByGuestNameOrLastName(guestName);
            } else {
                bookings = bookingDao.getAll();
            }
        } else if (!guestName.isEmpty() && !state.isEmpty()) {
            bookings = bookingDao.getByGuestNameOrLastNameAndStatus(guestName, state);
        } else if (!state.isEmpty()) {
            bookings = bookingDao.getByState(state);
        } else if (!guestName.isEmpty()) {
            bookings = bookingDao.getByGuestNameOrLastName(guestName);
        } else {
            bookings = bookingDao.getAll();
        }

        // Utilize the instance variable 'model' instead of redefining a new local variable
        model.setRowCount(0);

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

    // Custom DefaultTableModel to prevent cell editing
    private static class MyDefaultTableModel extends DefaultTableModel {
        public MyDefaultTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
