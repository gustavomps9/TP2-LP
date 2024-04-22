package database.dao;

import database.HibernateUtil;
import entities.Status;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class StatusDao {
    public List<Status> getAll() {
        Session session = HibernateUtil.getSession();
        List<Status> statuses = session.createQuery("from Status", Status.class).list();
        session.close();
        return statuses;
    }

    public Status getById(int id) {
        Session session = HibernateUtil.getSession();
        Status status = session.get(Status.class, id);
        session.close();
        return status;
    }

    public void save(Status status) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            session.save(status);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && session.isOpen()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void saveAll(List<Status> statuses) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            for (Status status : statuses) {
                session.save(status);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && session.isOpen()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(Status status) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            session.update(status);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && session.isOpen()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(Status status) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.delete(status);
        session.getTransaction().commit();
        session.close();
    }

    public void deleteAll() {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.createQuery("delete from Status").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
