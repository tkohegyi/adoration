package org.rockhill.adoration.web.configuration;

import org.apache.commons.lang3.math.NumberUtils;
import org.rockhill.adoration.configuration.ConfigurationAccessBase;
import org.rockhill.adoration.configuration.PropertyHolder;
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
    private final int SESSION_TIMEOUT_DEFAULT_VALUE = 500;

    private PropertyDto properties;

    @Autowired
    private PropertyHolder propertyHolder;

    /**
     * Returns a {@link PropertyDto} holding all module specific properties.
     *
     * @return the propertiesDTO object
     */
    public PropertyDto getProperties() {
        return properties;
    }

    @Override
    public void loadProperties() {
        String google_client_id = propertyHolder.get("google_client_id");
        String google_client_secret = propertyHolder.get("google_client_secret");
        String google_redirect_url = propertyHolder.get("google_redirect_url");
        String base_url = propertyHolder.get("base_url");
        String facebook_app_id = propertyHolder.get("facebook_app_id");
        String facebook_app_secret = propertyHolder.get("facebook_app_secret");
        Integer sessionTimeout = NumberUtils.toInt(propertyHolder.get("sessionTimeout"), SESSION_TIMEOUT_DEFAULT_VALUE);
        String excel_file_name = propertyHolder.get("excel_file_name");
        String daily_info_file_name = propertyHolder.get("daily_info_file_name");
        String hourly_info_file_name = propertyHolder.get("hourly_info_file_name");
        String adorator_info_file_name = propertyHolder.get("adorator_info_file_name");
        String captcha_site_secret = propertyHolder.get("captcha_site_secret");
        properties = new PropertyDto(google_client_id, google_client_secret, google_redirect_url, base_url,
                facebook_app_id, facebook_app_secret, sessionTimeout,
                excel_file_name, daily_info_file_name, hourly_info_file_name, adorator_info_file_name, captcha_site_secret);
    }
}
