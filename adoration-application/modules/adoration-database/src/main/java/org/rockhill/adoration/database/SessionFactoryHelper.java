package org.rockhill.adoration.database;

import com.sun.istack.NotNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.rockhill.adoration.database.exception.DatabaseHandlingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is to provide general connection to the database server.
 * Requires an external class that has info about DB connection details - in addition to hibernate.cfg.xml.
 */
public class SessionFactoryHelper {
    private static final Logger SESSION_FACTORY_HELPER_LOGGER = LoggerFactory.getLogger(SessionFactoryHelper.class);
    private static SessionFactory sessionFactory = null;

    private synchronized static void createSessionFactory(final StandardServiceRegistry registry) {
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public void initiateHibernateSessionFactory(final String connectionUrl, final String userName, final String password) {
        SESSION_FACTORY_HELPER_LOGGER.info("Connecting to database...");
        if ((connectionUrl == null) || (userName == null) || (password == null)) {
            throw new ExceptionInInitializerError("Unknown DB connection parameters.");
        }

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .applySetting("hibernate.connection.url", connectionUrl)
                .applySetting("hibernate.connection.username", userName)
                .applySetting("hibernate.connection.password", password)
                .build();
        try {
            createSessionFactory(registry);
            SESSION_FACTORY_HELPER_LOGGER.info("Connection to database established correctly.");
        } catch (Throwable e) {
            SESSION_FACTORY_HELPER_LOGGER.error("Error in creating SessionFactory object.", e);
            // The registry would be destroyed by the DatabaseConnection, but we had trouble building the DatabaseConnection
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdownHibernateSessionFactory() {
        if (sessionFactory != null) {
            SESSION_FACTORY_HELPER_LOGGER.info("Closing database connection...");
            sessionFactory.close();
            SESSION_FACTORY_HELPER_LOGGER.info("Connection to database closed successfully.");
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @NotNull
    public static Session getOpenedSession() {
        if (sessionFactory == null) {
            throw new DatabaseHandlingException("Database connection issue (SessionFactory) - pls contact to maintainers.");
        }
        return sessionFactory.openSession();
    }
}
