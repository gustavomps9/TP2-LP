package database.dao;

import database.HibernateUtil;
import entities.Room;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

public class RoomDao {
    public List<Room> getAll() {
        Session session = HibernateUtil.getSession();
        Query<Room> query = session.createQuery("from Room", Room.class);
        List<Room> rooms = query.list();
        session.close();
        return rooms;
    }

    public Room getById(int id) {
        Session session = HibernateUtil.getSession();
        Room room = session.get(Room.class, id);
        session.close();
        return room;
    }

    public Room getByNumber(int number) {
        Session session = HibernateUtil.getSession();
        Query<Room> query = session.createQuery("from Room where roomNumber = :number", Room.class);
        query.setParameter("number", number);
        Room room = query.uniqueResult();
        session.close();
        return room;
    }

    public List<Room> getSuitableRooms(int adults, int children) {
        Session session = HibernateUtil.getSession();
        Query<Room> query = session.createQuery("from Room where adultsCapacity >= :adults and childrenCapacity >= :children", Room.class);
        query.setParameter("adults", adults);
        query.setParameter("children", children);
        List<Room> rooms = query.list();
        session.close();
        return rooms;
    }

    public List<Room> getSuitableAndAvailableRooms(int adults, int children, Date checkIn, Date checkOut) {
        Session session = HibernateUtil.getSession();
        Query<Room> query = session.createQuery("from Room where adultsCapacity >= :adults and childrenCapacity >= :children and id not in (select room.id from Booking where (checkInDate <= :checkIn and checkOutDate >= :checkIn) or (checkInDate <= :checkOut and checkOutDate >= :checkOut) or (checkInDate >= :checkIn and checkOutDate <= :checkOut))", Room.class);
        query.setParameter("adults", adults);
        query.setParameter("children", children);
        query.setParameter("checkIn", checkIn);
        query.setParameter("checkOut", checkOut);
        List<Room> rooms = query.list();
        session.close();
        return rooms;
    }

    public boolean existByNumber(int number) {
        Session session = HibernateUtil.getSession();
        Query<Room> query = session.createQuery("from Room where roomNumber = :number", Room.class);
        query.setParameter("number", number);
        List<Room> rooms = query.list();
        session.close();
        return !rooms.isEmpty();
    }

    public void save(Room room) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            // start a transaction
            transaction = session.beginTransaction();
            // save the room object
            session.save(room);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && session.isOpen()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    //save all
    public void saveAll(List<Room> rooms) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            // start a transaction
            transaction = session.beginTransaction();
            // save all room objects
            for (Room room : rooms) {
                session.save(room);
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

    public void update(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // update the room object
            session.update(room);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // delete the room object
            session.delete(room);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // delete all room objects
            session.createQuery("delete from Room").executeUpdate();
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