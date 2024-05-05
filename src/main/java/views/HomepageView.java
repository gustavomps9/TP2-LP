package views;

import entities.Booking;
import database.dao.BookingDao;
import database.dao.StatusDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

public class HomepageView extends JPanel {
    private JTable checkInTable;
    private JTable checkOutTable;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private BookingDao bookingDao = new BookingDao();
    private StatusDao statusDao = new StatusDao();  // Assume statusDao is set up to handle status changes

    public HomepageView() {
        setLayout(new GridLayout(2, 1));  // Layout to hold two tables
        initTables();
    }

    private void initTables() {
        // Retrieve all bookings
        List<Booking> bookings = bookingDao.getAll();
        Date today = new Date();

        // Create tables
        checkInTable = createTable(bookings, true);
        checkOutTable = createTable(bookings, false);

        // Add tables to the panel
        add(new JScrollPane(checkInTable));
        add(new JScrollPane(checkOutTable));
    }

    private JTable createTable(List<Booking> bookings, boolean isCheckIn) {
        String[] columnNames = {"Last Name", "First Name", "Room", isCheckIn ? "Check-Out" : "Check-In", "Action"};
        Object[][] data = bookings.stream()
                .filter(b -> dateFormat.format(isCheckIn ? b.getCheckInDate() : b.getCheckOutDate()).equals(dateFormat.format(new Date())))
                .filter(b -> b.getStatus().getState().equals(isCheckIn ? "Booked" : "CheckedIn"))
                .map(b -> new Object[]{
                        b.getGuestLastName(),
                        b.getGuestFirstName(),
                        b.getRoom().getRoomNumber(),
                        dateFormat.format(isCheckIn ? b.getCheckOutDate() : b.getCheckInDate()),
                        createButton(b, isCheckIn)
                })
                .toArray(Object[][]::new);

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Make only the "Action" column editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 4 ? JButton.class : String.class;
            }
        };

        JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(JButton.class, new ButtonRenderer());
        table.setDefaultEditor(JButton.class, new ButtonEditor(new JCheckBox()));
        return table;
    }

    private JButton createButton(Booking booking, boolean isCheckIn) {
        JButton button = new JButton(isCheckIn ? "Check In" : "Check Out");
        button.addActionListener(e -> {
            // Update booking status
            booking.setStatus(statusDao.getByState(isCheckIn ? "CheckedIn" : "CheckedOut"));
            bookingDao.update(booking);
            // Refresh the table
            initTables();
        });
        return button;
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // Ensure the component returned is actually a button
        if (value instanceof JButton) {
            // Return the button as is
            return (JButton) value;
        }
        return this; // Fallback to default
    }
}


class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> {
            fireEditingStopped(); // Notify the cell editor that editing has stopped
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (value instanceof JButton) {
            button = (JButton) value;
            label = button.getText();
        } else {
            label = value.toString();
            button.setText(label);
        }
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }
}