import com.formdev.flatlaf.FlatLightLaf;
import database.dao.BookingDao;
import database.dao.RoomDao;
import database.dao.StatusDao;
import entities.Booking;
import entities.Room;
import entities.Status;
import views.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookingApplication extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardsPanel;
    private final JButton homeButton;
    private final JButton bookingsButton;
    private final JButton roomsButton;

    public BookingApplication() {
        // Set up the main frame
        setTitle("Booking Application");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up a toolbar for navigation
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // Create buttons for navigation
        homeButton = new JButton("Home");
        homeButton.addActionListener(e -> showHomepage());
        toolBar.add(homeButton);

        bookingsButton = new JButton("Bookings");
        bookingsButton.addActionListener(e -> showBookingList());
        toolBar.add(bookingsButton);

        roomsButton = new JButton("Rooms");
        roomsButton.addActionListener(e -> showRoomList());
        toolBar.add(roomsButton);

        // Add the toolbar to the top of the frame
        add(toolBar, BorderLayout.NORTH);

        // CardLayout to switch between different views
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        // Add individual view panels
        SplashScreenView splashScreenView = new SplashScreenView();
        HomepageView homepageView = new HomepageView();
        RoomListView roomListView = new RoomListView(cardLayout, cardsPanel);
        RoomFormView RoomFormView = new RoomFormView(cardLayout, cardsPanel, null);
        BookingFormView bookingFormView = new BookingFormView(cardLayout, cardsPanel, null);
        BookingListView bookingListView = new BookingListView(cardLayout, cardsPanel);

        cardsPanel.add(splashScreenView, "Splash");
        cardsPanel.add(homepageView, "Homepage");
        cardsPanel.add(roomListView, "Rooms");
        cardsPanel.add(RoomFormView, "RoomForm");
        cardsPanel.add(bookingListView, "Bookings");
        cardsPanel.add(bookingFormView, "BookingForm");

        // Display the RoomListView initially
        add(cardsPanel, BorderLayout.CENTER);

        showHomepage(); // Show the homepage initially
    }

    private void showSplashScreen() {
        cardLayout.show(cardsPanel, "Splash");
    }

    private void showHomepage() {
        HomepageView homepageView = (HomepageView) cardsPanel.getComponent(1);
        homepageView.refreshTables();
        cardLayout.show(cardsPanel, "Homepage");
    }

    private void showRoomList() {
        cardLayout.show(cardsPanel, "Rooms");
    }

    private void showBookingList() {
        BookingListView bookingListView = (BookingListView) cardsPanel.getComponent(4);
        bookingListView.refreshTable();
        cardLayout.show(cardsPanel, "Bookings");
    }

    public static void main(String[] args) {
        RoomDao roomDao = new RoomDao();
        BookingDao bookingDao = new BookingDao();
        StatusDao statusDao = new StatusDao();

//        bookingDao.deleteAll();
//        roomDao.deleteAll();
//        statusDao.deleteAll();

        if (statusDao.getAll().isEmpty()) {
            createSampleData(roomDao, statusDao);
        }

        //FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new BookingApplication().setVisible(true);
        });
    }

    private static void createSampleData(RoomDao roomDao, StatusDao statusDao) {
        List<Room> rooms = List.of(
                new Room(101, 1, 0, 34.00f),
                new Room(102, 2, 0, 48.00f),
                new Room(103, 2, 1, 52.00f),
                new Room(104, 2, 2, 64.00f),
                new Room(201, 1, 0, 34.00f),
                new Room(202, 2, 0, 48.00f),
                new Room(203, 2, 1, 52.00f),
                new Room(204, 2, 2, 64.00f),
                new Room(301, 4, 2, 107.00f),
                new Room(302, 4, 2, 107.00f)
        );
        roomDao.saveAll(rooms);

        List<Status> statuses = List.of(
                new Status(1, "Booked"),
                new Status(2, "Checked In"),
                new Status(3, "Checked Out"),
                new Status(4, "Cancelled")
        );
        statusDao.saveAll(statuses);
    }
}