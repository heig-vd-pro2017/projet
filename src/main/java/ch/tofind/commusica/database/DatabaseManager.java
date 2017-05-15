package ch.tofind.commusica.database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @brief This class represents the database and allows interaction with the real database
 */
public class DatabaseManager {

    //! Shared instance of the object for all the application.
    private static DatabaseManager instance = null;

    //! SessionFactory (Hibernate related).
    private SessionFactory factory = null;

    //! UserSession (Hibernate related).
    private Session session = null;

    //! Transaction (Hibernate related).
    private Transaction transaction = null;

    /**
     * @brief DatabaseManager single constructor. Avoid the instantiation.
     */
    private DatabaseManager() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            factory = configuration.buildSessionFactory();
            session = factory.openSession();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Get the object instance.
     *
     * @return The instance of the object.
     */
    public static DatabaseManager getInstance() {

        if(instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }

        return instance;
    }

    public Session getSession() {
        return session;
    }

    public Object save(Object object) {
        Object id = null;

        try {
            transaction = session.beginTransaction();
            id = session.save(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return id;
    }

    public void delete(Object object) {
        try {
            transaction = session.beginTransaction();
            session.delete(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(Object object) {
        try {
            transaction = session.beginTransaction();
            session.update(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void close() {
        if(instance != null) {
            session.close();
            factory.close();
        }
    }
}
