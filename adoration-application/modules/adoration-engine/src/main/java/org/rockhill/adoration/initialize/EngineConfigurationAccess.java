package org.rockhill.adoration.initialize;

import org.rockhill.adoration.configuration.ConfigurationAccessBase;
import org.rockhill.adoration.configuration.PropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Configures the module with the necessary properties.
 */
@Component
public class EngineConfigurationAccess implements ConfigurationAccessBase {

    private PropertyDTO properties;

    @Autowired
    private PropertyHolder propertyHolder;

    /**
     * Returns a {@link PropertyDTO} holding all module specific properties.
     *
     * @return the propertiesDTO object
     */
    public PropertyDTO getProperties() {
        return properties;
    }

    @Override
    public void loadProperties() {
        Integer port = propertyHolder.getInt("webapp.port");
        properties = new PropertyDTO(port);
    }
}
