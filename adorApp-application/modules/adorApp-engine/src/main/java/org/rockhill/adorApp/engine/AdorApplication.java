package org.rockhill.adorApp.engine;

import org.rockhill.adorApp.bootstrap.AdorAppBootstrap;

/**
 * Starts the application.
 */
public final class AdorApplication {

    public static String[] arguments; //NOSONAR

    private AdorApplication() {
    }

    /**
     * The app main entry point.
     *
     * @param args The program needs the path of conf.properties to run.
     */
    public static void main(final String[] args) {
        arguments = args; //NOSONAR
        new AdorAppBootstrap().bootstrap(args);
    }
}
