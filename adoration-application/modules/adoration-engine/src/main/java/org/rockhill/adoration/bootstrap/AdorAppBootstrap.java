package org.rockhill.adoration.bootstrap;

import org.rockhill.adoration.bootstrap.helper.SystemExceptionSelector;
import org.rockhill.adoration.database.SessionFactoryHelper;
import org.rockhill.adoration.database.property.HibernateConfig;
import org.rockhill.adoration.exception.InvalidPropertyException;
import org.rockhill.adoration.exception.SystemException;
import org.rockhill.adoration.properties.PropertyLoader;
import org.rockhill.adoration.properties.helper.PropertiesNotAvailableException;
import org.rockhill.adoration.web.WebAppServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;

import java.util.Properties;

/**
 * Bootstrap class that starts the application engine.
 */
public class AdorAppBootstrap {

    private final Logger logger = LoggerFactory.getLogger(AdorAppBootstrap.class);
    private final SystemExceptionSelector systemExceptionSelector = new SystemExceptionSelector();
    private final PropertyLoader propertyLoader = new PropertyLoader();

    WebAppServer createWebAppServer() {
        return new WebAppServer();
    }

    /**
     * Starts the application.
     * @param args command line arguments
     */
    public void bootstrap(final String[] args) {

        HibernateConfig.HIBERNATE_CONNECTION_PASSWORD = null;
        HibernateConfig.HIBERNATE_CONNECTION_URL = null;
        HibernateConfig.HIBERNATE_CONNECTION_USERNAME = null;

        WebAppServer webAppServer = createWebAppServer();
        try {
            //prepare hibernate
            String hibernateUsername = getStringInfo(args, "hibernate.connection.username");
            String hibernatePassword = getStringInfo(args, "hibernate.connection.password");
            String hibernateUrl = getStringInfo(args, "hibernate.connection.url");
            HibernateConfig.HIBERNATE_CONNECTION_USERNAME = hibernateUsername;
            HibernateConfig.HIBERNATE_CONNECTION_PASSWORD = hibernatePassword;
            HibernateConfig.HIBERNATE_CONNECTION_URL = hibernateUrl;
            //prepare web server
            String serverKeyStoreFile = getStringInfo(args, "keyStoreFile");
            String serverKeyStorePassword = getStringInfo(args, "keyStorePassword");
            Integer port = getPort(args);
            Boolean isHttpsInUse = getIsHttpsInUse(args);
            webAppServer.createServer(port, isHttpsInUse.booleanValue(), serverKeyStoreFile, serverKeyStorePassword);
            webAppServer.start();
        } catch (BeanCreationException e) {
            handleException(webAppServer, e);
        } catch (SystemException e) {
            logError(e);
            webAppServer.stop();
            SessionFactoryHelper.shutdownHibernateSessionFactory();
        }

    }

    private String getStringInfo(String[] args, String propertyName) {
        checkPropertyFileArgument(args);
        Properties properties = propertyLoader.loadProperties(args[0]);
        String info = properties.getProperty(propertyName);
        return info;
    }

    private Integer getPort(final String[] args) {
        checkPropertyFileArgument(args);
        Properties properties = propertyLoader.loadProperties(args[0]);
        Integer port = null;
        try {
            port = Integer.valueOf(properties.getProperty("webapp.port"));
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException("Invalid port value!");
        }
        return port;
    }

    private Boolean getIsHttpsInUse(final String[] args) {
        checkPropertyFileArgument(args);
        Properties properties = propertyLoader.loadProperties(args[0]);
        Boolean isHttpsInUse;
        try {
            isHttpsInUse = Boolean.valueOf(properties.getProperty("isHttpsInUse"));
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException("Invalid isHttpsInUse value!");
        }
        return isHttpsInUse;
    }

    private void checkPropertyFileArgument(final String[] args) {
        if (args.length == 0) {
            throw new PropertiesNotAvailableException("Configuration file was not specified as input argument!");
        } else if (!args[0].endsWith(".properties")) {
            throw new PropertiesNotAvailableException("Configuration file must be a properties file!");
        }
    }

    private void handleException(final WebAppServer webAppServer, final BeanCreationException e) {
        SystemException ex = systemExceptionSelector.getSystemException(e);
        if (ex != null) {
            logError(ex);
            webAppServer.stop();
            SessionFactoryHelper.shutdownHibernateSessionFactory();
        } else {
            throw e;
        }
    }

    private void logError(final Exception e) {
        logger.error("Application cannot be started: " + e.getMessage());
    }
}
