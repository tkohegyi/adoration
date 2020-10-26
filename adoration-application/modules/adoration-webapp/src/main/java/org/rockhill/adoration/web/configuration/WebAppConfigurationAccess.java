package org.rockhill.adoration.web.configuration;

import org.apache.commons.lang3.math.NumberUtils;
import org.rockhill.adoration.configuration.ConfigurationAccessBase;
import org.rockhill.adoration.configuration.PropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Configures the module with the necessary properties.
 */
@Component
public class WebAppConfigurationAccess implements ConfigurationAccessBase {
    private static final int SESSION_TIMEOUT_DEFAULT_VALUE = 500;

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
        String googleClientId = propertyHolder.get("google_client_id");
        String googleClientSecret = propertyHolder.get("google_client_secret");
        String googleRedirectUrl = propertyHolder.get("google_redirect_url");
        String baseUrl = propertyHolder.get("base_url");
        String facebookAppId = propertyHolder.get("facebook_app_id");
        String facebookAppSecret = propertyHolder.get("facebook_app_secret");
        Integer sessionTimeout = NumberUtils.toInt(propertyHolder.get("sessionTimeout"), SESSION_TIMEOUT_DEFAULT_VALUE);
        String excelFileName = propertyHolder.get("excel_file_name");
        String dailyInfoFileName = propertyHolder.get("daily_info_file_name");
        String hourlyInfoFileName = propertyHolder.get("hourly_info_file_name");
        String adoratorInfoFileName = propertyHolder.get("adorator_info_file_name");
        String captchaSiteSecret = propertyHolder.get("captcha_site_secret");
        properties = new PropertyDTO(googleClientId, googleClientSecret, googleRedirectUrl, baseUrl,
                facebookAppId, facebookAppSecret, sessionTimeout,
                excelFileName, dailyInfoFileName, hourlyInfoFileName, adoratorInfoFileName, captchaSiteSecret);
    }
}
