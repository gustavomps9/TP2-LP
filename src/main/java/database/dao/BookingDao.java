package database.dao;

import database.HibernateUtil;
import entities.Booking;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class BookingDao {
    public List<Booking> getAll() {
        Session session = HibernateUtil.getSession();
        Query<Booking> query = session.createQuery("from Booking", Booking.class);
        List<Booking> bookings = query.list();
        session.close();
        return bookings;
    }

    public Booking getById(int id) {
        Session session = HibernateUtil.getSession();
        Booking booking = session.get(Booking.class, id);
        session.close();
        return booking;
    }

    public List<Booking> getByRoomId(int roomId) {
        Session session = HibernateUtil.getSession();
        Query<Booking> query = session.createQuery("from Booking where room.id = :roomId", Booking.class);
        query.setParameter("roomId", roomId);
        List<Booking> bookings = query.list();
        session.close();
        return bookings;
    }

    public boolean isRoomBooked(int roomId, Date checkInDate, Date checkOutDate) {
        Session session = HibernateUtil.getSession();
        Query<Booking> query = session.createQuery("from Booking where room.id = :roomId and checkInDate < :checkOutDate and checkOutDate > :checkInDate", Booking.class);
        query.setParameter("roomId", roomId);
        query.setParameter("checkInDate", checkInDate);
        query.setParameter("checkOutDate", checkOutDate);
        List<Booking> bookings = query.list();
        session.close();
        return !bookings.isEmpty();
    }

    public boolean isRoomBooked(Booking booking) {
        Session session = HibernateUtil.getSession();
        Query<Booking> query = session.createQuery("from Booking where room.id = :roomId and checkInDate < :checkOutDate and checkOutDate > :checkInDate and id != :bookingId", Booking.class);
        query.setParameter("roomId", booking.getRoomId());
        query.setParameter("checkInDate", booking.getCheckInDate());
        query.setParameter("checkOutDate", booking.getCheckOutDate());
        query.setParameter("bookingId", booking.getId());
        List<Booking> bookings = query.list();
        session.close();
        return !bookings.isEmpty();
    }

    public void save(Booking booking) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            // start a transaction
            transaction = session.beginTransaction();
            // save the booking object
            session.save(booking);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && session.isOpen()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void saveAll(List<Booking> bookings) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            // start a transaction
            transaction = session.beginTransaction();
            // save all booking objects
            for (Booking booking : bookings) {
                session.save(booking);
            }
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && session.isOpen()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(Booking booking) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // update the booking object
            session.update(booking);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(Booking booking) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // delete the booking object
            session.delete(booking);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    //delete all
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // delete all booking objects
            session.createQuery("delete from Booking").executeUpdate();
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
