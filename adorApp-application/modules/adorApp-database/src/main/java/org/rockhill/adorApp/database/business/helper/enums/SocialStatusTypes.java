package org.rockhill.adorApp.database.business.helper.enums;

import org.rockhill.adorApp.database.exception.DatabaseHandlingException;

public enum SocialStatusTypes {
    WAIT_FOR_IDENTIFICATION("Waiting for Identification", 1),
    IDENTIFIED_USER("Identified User", 2),
    SOCIAL_USER("Social-only User", 3);

    private String translatedText;
    private Integer typeValue;

    SocialStatusTypes(String translatedText, Integer typeValue) {
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
        if (WAIT_FOR_IDENTIFICATION.getTypeValue().equals(typeValue)) { webStatusType = WAIT_FOR_IDENTIFICATION.getTranslatedText(); }
        if (IDENTIFIED_USER.getTypeValue().equals(typeValue)) { webStatusType = IDENTIFIED_USER.getTranslatedText(); }
        if (SOCIAL_USER.getTypeValue().equals(typeValue)) { webStatusType = SOCIAL_USER.getTranslatedText(); }
        if (webStatusType == null) {
            throw new DatabaseHandlingException("Incorrect usage of data -> SocialStatusTypes type:" + typeValue.toString() + " was requested.");
        }
        return webStatusType;
    }

    public static SocialStatusTypes getTypeFromId(Integer id) {
        if (WAIT_FOR_IDENTIFICATION.getTypeValue().equals(id)) return WAIT_FOR_IDENTIFICATION;
        if (IDENTIFIED_USER.getTypeValue().equals(id)) return IDENTIFIED_USER;
        if (SOCIAL_USER.getTypeValue().equals(id)) return SOCIAL_USER;
        throw new DatabaseHandlingException("Incorrect usage of data -> SocialStatusTypes id:" + id.toString() + " was requested.");
    }

}
