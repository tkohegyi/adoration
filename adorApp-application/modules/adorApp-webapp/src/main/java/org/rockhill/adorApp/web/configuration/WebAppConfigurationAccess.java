package org.rockhill.adorApp.web.configuration;

import org.rockhill.adorApp.configuration.ConfigurationAccessBase;
import org.rockhill.adorApp.configuration.PropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Configures the module with the necessary properties.
 */
@Component
public class WebAppConfigurationAccess implements ConfigurationAccessBase {
    private final Logger logger = LoggerFactory.getLogger(WebAppConfigurationAccess.class);

    private PropertyDto properties;

    @Autowired
    private PropertyHolder propertyHolder;

    /**
     * Returns a {@link PropertyDto} holding all module specific properties.
     *
     * @return the propertiesDTO object
     */
    public PropertyDto getProperties() {
        if (properties == null) {
            //it is unknown why we have 2 WebAppConfigurationAccess class in this app, and only one is initialized properly
            //so it worth to check and reload if necessary
            logger.warn("Had to reload WebAppConfigurationAccess properties...");
            loadProperties();
        }
        return properties;
    }

    @Override
    public void loadProperties() {
        String google_client_id = propertyHolder.get("google_client_id");
        String google_client_secret = propertyHolder.get("google_client_secret");
        String google_redirect_url = propertyHolder.get("google_redirect_url");
        String google_developer_key = propertyHolder.get("google_developer_key");
        String base_url = propertyHolder.get("base_url");
        properties = new PropertyDto(google_client_id, google_client_secret, google_redirect_url, google_developer_key, base_url);
    }
}