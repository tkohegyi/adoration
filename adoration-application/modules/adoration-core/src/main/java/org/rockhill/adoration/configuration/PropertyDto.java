package org.rockhill.adoration.configuration;

/**
 * Holds module specific properties.
 */
public class PropertyDto {

    private final String smtp_server;
    private final String smtp_port;
    private final String smtp_userName;
    private final String smtp_password;
    private final String email_from;
    private final String email_to;

    /**
     * Constructs a new property holding object with the given fields.
     *
     */
    public PropertyDto(final String smtp_server, final String smtp_port, final String smtp_userName,
                       final String smtp_password, final String email_from, final String email_to) {
        super();
        this.smtp_server = smtp_server;
        this.smtp_port = smtp_port;
        this.smtp_userName = smtp_userName;
        this.smtp_password = smtp_password;
        this.email_from = email_from;
        this.email_to = email_to;
    }

    public String getSmtpServer() {
        return smtp_server;
    }

    public String getSmtpPort() {
        return smtp_port;
    }

    public String getSmtpUserName() {
        return smtp_userName;
    }

    public String getSmtpPassword() {
        return smtp_password;
    }

    public String getEmailFrom() {
        return email_from;
    }

    public String getEmailTo() {
        return email_to;
    }

}
