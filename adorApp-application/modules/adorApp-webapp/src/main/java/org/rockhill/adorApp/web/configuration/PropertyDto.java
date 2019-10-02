package org.rockhill.adorApp.web.configuration;

/**
 * Holds module specific properties.
 */
public class PropertyDto {

    private final String google_client_id;
    private final String google_client_secret;
    private final String google_redirect_url;
    private final String google_developer_key;
    private final String base_url;

    /**
     * Constructs a new property holding object with the given fields.
     *
     */
    public PropertyDto(final String google_client_id, final String google_client_secret, final String google_redirect_url,
                       final String google_developer_key, final String base_url) {
        super();
        this.google_client_id = google_client_id;
        this.google_client_secret = google_client_secret;
        this.google_redirect_url = google_redirect_url;
        this.google_developer_key = google_developer_key;
        this.base_url = base_url;
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
    public String getGoogleDeveloperKey() {
        return google_developer_key;
    }

    public String getBaseUrl() {
        return base_url;
    }
}
