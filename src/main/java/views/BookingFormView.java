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
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
    private JButton deleteButton;

    // variables for the booking form
    private BookingDao bookingDao = new BookingDao();
    private RoomDao roomDao = new RoomDao();
    private List<Room> rooms = roomDao.getAll();
    private String enteredGuestFirstName, enteredGuestLastName;
    private int enteredNumberOfAdults, enteredNumberOfChildren;
    private Date checkInDate, checkOutDate;
    private Room selectedRoom;

    public BookingFormView(CardLayout cardLayout, JPanel parentPanel, Booking booking) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Guest First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Guest First Name:"), gbc);

        gbc.gridx = 1;
        guestFirstNameField = new JTextField(20); // Aumenta o número de colunas para aumentar a largura
        add(guestFirstNameField, gbc);

        // Guest Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Guest Last Name:"), gbc);

        gbc.gridx = 1;
        guestLastNameField = new JTextField(20); // Aumenta o número de colunas para aumentar a largura
        add(guestLastNameField, gbc);


        // Properties for the date pickers
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        // Check-In Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Check-In Date:"), gbc);

        UtilDateModel checkInModel = new UtilDateModel();
        // Set the date to today
        Calendar today = Calendar.getInstance();
        checkInModel.setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        checkInModel.setSelected(true);

        checkInDatePicker = new JDatePickerImpl(new JDatePanelImpl(checkInModel, p), new JFormattedTextField.AbstractFormatter() {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.trim().isEmpty()) {
                    return null;
                }
                return dateFormat.parse(text);
            }

            @Override
            public String valueToString(Object value) throws ParseException {
                if (value == null) {
                    return "";
                }
                Calendar cal = (Calendar) value;
                return dateFormat.format(cal.getTime());
            }
        });
        // Check if the selected check-in date is before the check-out date
        PropertyChangeListener checkInListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equals(evt.getPropertyName())) {
                    Date selectedDate = (Date) evt.getNewValue();
                    if (selectedDate == null) {
                        checkInDatePicker.getModel().removePropertyChangeListener(this); // remove listener
                        Calendar today = Calendar.getInstance();
                        checkInDatePicker.getModel().setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                        checkInDatePicker.getModel().addPropertyChangeListener(this); // add listener back
                    }
                }
            }
        };
        checkInDatePicker.getModel().addPropertyChangeListener(checkInListener);

        gbc.gridx = 1;
        add(checkInDatePicker, gbc);

        // Check-Out Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Check-Out Date:"), gbc);

        UtilDateModel checkOutModel = new UtilDateModel();
        // Set the date to tomorrow
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        checkOutModel.setDate(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DAY_OF_MONTH));
        checkOutModel.setSelected(true);

        checkOutDatePicker = new JDatePickerImpl(new JDatePanelImpl(checkOutModel, p), new JFormattedTextField.AbstractFormatter() {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.trim().isEmpty()) {
                    return null;
                }
                return dateFormat.parse(text);
            }

            @Override
            public String valueToString(Object value) throws ParseException {
                if (value == null) {
                    return "";
                }
                Calendar cal = (Calendar) value;
                return dateFormat.format(cal.getTime());
            }
        });
        // Check if the selected date is before the check-in date
        // Check-Out Date
        PropertyChangeListener checkOutListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equals(evt.getPropertyName())) {
                    Date selectedDate = (Date) evt.getNewValue();
                    Date checkInDate = (Date) checkInDatePicker.getModel().getValue();
                    if (selectedDate == null || selectedDate.before(checkInDate)) {
                        SwingUtilities.invokeLater(() -> {
                            checkOutDatePicker.getModel().removePropertyChangeListener(this); // remove listener
                            checkOutDatePicker.getModel().setDate(checkInDatePicker.getModel().getYear(), checkInDatePicker.getModel().getMonth(), checkInDatePicker.getModel().getDay());
                            checkOutDatePicker.getModel().addPropertyChangeListener(this); // add listener back
                        });
                    }
                }
            }
        };
        checkOutDatePicker.getModel().addPropertyChangeListener(checkOutListener);

        // Check if the selected check-out date is after the check-in date
        checkInDatePicker.getModel().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("value".equals(evt.getPropertyName())) {
                    Date newCheckInDate = (Date) evt.getNewValue();
                    Date currentCheckOutDate = (Date) checkOutDatePicker.getModel().getValue();
                    if (currentCheckOutDate != null && newCheckInDate != null && newCheckInDate.after(currentCheckOutDate)) {
                        checkOutDatePicker.getModel().removePropertyChangeListener(checkOutListener); // remove listener
                        checkOutDatePicker.getModel().setDate(newCheckInDate.getYear() + 1900, newCheckInDate.getMonth(), newCheckInDate.getDate());
                        checkOutDatePicker.getModel().addPropertyChangeListener(checkOutListener); // add listener back
                    }
                }
            }
        });

        gbc.gridx = 1;
        add(checkOutDatePicker, gbc);

        // Number of Adults
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Number of Adults:"), gbc);

        gbc.gridx = 1;
        SpinnerNumberModel adultModel = new SpinnerNumberModel(1, 1, 10, 1);
        numberOfAdultsSpinner = new JSpinner(adultModel);
        add(numberOfAdultsSpinner, gbc);

        // Number of Children
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Number of Children:"), gbc);

        gbc.gridx = 1;
        SpinnerNumberModel childrenModel = new SpinnerNumberModel(0, 0, 10, 1);
        numberOfChildrenSpinner = new JSpinner(childrenModel);
        add(numberOfChildrenSpinner, gbc);

        // Room Selector
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Room:"), gbc);

        gbc.gridx = 1;
        roomComboBox = new JComboBox<>();
        for (Room room : rooms) {
            roomComboBox.addItem(String.valueOf(room.getRoomNumber())); // Adiciona o número do quarto ao ComboBox
        }
        AutoCompleteDecorator.decorate(roomComboBox); // Adiciona autocompletar ao ComboBox
        add(roomComboBox, gbc);

        // Delete and Cancel Buttons
        deleteButton = new JButton("Delete");
        deleteButton.setForeground(Color.RED); // Set text color to red
        if (booking == null) {
            deleteButton.setEnabled(false); // Disable delete button if no booking is selected
        }
        cancelButton = new JButton("Cancel");

        GridBagLayout buttonLayout = new GridBagLayout();
        GridBagConstraints buttonGbc = new GridBagConstraints();
        JPanel buttonPanel = new JPanel(buttonLayout);

        buttonGbc.gridx = 0;
        buttonGbc.gridy = 0;
        buttonGbc.weightx = 1.0; // Allow horizontal growth
        buttonGbc.fill = GridBagConstraints.HORIZONTAL; // Fill the container horizontally
        buttonGbc.anchor = GridBagConstraints.EAST; // Align to the right
        buttonGbc.insets = new Insets(0, 0, 0, 5); // Add some space to the right of the delete button
        buttonPanel.add(deleteButton, buttonGbc);

        buttonGbc.gridx = 1;
        buttonGbc.gridy = 0;
        buttonGbc.weightx = 1.0; // Allow horizontal growth
        buttonGbc.fill = GridBagConstraints.HORIZONTAL; // Fill the container horizontally
        buttonGbc.anchor = GridBagConstraints.WEST; // Align to the left
        buttonGbc.insets = new Insets(0, 5, 0, 0); // Add some space to the left of the cancel button
        buttonPanel.add(cancelButton, buttonGbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END; // Align to the right end of the cell
        add(buttonPanel, gbc);

        // Submit Button
        submitButton = new JButton("Submit");

        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END; // Align to the right end of the cell
        add(submitButton, gbc);

        // If booking is not null, populate the form fields
        if (booking != null) {
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

        // Button actions
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if any field is blank
                if (guestFirstNameField.getText().trim().isEmpty() ||
                        guestLastNameField.getText().trim().isEmpty() ||
                        checkInDatePicker.getModel().getValue() == null ||//
                        checkOutDatePicker.getModel().getValue() == null ||//
                        roomComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Assign values from the form
                assignValues();

                // Check if the selected room is available
                if (booking != null) {
                    if (bookingDao.isRoomBooked(booking)) {
                        JOptionPane.showMessageDialog(null, "Room is already booked for the selected dates", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    if (bookingDao.isRoomBooked(selectedRoom.getId(), checkInDate, checkOutDate)) {
                        JOptionPane.showMessageDialog(null, "Room is already booked for the selected dates", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // Update / save the booking
                if (booking != null) {
                    updateBooking(booking);
                } else {
                    saveBooking();
                }

                // Refresh the table in BookingListView
                BookingListView bookingListView = (BookingListView) parentPanel.getComponent(3);
                bookingListView.refreshTable(); // Refresh the table
                cardLayout.show(parentPanel, "Bookings"); // Go back to BookingListView
                System.out.println("Booking was successfully saved/updated");
                clearForm();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
                cardLayout.show(parentPanel, "Bookings"); // Return to BookingListView
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Booking Form Deleted");
                // ask for confirmation
                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this booking?", "Delete Booking", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    bookingDao.delete(booking);
                    System.out.println("Booking deleted from database");
                    cardLayout.show(parentPanel, "Bookings"); // Return to BookingListView
                    clearForm();
                }
                clearForm();
            }
        });

        //clearForm();
    }

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
        String selectedRoomNumber = (String) roomComboBox.getSelectedItem();
        selectedRoom = rooms.stream()
                .filter(room -> String.valueOf(room.getRoomNumber()).equals(selectedRoomNumber))
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
        StatusDao statusDao = new StatusDao();
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

    private void clearForm() {
        guestFirstNameField.setText("");
        guestLastNameField.setText("");
        numberOfAdultsSpinner.setValue(1);
        numberOfChildrenSpinner.setValue(0);
        roomComboBox.setSelectedIndex(0);
        // Set the check-in date to today
        Calendar calendar = Calendar.getInstance();
        checkInDatePicker.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        checkInDatePicker.getModel().setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 1);
        numberOfAdultsSpinner.setValue(1);
        numberOfChildrenSpinner.setValue(0);
        roomComboBox.setSelectedIndex(0);
    }
}

