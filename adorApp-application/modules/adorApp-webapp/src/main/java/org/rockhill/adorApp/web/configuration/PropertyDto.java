package org.rockhill.adorApp.web.configuration;

/**
 * Holds module specific properties.
 */
public class PropertyDto {

    private final String google_client_id;
    private final String google_client_secret;
    private final String google_redirect_url;
    private final String base_url;
    private final String facebook_app_id;
    private final String facebook_app_secret;
    private final Integer sessionTimeout;
    private final String excel_file_name;
    private final String daily_info_file_name;
    private final String hourly_info_file_name;
    private final String adorator_info_file_name;
    private final String captcha_site_secret;

    /**
     * Constructs a new property holding object with the given fields.
     *
     */
    public PropertyDto(final String google_client_id, final String google_client_secret, final String google_redirect_url,
                       final String base_url, final String facebook_app_id, final String facebook_app_secret,
                       final Integer sessionTimeout, final String excel_file_name, final String daily_info_file_name,
                       final String hourly_info_file_name, final String adorator_info_file_name,
                       final String captcha_site_secret) {
        super();
        this.google_client_id = google_client_id;
        this.google_client_secret = google_client_secret;
        this.google_redirect_url = google_redirect_url;
        this.base_url = base_url;
        this.facebook_app_id = facebook_app_id;
        this.facebook_app_secret = facebook_app_secret;
        this.sessionTimeout = sessionTimeout;
        this.excel_file_name = excel_file_name;
        this.daily_info_file_name = daily_info_file_name;
        this.hourly_info_file_name = hourly_info_file_name;
        this.adorator_info_file_name = adorator_info_file_name;
        this.captcha_site_secret = captcha_site_secret;
    }

    public String getGoogleClientId() {
        return google_client_id;
    }
    public String getGoogleClientSecret() {
        return google_client_secret;
    }
    public String getGoogleRedirectUrl() {
        return google_redirect_url;
    }

    public String getBaseUrl() {
        return base_url;
    }

    public String getFacebook_app_id() {
        return facebook_app_id;
    }

    public String getFacebook_app_secret() {
        return facebook_app_secret;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public String getExcelFileName() { return excel_file_name; }

    public String getDailyInfoFileName() {
        return daily_info_file_name;
    }

    public String getHourlyInfoFileName() {
        return hourly_info_file_name;
    }

    public String getAdoratorInfoFileName() {
        return adorator_info_file_name;
    }

    public String getCaptchaSiteSecret() { return captcha_site_secret; }
}
