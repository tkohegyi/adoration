package org.rockhill.adoration.database.business.helper.enums;

import org.rockhill.adoration.database.exception.DatabaseHandlingException;

public enum SocialStatusTypes {
    WAIT_FOR_IDENTIFICATION("Waiting for Identification", 1),
    IDENTIFIED_USER("Identified User", 2),
    SOCIAL_USER("Social-only User", 3);

    private final String translatedText;
    private final Integer typeValue;

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
        for (SocialStatusTypes socialStatusTypes : SocialStatusTypes.values()) {
            if (socialStatusTypes.typeValue.equals(typeValue)) {
                return socialStatusTypes.getTranslatedText();
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> SocialStatusTypes type:" + typeValue.toString() + " was requested.");
    }

    public static SocialStatusTypes getTypeFromId(Integer id) {
        for (SocialStatusTypes socialStatusTypes : SocialStatusTypes.values()) {
            if (socialStatusTypes.typeValue.equals(id)) {
                return socialStatusTypes;
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> SocialStatusTypes id:" + id.toString() + " was requested.");
    }

}
