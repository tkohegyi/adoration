package org.rockhill.adorApp.web;
import java.net.URL;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import org.rockhill.adorApp.web.service.ServerException;
import org.rockhill.adorApp.web.service.WebAppStopper;

/**
 * Responsible for configuring, starting and stopping the jetty server.
 */

public class WebAppServer {

    private static final String SHUTDOWN_URL = "/adorationsecure/shutdown";
    private static final int STOP_TIMEOUT = 5000;
    private static final String WEB_XML_LOCATION = "/WEB-INF/web.xml";
    private static final String WEBAPP_ROOT = "webapp";
    private Server server;

    /**
     * Creates and configures the webapp server.
     * @param port the port the server listens on
     */
    public void createServer(final Integer port, final boolean isHttpsInUse) {
        WebAppContext context = configureWebAppContext();
        createServer(context, port, isHttpsInUse);
    }

    /**
     * Starts the jetty server.
     */
    public void start() {
        try {
            startJettyServer();
        } catch (Exception e) {
            throw new ServerException("Jetty server cannot be started. Reason: " + e.getMessage(), e);
        }
    }

    /**
     * Stops the server.
     */
    public void stop() {
        try {
            if (server != null && server.isStarted()) {
                stopJettyServer();
            }
        } catch (Exception e) {
            throw new ServerException("Internal web application can not be stopped: " + e.getMessage(), e);
        }
    }

    private WebAppContext configureWebAppContext() {
        final WebAppContext context = new WebAppContext();
        String baseUrl = getBaseUrl();
        context.setDescriptor(baseUrl + WEB_XML_LOCATION);
        context.setResourceBase(baseUrl + "");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        return context;
    }

    void createServer(final WebAppContext context, final Integer port, final boolean isHttpsInUse) {
        server = new Server(port);
        server.setStopAtShutdown(true);
        server.setStopTimeout(STOP_TIMEOUT);
        server.setHandler(context);

        //HTTPS part
        if (isHttpsInUse) {
            HttpConfiguration http_config = new HttpConfiguration();
            http_config.setSecureScheme("https");
            http_config.setSecurePort(port);
            HttpConfiguration https_config = new HttpConfiguration(http_config);
            https_config.addCustomizer(new SecureRequestCustomizer());
            //TODO both keystore and its pwd need to be stored in property file
            SslContextFactory sslContextFactory = new SslContextFactory("security/adorApp.keystore");
            sslContextFactory.setKeyStorePassword("OBF:1rhd1v921aps1apu1apo1apq1v9k1rj1");      //Rf7856op
            ServerConnector httpsConnector = new ServerConnector(server,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(https_config));
            httpsConnector.setPort(port);
            httpsConnector.setIdleTimeout(50000);
            server.setConnectors(new Connector[]{httpsConnector});
        }
    }

    void startJettyServer() throws Exception {
        server.start();
        server.join();
    }

    void stopJettyServer() throws Exception {
        server.stop();
        server.join();
    }

    private String getBaseUrl() {
        URL webInfUrl = WebAppServer.class.getClassLoader().getResource(WEBAPP_ROOT);
        return webInfUrl.toExternalForm();
    }

}
