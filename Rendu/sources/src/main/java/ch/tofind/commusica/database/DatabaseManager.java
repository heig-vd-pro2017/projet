package ch.tofind.commusica.database;

import ch.tofind.commusica.utils.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

/**
 * This class represents the database and allows interaction with the real database
 */
public class DatabaseManager {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(DatabaseManager.class.getSimpleName());

    //! Shared instance of the object for all the application.
    private static DatabaseManager instance = null;

    //! SessionFactory (Hibernate related).
    private SessionFactory factory;

    //! Session (Hibernate related).
    private Session session;

    //! Transaction (Hibernate related).
    private Transaction transaction;

    /**
     * DatabaseManager single constructor. Avoid the instantiation.
     */
    private DatabaseManager() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            factory = configuration.buildSessionFactory();
            session = factory.openSession();
        } catch (HibernateException e) {
            LOG.error(e);
        }
    }

    /**
     * Get the object instance.
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

    /**
     * Get the Hibernate session.
     *
     * @return The Hibernate session.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Execute the query on the database.
     *
     * @param query The query to execute.
     */
    public void execute(Query query) {

        try {
            // Wait while transaction is still active.
            while(transaction != null && transaction.isActive());

            transaction = session.beginTransaction();
            query.executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOG.error(e);
        }
    }

    /**
     * Save the object in the database.
     *
     * @param object The object to save.
     */
    public void save(Object object) {

        try {
            // Wait while transaction is still active.
            while(transaction != null && transaction.isActive());

            transaction = session.beginTransaction();
            session.saveOrUpdate(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOG.error(e);
        }
    }

    /**
     * Delete the object from the database.
     *
     * @param object The object to delete.
     */
    public void delete(Object object) {
        try {
            // Wait while transaction is still active.
            while(transaction != null && transaction.isActive());

            transaction = session.beginTransaction();
            session.delete(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOG.error(e);
        }
    }

    /**
     * Update the object from the database.
     *
     * @param object The object to update.
     */
    public void update(Object object) {
        try {
            // Wait while transaction is still active.
            while(transaction != null && transaction.isActive());

            transaction = session.beginTransaction();
            session.update(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOG.error(e);
        }
    }

    /**
     * Close the database connection.
     */
    public void close() {

        if(instance != null) {
            session.close();
            factory.close();

            instance = null;
        }

    }
}
