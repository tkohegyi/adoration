package org.rockhill.adoration.database.business.helper.enums;

import org.rockhill.adoration.database.exception.DatabaseHandlingException;

public enum AdorationMethodTypes {
    PHYSICAL("Physical", 0),
    ONLINE("Online", 1);

    private String adorationMethodText;
    private Integer adorationMethodValue;

    AdorationMethodTypes(String adorationMethodText, Integer adorationMethodValue) {
        this.adorationMethodText = adorationMethodText;
        this.adorationMethodValue = adorationMethodValue;
    }

    // helper functions
    public static String getTranslatedString(Integer adorationMethodValue) {
        String adorationMethodText = null;
        if (PHYSICAL.getAdorationMethodValue().equals(adorationMethodValue)) { adorationMethodText = PHYSICAL.getAdorationMethodText(); }
        if (ONLINE.getAdorationMethodValue().equals(adorationMethodValue)) { adorationMethodText = ONLINE.getAdorationMethodText(); }
        if (adorationMethodText == null) {
            throw new DatabaseHandlingException("Incorrect usage of data -> AdorationMethodType number:" + adorationMethodValue.toString() + " was requested.");
        }
        return adorationMethodText;
    }

    public static AdorationMethodTypes getTypeFromId(Integer id) {
        if (PHYSICAL.getAdorationMethodValue().equals(id)) return PHYSICAL;
        if (ONLINE.getAdorationMethodValue().equals(id)) return ONLINE;
        throw new DatabaseHandlingException("Invalid AdorationMethodType requested: " + id);
    }

    public String getAdorationMethodText() {
        return adorationMethodText;
    }

    public Integer getAdorationMethodValue() {
        return adorationMethodValue;
    }
}