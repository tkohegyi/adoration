package org.rockhill.adorApp.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is to provide general connection to the database server.
 */
public class SessionFactoryHelper {
    private static final Logger SESSION_FACTORY_HELPER_LOGGER = LoggerFactory.getLogger(SessionFactoryHelper.class);
    private static final SessionFactory sessionFactory;

    static {
        SESSION_FACTORY_HELPER_LOGGER.info("Connecting to database...");
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
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
}
