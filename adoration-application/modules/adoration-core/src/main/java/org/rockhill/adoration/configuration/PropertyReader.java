package org.rockhill.adoration.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Set;

/**
 * Reads configuration parameters from a {@link Properties} object and saves them in a key-value store.
 */
@Component
public class PropertyReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReader.class);

    @Autowired
    private PropertyHolder propertyHolder;

    /**
     * Reads configuration parameters from a {@link Properties} object and
     * saves them in a key-value store.
     *
     * @param properties a {@link Properties} object that will be processed
     *                   and the each property will be injected a key-value store
     */
    public void setProperties(final Properties properties) {
        Set<Object> keySet = properties.keySet();
        for (Object key : keySet) {
            String value = properties.getProperty(key.toString());
            propertyHolder.addProperty(key.toString(), value);
        }
        LOGGER.debug("LOADED PROPERTY: " + propertyHolder.get("webapp.port"));
    }

}
