package org.rockhill.adoration.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Configures the module with the necessary properties.
 */
@Component
public class EmailConfigurationAccess implements ConfigurationAccessBase {
    private final Logger logger = LoggerFactory.getLogger(EmailConfigurationAccess.class);

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
            logger.warn("Had to reload EmailConfigurationAccess properties...");
            loadProperties();
        }
        return properties;
    }

    @Override
    public void loadProperties() {
        String smtp_server = propertyHolder.get("smtp.server");
        String smtp_port = propertyHolder.get("smtp.port");
        String smtp_userName = propertyHolder.get("smtp.userName");
        String smtp_password = propertyHolder.get("smtp.password");
        String email_from = propertyHolder.get("email.from");
        String email_to = propertyHolder.get("email.to");
        properties = new PropertyDto(smtp_server, smtp_port, smtp_userName, smtp_password, email_from, email_to);
    }
}
