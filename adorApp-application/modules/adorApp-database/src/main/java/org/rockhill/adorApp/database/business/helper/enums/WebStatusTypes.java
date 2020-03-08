package org.rockhill.adorApp.database.business.helper.enums;

import org.rockhill.adorApp.database.exception.DatabaseHandlingException;

public enum WebStatusTypes {
    NO_ID("Anonymous", 0), //who don't want to show his/her name
    WAIT_FOR_AUTHORIZATION("Waiting for Identification/Authorization", 1),
    GOOGLE_OK("Google User", 2),
    FACEBOOK_OK("Facebook User", 3),
    GOOGLE_AND_FACEBOOK_USER("Google/Facebook User", 4),
    NO_SOCIAL("User without Social login", 5);

    private String translatedText;
    private Integer typeValue;

    WebStatusTypes(String translatedText, Integer typeValue) {
        this.translatedText = translatedText;
        this.typeValue = typeValue;
    }

    public Integer getTypeValue() {
        return typeValue;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    // helper functions
    public static String getTranslatedString(Integer typeValue) {
        String webStatusType = null;
        if (NO_ID.getTypeValue().equals(typeValue)) { webStatusType = NO_ID.getTranslatedText(); }
        if (WAIT_FOR_AUTHORIZATION.getTypeValue().equals(typeValue)) { webStatusType = WAIT_FOR_AUTHORIZATION.getTranslatedText(); }
        if (GOOGLE_OK.getTypeValue().equals(typeValue)) { webStatusType = GOOGLE_OK.getTranslatedText(); }
        if (FACEBOOK_OK.getTypeValue().equals(typeValue)) { webStatusType = FACEBOOK_OK.getTranslatedText(); }
        if (GOOGLE_AND_FACEBOOK_USER.getTypeValue().equals(typeValue)) { webStatusType = GOOGLE_AND_FACEBOOK_USER.getTranslatedText(); }
        if (NO_SOCIAL.getTypeValue().equals(typeValue)) { webStatusType = NO_SOCIAL.getTranslatedText(); }
        if (webStatusType == null) {
            throw new DatabaseHandlingException("Incorrect usage of data -> WebStatusTypes type:" + typeValue.toString() + " was requested.");
        }
        return webStatusType;
    }

    public static WebStatusTypes getTypeFromId(Integer id) {
        if (NO_ID.getTypeValue().equals(id)) return NO_ID;
        if (WAIT_FOR_AUTHORIZATION.getTypeValue().equals(id)) return WAIT_FOR_AUTHORIZATION;
        if (GOOGLE_OK.getTypeValue().equals(id)) return GOOGLE_OK;
        if (FACEBOOK_OK.getTypeValue().equals(id)) return FACEBOOK_OK;
        if (GOOGLE_AND_FACEBOOK_USER.getTypeValue().equals(id)) return GOOGLE_AND_FACEBOOK_USER;
        if (NO_SOCIAL.getTypeValue().equals(id)) return NO_SOCIAL;
        throw new DatabaseHandlingException("Incorrect usage of data -> WebStatusTypes id:" + id.toString() + " was requested.");
    }

}
