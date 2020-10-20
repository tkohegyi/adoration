package org.rockhill.adoration.web.service.helper;

public class Oauth2ServiceBase {
    protected static final String AUDIT_SOCIAL_UPDATE = "Social:Update:";

    protected String makeEmptyStringFromNull(String inputString) {
        String notNullString = inputString;
        if (inputString == null) {
            notNullString = "";
        }
        return notNullString;
    }

}
