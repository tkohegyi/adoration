package org.rockhill.adoration.initialize;

/**
 * Holds module specific properties.
 */
public class PropertyDTO {

    private final Integer port;

    /**
     * Constructs a new property holding object with the given fields.
     *
     * @param port the port used by the web application
     */
    public PropertyDTO(final Integer port) {
        super();
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

}
