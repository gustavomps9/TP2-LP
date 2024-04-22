import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import views.*;

import javax.swing.*;
import java.awt.*;

public class BookingApplication extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private JButton roomsButton;
    private JButton bookingsButton;

    public BookingApplication() {
        // Set up the main frame
        setTitle("Booking Application");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up a toolbar for navigation
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // Create buttons with icons for navigation
        roomsButton = new JButton("Rooms");
        roomsButton.addActionListener(e -> showRoomList());
        toolBar.add(roomsButton);

        bookingsButton = new JButton("Bookings");
        bookingsButton.addActionListener(e -> showBookingList());
        toolBar.add(bookingsButton);

        // Add the toolbar to the top of the frame
        add(toolBar, BorderLayout.NORTH);

        // CardLayout to switch between different views
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        // Add individual view panels
        SplashScreenView splashScreenView = new SplashScreenView();
        RoomListView roomListView = new RoomListView(cardLayout, cardsPanel);
        RoomFormView RoomFormView = new RoomFormView(cardLayout, cardsPanel);
        BookingFormView bookingFormView = new BookingFormView(cardLayout, cardsPanel);
        BookingListView bookingListView = new BookingListView(cardLayout, cardsPanel);

        cardsPanel.add(splashScreenView, "Splash");
        cardsPanel.add(roomListView, "Rooms");
        cardsPanel.add(RoomFormView, "RoomForm");
        cardsPanel.add(bookingListView, "Bookings");
        cardsPanel.add(bookingFormView, "BookingForm");

        // Display the RoomListView initially
        add(cardsPanel, BorderLayout.CENTER);

        showRoomList(); // Start with RoomListView
    }

    private void showSplashScreen() {
        cardLayout.show(cardsPanel, "Splash");
    }

    private void showHome() {
        cardLayout.show(cardsPanel, "Home");
    }

    private void showRoomList() {
        cardLayout.show(cardsPanel, "Rooms");
    }

    private void showRoomForm() {
        cardLayout.show(cardsPanel, "RoomForm");
    }

    private void showBookingList() {
        cardLayout.show(cardsPanel, "Bookings");
    }

    private void showBookingForm() {
        cardLayout.show(cardsPanel, "BookingForm");
    }

    public static void main(String[] args) {
        //FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new BookingApplication().setVisible(true);
        });
    }
}