package views;

import database.dao.BookingDao;
import database.dao.RoomDao;
import database.dao.StatusDao;
import entities.Booking;
import entities.Room;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static java.util.Calendar.getInstance;

public class BookingFormView extends JPanel {
    private JTextField guestFirstNameField;
    private JTextField guestLastNameField;
    private JDatePickerImpl checkInDatePicker;
    private JDatePickerImpl checkOutDatePicker;
    private JSpinner numberOfAdultsSpinner;
    private JSpinner numberOfChildrenSpinner;
    private JComboBox<String> roomComboBox;
    private JButton submitButton;
    private JButton cancelButton;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JButton backButton;


    // variables for the booking form
    private BookingDao bookingDao = new BookingDao();
    private RoomDao roomDao = new RoomDao();
    private List<Room> rooms = roomDao.getAll();
    StatusDao statusDao = new StatusDao();
    private String enteredGuestFirstName, enteredGuestLastName;
    private int enteredNumberOfAdults, enteredNumberOfChildren;
    private Date checkInDate, checkOutDate;
    private Room selectedRoom;

    public BookingFormView(CardLayout cardLayout, JPanel parentPanel, Booking booking) {
        initializeComponents();

        adjustButtonVisibility(booking);
        if (booking != null) {
            populateForm(booking);
        } else {
            clearForm();
        }
        updateRoomList();

        // Button actions
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if any field is blank
                if (guestFirstNameField.getText().trim().isEmpty() ||
                        guestLastNameField.getText().trim().isEmpty() ||
                        checkInDatePicker.getModel().getValue() == null ||
                        checkOutDatePicker.getModel().getValue() == null ||
                        roomComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                assignValues();
                // Check if the selected room is available
                if (bookingDao.isRoomBooked(selectedRoom.getId(), checkInDate, checkOutDate)) {
                    JOptionPane.showMessageDialog(null, "Room is already booked for the selected dates", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update / save the booking
                if (booking != null) {
                    if (hasChanges(booking)) {
                        JOptionPane.showMessageDialog(null, "No changes to save", "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    updateBooking(booking);
                } else {
                    saveBooking();
                }

                // Go back
                BookingListView bookingListView = new BookingListView(cardLayout, parentPanel);
                parentPanel.add(bookingListView, "BookingList");
                cardLayout.show(parentPanel, "BookingList");
                bookingListView.refreshTable();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Booking Form Deleted");
                // ask for confirmation
                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel this booking?", "Cancel Booking", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    assert booking != null;
                    booking.setStatus(statusDao.getByState("Cancelled"));
                    bookingDao.update(booking);
                    System.out.println("Booking deleted from database");

                    // Go back
                    BookingListView bookingListView = new BookingListView(cardLayout, parentPanel);
                    parentPanel.add(bookingListView, "BookingList");
                    cardLayout.show(parentPanel, "BookingList");
                    bookingListView.refreshTable();
                }
            }
        });

        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assert booking != null;
                booking.setStatus(statusDao.getByState("Checked In"));
                bookingDao.update(booking);
                System.out.println("Booking checked in");

                BookingListView bookingListView = new BookingListView(cardLayout, parentPanel);
                parentPanel.add(bookingListView, "BookingList");
                cardLayout.show(parentPanel, "BookingList");
                bookingListView.refreshTable();
            }
        });

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assert booking != null;
                booking.setStatus(statusDao.getByState("Checked Out"));
                bookingDao.update(booking);
                System.out.println("Booking checked out");

                BookingListView bookingListView = new BookingListView(cardLayout, parentPanel);
                parentPanel.add(bookingListView, "BookingList");
                cardLayout.show(parentPanel, "BookingList");
                bookingListView.refreshTable();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(parentPanel, "Bookings"); // Return to BookingListView
                clearForm();
            }
        });

        // listeners
        numberOfAdultsSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateRoomList();
            }
        });

        numberOfChildrenSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateRoomList();
            }
        });

        checkInDatePicker.getJFormattedTextField().addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    checkInDate = dateFormat.parse(checkInDatePicker.getJFormattedTextField().getText());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                updateRoomList();
            }
        });

        checkOutDatePicker.getJFormattedTextField().addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    checkOutDate = dateFormat.parse(checkOutDatePicker.getJFormattedTextField().getText());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                updateRoomList();
            }
        });

        roomComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedRoom = (Room) roomComboBox.getSelectedItem();
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////

    private void initializeComponents() {
        setLayout(new GridBagLayout());
        initializeFields();
        initializeDatePickers();
        initializeSpinners();
        initializeComboBox();
        initializeButtons();
        layoutComponents();
        setupDateConstraints();
    }

    private void initializeFields() {
        guestFirstNameField = new JTextField(20);
        guestLastNameField = new JTextField(20);
    }

    // Initialize Date Pickers
    // Initialize Date Pickers
    private void initializeDatePickers() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        // Setup Check-In DatePicker
        UtilDateModel checkInModel = new UtilDateModel();
        Calendar today = getInstance();
        checkInModel.setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        checkInModel.setSelected(true);
        checkInDatePicker = new JDatePickerImpl(new JDatePanelImpl(checkInModel, p), new DateLabelFormatter());

        // Setup Check-Out DatePicker
        UtilDateModel checkOutModel = new UtilDateModel();
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        checkOutModel.setDate(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DAY_OF_MONTH));
        checkOutModel.setSelected(true);
        checkOutDatePicker = new JDatePickerImpl(new JDatePanelImpl(checkOutModel, p), new DateLabelFormatter());
    }


    // Custom formatter class
    public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String datePattern = "dd/MM/yyyy";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }

    private void initializeSpinners() {
        SpinnerNumberModel adultModel = new SpinnerNumberModel(1, 1, 10, 1);
        numberOfAdultsSpinner = new JSpinner(adultModel);

        SpinnerNumberModel childrenModel = new SpinnerNumberModel(0, 0, 10, 1);
        numberOfChildrenSpinner = new JSpinner(childrenModel);
    }

    private void initializeComboBox() {
        roomComboBox = new JComboBox<>();
        for (Room room : rooms) {
            roomComboBox.addItem(String.valueOf(room.getRoomNumber()));
        }
        AutoCompleteDecorator.decorate(roomComboBox);
    }

    private void initializeButtons() {
        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel Booking");
        cancelButton.setForeground(Color.RED);
        checkInButton = new JButton("Check-In");
        checkOutButton = new JButton("Check-Out");
        backButton = new JButton("Back");
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Guest First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Guest First Name:"), gbc);

        gbc.gridx = 1;
        add(guestFirstNameField, gbc);

        // Guest Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Guest Last Name:"), gbc);

        gbc.gridx = 1;
        add(guestLastNameField, gbc);

        // Check-In Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Check-In Date:"), gbc);

        gbc.gridx = 1;
        add(checkInDatePicker, gbc);

        // Check-Out Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Check-Out Date:"), gbc);

        gbc.gridx = 1;
        add(checkOutDatePicker, gbc);

        // Number of Adults
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Number of Adults:"), gbc);

        gbc.gridx = 1;
        add(numberOfAdultsSpinner, gbc);

        // Number of Children
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Number of Children:"), gbc);

        gbc.gridx = 1;
        add(numberOfChildrenSpinner, gbc);

        // Room Selector
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Room:"), gbc);

        gbc.gridx = 1;
        add(roomComboBox, gbc);

        // Delete Button
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(cancelButton, gbc);

        // Submit Button
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(submitButton, gbc);

        // Back Button
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(backButton, gbc);

        // Check-In and Check-Out Buttons on the lef side full width
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(checkInButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(checkOutButton, gbc);
    }

    private void adjustButtonVisibility(Booking booking) {
        boolean isNew = booking == null;
        checkInButton.setVisible(!isNew);
        checkOutButton.setVisible(!isNew);

        checkInButton.setEnabled(!isNew && "Booked".equals(booking.getStatus().getState()));
        checkOutButton.setEnabled(!isNew && "Checked In".equals(booking.getStatus().getState()));

        cancelButton.setVisible(!isNew && booking.getStatus().getState().equals("Booked"));
        submitButton.setVisible(isNew || booking.getStatus().getState().equals("Booked"));
    }


    private void setupDateConstraints() {
        checkInDatePicker.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                adjustCheckOutDate();
            }
        });

        checkOutDatePicker.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                Date checkInDate = ((UtilDateModel) checkInDatePicker.getModel()).getValue();
                Date checkOutDate = ((UtilDateModel) checkOutDatePicker.getModel()).getValue();
                if (checkOutDate != null && checkInDate != null && !checkOutDate.after(checkInDate)) {
                    Calendar c = getInstance();
                    c.setTime(checkInDate);
                    c.add(Calendar.DATE, 1);  // Ensure check-out is at least one day after check-in
                    ((UtilDateModel) checkOutDatePicker.getModel()).setValue(c.getTime());
                }
            }
        });
    }

    private void adjustCheckOutDate() {
        Date checkInDate = ((UtilDateModel) checkInDatePicker.getModel()).getValue();
        Date checkOutDate = ((UtilDateModel) checkOutDatePicker.getModel()).getValue();
        if (checkOutDate != null && checkInDate != null && !checkOutDate.after(checkInDate)) {
            Calendar c = getInstance();
            c.setTime(checkInDate);
            c.add(Calendar.DATE, 1);  // Add one day
            ((UtilDateModel) checkOutDatePicker.getModel()).setValue(c.getTime());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    private void assignValues() {
        // Get values from the form
        enteredGuestFirstName = guestFirstNameField.getText();
        enteredGuestLastName = guestLastNameField.getText();
        enteredNumberOfAdults = (int) numberOfAdultsSpinner.getValue();
        enteredNumberOfChildren = (int) numberOfChildrenSpinner.getValue();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            checkInDate = dateFormat.parse(checkInDatePicker.getJFormattedTextField().getText());//
            checkOutDate = dateFormat.parse(checkOutDatePicker.getJFormattedTextField().getText());//
        } catch (ParseException ex) {
            ex.printStackTrace();
            // Handle date parsing error, if necessary
            return;
        }
        selectedRoom = (Room) roomComboBox.getSelectedItem();
        int selectedRoomNumber = selectedRoom.getRoomNumber();
        selectedRoom = rooms.stream()
                .filter(room -> room.getRoomNumber() == selectedRoomNumber)
                .findFirst()
                .orElse(null);
    }

    private void updateBooking(Booking booking) {
        // Check if there are any changes to save
        if (booking.getGuestFirstName().equals(enteredGuestFirstName) &&
                booking.getGuestLastName().equals(enteredGuestLastName) &&
                booking.getCheckInDate().equals(checkInDate) &&
                booking.getCheckOutDate().equals(checkOutDate) &&
                booking.getNumberOfAdults() == enteredNumberOfAdults &&
                booking.getNumberOfChildren() == enteredNumberOfChildren &&
                booking.getRoom().equals(selectedRoom)) {
            JOptionPane.showMessageDialog(null, "No changes to save", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // If there are changes, update the booking
        booking.setGuestFirstName(enteredGuestFirstName);
        booking.setGuestLastName(enteredGuestLastName);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setNumberOfAdults(enteredNumberOfAdults);
        booking.setNumberOfChildren(enteredNumberOfChildren);
        booking.setRoom(selectedRoom);

        // Update the booking
        bookingDao.update(booking);
    }

    private void saveBooking() {
        // If booking is null, create a new booking
        Booking newBooking = new Booking(
                enteredGuestFirstName,
                enteredGuestLastName,
                checkInDate,
                checkOutDate,
                enteredNumberOfAdults,
                enteredNumberOfChildren,
                selectedRoom,
                statusDao.getById(1)
        );
        // Save the new booking
        bookingDao.save(newBooking);
    }

    private void populateForm(Booking booking) {
        guestFirstNameField.setText(booking.getGuestFirstName());
        guestLastNameField.setText(booking.getGuestLastName());
        checkInDatePicker.getModel().setDate(booking.getCheckInDate().getYear() + 1900, booking.getCheckInDate().getMonth(), booking.getCheckInDate().getDate());
        checkInDatePicker.getModel().setSelected(true);
        checkOutDatePicker.getModel().setDate(booking.getCheckOutDate().getYear() + 1900, booking.getCheckOutDate().getMonth(), booking.getCheckOutDate().getDate());
        checkOutDatePicker.getModel().setSelected(true);
        numberOfAdultsSpinner.setValue(booking.getNumberOfAdults());
        numberOfChildrenSpinner.setValue(booking.getNumberOfChildren());
        roomComboBox.setSelectedItem(String.valueOf(booking.getRoom().getRoomNumber()));
    }

    private void clearForm() {
        guestFirstNameField.setText("");
        guestLastNameField.setText("");
        numberOfAdultsSpinner.setValue(1);
        numberOfChildrenSpinner.setValue(0);
        roomComboBox.setSelectedIndex(0);
        // Set the check-in date to today
        Calendar calendar = getInstance();
        checkInDatePicker.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        checkOutDatePicker.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 1);
        numberOfAdultsSpinner.setValue(1);
        numberOfChildrenSpinner.setValue(0);
        roomComboBox.setSelectedIndex(0);

        updateRoomList();
    }

    private boolean hasChanges(Booking booking) {
        return !booking.getGuestFirstName().equals(enteredGuestFirstName) ||
                !booking.getGuestLastName().equals(enteredGuestLastName) ||
                !booking.getCheckInDate().equals(checkInDate) ||
                !booking.getCheckOutDate().equals(checkOutDate) ||
                booking.getNumberOfAdults() != enteredNumberOfAdults ||
                booking.getNumberOfChildren() != enteredNumberOfChildren ||
                !booking.getRoom().equals(selectedRoom);
    }

    private void updateRoomList() {
        List<Room> suitableAndAvailableRooms = roomDao.getSuitableAndAvailableRooms(
                (int) numberOfAdultsSpinner.getValue(),
                (int) numberOfChildrenSpinner.getValue(),
                checkInDate,
                checkOutDate
        );
        roomComboBox.setModel(new DefaultComboBoxModel(suitableAndAvailableRooms.toArray()));
    }
}