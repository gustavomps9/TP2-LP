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
import java.util.*;
import java.util.List;

import static java.util.Calendar.getInstance;

public class BookingFormView extends JPanel {
    private JTextField guestFirstNameField;
    private JTextField guestLastNameField;
    private JDatePickerImpl checkInDatePicker;
    private JDatePickerImpl checkOutDatePicker;
    private JSpinner numberOfAdultsSpinner;
    private JSpinner numberOfChildrenSpinner;
    private JComboBox<Room> roomComboBox;
    private JLabel roomPriceLabel;
    private JButton submitButton;
    private JButton cancelButton;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JButton backButton;

    // variables for the booking form
    private final BookingDao bookingDao = new BookingDao();
    private final RoomDao roomDao = new RoomDao();
    private List<Room> rooms = roomDao.getAll();
    StatusDao statusDao = new StatusDao();
    private String enteredGuestFirstName, enteredGuestLastName;
    private int enteredNumberOfAdults, enteredNumberOfChildren;
    private Date selectedCheckInDate, selectedCheckOutDate;
    private Room selectedRoom;

    public BookingFormView(CardLayout cardLayout, JPanel parentPanel, Booking booking) {
        initializeComponents();

        adjustButtonVisibility(booking);
        if (booking != null) {
            populateForm(booking);
            assignValues();
            updateRoomList(booking);
        } else {
            clearForm();
            assignValues();
            updateRoomList(null);
        }

        roomPriceLabel.setText(" at " + selectedRoom.getPrice() + "€ per night");

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

                // Validate the entered names
                if (!enteredGuestFirstName.matches("[a-zA-Z]+") || !enteredGuestLastName.matches("[a-zA-Z]+")) {
                    JOptionPane.showMessageDialog(null, "Names can only contain letters", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (booking != null && !hasChanges(booking)) {
                    JOptionPane.showMessageDialog(null, "No changes to save", "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Check if the selected room is available
                if (bookingDao.isRoomBooked(selectedRoom.getId(), selectedCheckInDate, selectedCheckOutDate) && (booking == null || booking.getRoom().getId() != selectedRoom.getId())){
                    JOptionPane.showMessageDialog(null, "Room is already booked for the selected dates", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update / save the booking
                if (booking != null) {
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
                BookingListView bookingListView = (BookingListView) parentPanel.getComponent(4); // Get BookingListView
                bookingListView.refreshTable(); // Refresh the table
                cardLayout.show(parentPanel, "Bookings"); // Go back to BookingListView
            }
        });

        // listeners
        numberOfAdultsSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateRoomList(booking);
            }
        });

        numberOfChildrenSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateRoomList(booking);
            }
        });

        checkInDatePicker.getJFormattedTextField().addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    selectedCheckInDate = dateFormat.parse(checkInDatePicker.getJFormattedTextField().getText());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                updateRoomList(booking);
            }
        });

        checkOutDatePicker.getJFormattedTextField().addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    selectedCheckOutDate = dateFormat.parse(checkOutDatePicker.getJFormattedTextField().getText());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                updateRoomList(booking);
            }
        });

        roomComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedRoom = (Room) roomComboBox.getSelectedItem();
                    roomPriceLabel.setText(" at " + selectedRoom.getPrice() + "€ per night");
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
        //setupDateConstraints();
    }

    private void initializeFields() {
        guestFirstNameField = new JTextField(20);
        guestLastNameField = new JTextField(20);
    }

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

        // Add a listener to checkOutModel to ensure the selected date is not before the check-in date
        checkInModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equals(evt.getPropertyName())) {
                    Date selectedDate = (Date) evt.getNewValue();
                    if (selectedDate.before(new Date())) {
                        checkInModel.setValue(new Date());
                    } else if (selectedDate.after(checkOutModel.getValue())) {
                        checkOutModel.setDate(selectedDate.getYear() + 1900, selectedDate.getMonth(), selectedDate.getDate() + 1);
                    }
                }
            }
        });

        checkOutModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equals(evt.getPropertyName())) {
                    Date selectedDate = (Date) evt.getNewValue();
                    if (selectedDate.before(checkInModel.getValue())) {
                        checkOutModel.setValue(checkInModel.getValue());
                    }
                }
            }
        });
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
        roomComboBox = new JComboBox<Room>();
        for (Room room : rooms) {
            roomComboBox.addItem(room);
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

        /// Room Selector
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Room:"), gbc);

        // Create a new GridBagLayout and GridBagConstraints for the roomPanel
        GridBagLayout roomPanelLayout = new GridBagLayout();
        GridBagConstraints roomPanelGbc = new GridBagConstraints();
        roomPanelGbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel roomPanel = new JPanel(roomPanelLayout);

        // Add roomComboBox to roomPanel with a width of 1 cell
        roomPanelGbc.gridx = 0;
        roomPanelGbc.gridy = 0;
        roomPanelGbc.weightx = 0.3; // this can be adjusted as per your requirement
        roomPanel.add(roomComboBox, roomPanelGbc);

        // Add roomPriceLabel to roomPanel with a width of 2 cells
        roomPriceLabel = new JLabel(" at ?€ per night");
        roomPanelGbc.gridx = 1;
        roomPanelGbc.weightx = 0.7; // this can be adjusted as per your requirement
        roomPanel.add(roomPriceLabel, roomPanelGbc);

        // Add roomPanel to the main panel
        gbc.gridx = 1;
        add(roomPanel, gbc);

        // Cancel Button
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

        // Disable UI components if the booking's state is not "Booked"
        boolean isBooked = isNew || "Booked".equals(booking.getStatus().getState());
        guestFirstNameField.setEnabled(isBooked);
        guestLastNameField.setEnabled(isBooked);
        checkInDatePicker.getComponent(0).setEnabled(isBooked);
        checkOutDatePicker.getComponent(0).setEnabled(isBooked);
        checkInDatePicker.getComponent(1).setEnabled(isBooked);
        checkOutDatePicker.getComponent(1).setEnabled(isBooked);
        numberOfAdultsSpinner.setEnabled(isBooked);
        numberOfChildrenSpinner.setEnabled(isBooked);
        roomComboBox.setEnabled(isBooked);

        // If the Submit and Cancel buttons are not visible, move the Back button to the Cancel button's place
        if (!submitButton.isVisible() && !cancelButton.isVisible()) {
            GridBagConstraints gbc = ((GridBagLayout) getLayout()).getConstraints(backButton);
            gbc.gridy = 7;
            ((GridBagLayout) getLayout()).setConstraints(backButton, gbc);
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
            selectedCheckInDate = dateFormat.parse(checkInDatePicker.getJFormattedTextField().getText());//
            selectedCheckOutDate = dateFormat.parse(checkOutDatePicker.getJFormattedTextField().getText());//
        } catch (ParseException ex) {
            ex.printStackTrace();
            // Handle date parsing error, if necessary
            return;
        }
        selectedRoom = (Room) roomComboBox.getSelectedItem();
    }

    private void updateBooking(Booking booking) {
        // Check if there are any changes to save
        if (booking.getGuestFirstName().equals(enteredGuestFirstName) &&
                booking.getGuestLastName().equals(enteredGuestLastName) &&
                booking.getCheckInDate().equals(selectedCheckInDate) &&
                booking.getCheckOutDate().equals(selectedCheckOutDate) &&
                booking.getNumberOfAdults() == enteredNumberOfAdults &&
                booking.getNumberOfChildren() == enteredNumberOfChildren &&
                booking.getRoom().equals(selectedRoom)) {
            JOptionPane.showMessageDialog(null, "No changes to save", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // If there are changes, update the booking
        booking.setGuestFirstName(enteredGuestFirstName);
        booking.setGuestLastName(enteredGuestLastName);
        booking.setCheckInDate(selectedCheckInDate);
        booking.setCheckOutDate(selectedCheckOutDate);
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
                selectedCheckInDate,
                selectedCheckOutDate,
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
        roomComboBox.setSelectedItem(booking.getRoom());
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
    }

    private boolean hasChanges(Booking booking) {
        // identify the which camp has changed and print in console
        if (!booking.getGuestFirstName().equals(enteredGuestFirstName)) {
            System.out.println("Guest First Name has changed");
        }
        if (!booking.getGuestLastName().equals(enteredGuestLastName)) {
            System.out.println("Guest Last Name has changed");
        }
        if (!booking.getCheckInDate().equals(selectedCheckInDate)) {
            System.out.println("Check In Date has changed");
        }
        if (!booking.getCheckOutDate().equals(selectedCheckOutDate)) {
            System.out.println("Check Out Date has changed");
        }
        if (booking.getNumberOfAdults() != enteredNumberOfAdults) {
            System.out.println("Number of Adults has changed");
        }
        if (booking.getNumberOfChildren() != enteredNumberOfChildren) {
            System.out.println("Number of Children has changed");
        }
        if (!booking.getRoom().equals(selectedRoom)) {
            System.out.println("Room has changed");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String formattedCheckInDate = sdf.format(selectedCheckInDate);
        String formattedCheckOutDate = sdf.format(selectedCheckOutDate);
        String formattedBookingCheckInDate = sdf.format(booking.getCheckInDate());
        String formattedBookingCheckOutDate = sdf.format(booking.getCheckOutDate());

        return !booking.getGuestFirstName().equals(enteredGuestFirstName) ||
                !booking.getGuestLastName().equals(enteredGuestLastName) ||
                !formattedBookingCheckInDate.equals(formattedCheckInDate) ||
                !formattedBookingCheckOutDate.equals(formattedCheckOutDate) ||
                booking.getNumberOfAdults() != enteredNumberOfAdults ||
                booking.getNumberOfChildren() != enteredNumberOfChildren ||
                !booking.getRoom().equals(selectedRoom);
    }

    private void updateRoomList(Booking booking) {
        List<Room> suitableAndAvailableRooms = roomDao.getSuitableAndAvailableRooms(
                enteredNumberOfAdults,
                enteredNumberOfChildren,
                selectedCheckInDate,
                selectedCheckOutDate
        );

        if (booking != null && suitableAndAvailableRooms.contains(booking.getRoom())) {
            suitableAndAvailableRooms.add(booking.getRoom());
        }

        // Sort the rooms by room number
        suitableAndAvailableRooms.sort(Comparator.comparing(Room::getNumber));

        roomComboBox.setModel(new DefaultComboBoxModel(suitableAndAvailableRooms.toArray()));

        if (selectedRoom != null && suitableAndAvailableRooms.contains(selectedRoom)) {
            roomComboBox.setSelectedItem(selectedRoom);
        } else if (!suitableAndAvailableRooms.isEmpty()) {
            roomComboBox.setSelectedItem(suitableAndAvailableRooms.get(0));
        }
    }
}