package org.rockhill.adoration.web.configuration;

/**
 * Holds module specific properties.
 */
public class PropertyDto {

    private final String googleClientId;
    private final String googleClientSecret;
    private final String googleRedirectUrl;
    private final String baseUrl;
    private final String facebookAppId;
    private final String facebookAppSecret;
    private final Integer sessionTimeout;
    private final String excelFileName;
    private final String dailyInfoFileName;
    private final String hourlyInfoFileName;
    private final String adoratorInfoFileName;
    private final String captchaSiteSecret;

    /**
     * Constructs a new property holding object with the given fields.
     *
     */
    public PropertyDto(final String googleClientId, final String googleClientSecret, final String googleRedirectUrl,
                       final String baseUrl, final String facebookAppId, final String facebookAppSecret,
                       final Integer sessionTimeout, final String excelFileName, final String dailyInfoFileName,
                       final String hourlyInfoFileName, final String adoratorInfoFileName,
                       final String captchaSiteSecret) { //NOSONAR
        super();
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
        this.googleRedirectUrl = googleRedirectUrl;
        this.baseUrl = baseUrl;
        this.facebookAppId = facebookAppId;
        this.facebookAppSecret = facebookAppSecret;
        this.sessionTimeout = sessionTimeout;
        this.excelFileName = excelFileName;
        this.dailyInfoFileName = dailyInfoFileName;
        this.hourlyInfoFileName = hourlyInfoFileName;
        this.adoratorInfoFileName = adoratorInfoFileName;
        this.captchaSiteSecret = captchaSiteSecret;
    }

    public String getGoogleClientId() {
        return googleClientId;
    }
    public String getGoogleClientSecret() {
        return googleClientSecret;
    }
    public String getGoogleRedirectUrl() {
        return googleRedirectUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getFacebookAppId() {
        return facebookAppId;
    }

    public String getFacebookAppSecret() {
        return facebookAppSecret;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public String getExcelFileName() { return excelFileName; }

    public String getDailyInfoFileName() {
        return dailyInfoFileName;
    }

    public String getHourlyInfoFileName() {
        return hourlyInfoFileName;
    }

    public String getAdoratorInfoFileName() {
        return adoratorInfoFileName;
    }

    public String getCaptchaSiteSecret() { return captchaSiteSecret; }
}
