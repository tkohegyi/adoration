package org.rockhill.adoration.database.business.helper.enums;

import org.rockhill.adoration.database.exception.DatabaseHandlingException;

public enum AdorationMethodTypes {
    PHYSICAL("Physical", 0),
    ONLINE("Online", 1);

    private final String adorationMethodText;
    private final Integer adorationMethodValue;

    AdorationMethodTypes(String adorationMethodText, Integer adorationMethodValue) {
        this.adorationMethodText = adorationMethodText;
        this.adorationMethodValue = adorationMethodValue;
    }

    // helper functions
    public static String getTranslatedString(Integer adorationMethodValue) {
        for (AdorationMethodTypes adorationMethodTypes : AdorationMethodTypes.values()) {
            if (adorationMethodTypes.getAdorationMethodValue().equals(adorationMethodValue)) {
                return adorationMethodTypes.getAdorationMethodText();
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> AdorationMethodType number:" + adorationMethodValue.toString() + " was requested.");
    }

    public static AdorationMethodTypes getTypeFromId(Integer id) {
        for (AdorationMethodTypes adorationMethodTypes : AdorationMethodTypes.values()) {
            if (adorationMethodTypes.getAdorationMethodValue().equals(id)) {
                return adorationMethodTypes;
            }
        }
        throw new DatabaseHandlingException("Invalid AdorationMethodType requested: " + id);
    }

    public String getAdorationMethodText() {
        return adorationMethodText;
    }

    public Integer getAdorationMethodValue() {
        return adorationMethodValue;
    }
}
