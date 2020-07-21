package org.rockhill.adorApp.database.business.helper.enums;

import org.rockhill.adorApp.database.exception.DatabaseHandlingException;

public enum AdorationMethodTypes {
    PHYSICAL("Physical", 0),
    ONLINE("Online", 1),
    HOURLY_COORDINATOR("Hourly Coordinator", 2),
    DAILY_COORDINATOR("Daily Coordinator", 3);

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
        if (HOURLY_COORDINATOR.getAdorationMethodValue().equals(adorationMethodValue)) { adorationMethodText = HOURLY_COORDINATOR.getAdorationMethodText(); }
        if (DAILY_COORDINATOR.getAdorationMethodValue().equals(adorationMethodValue)) { adorationMethodText = DAILY_COORDINATOR.getAdorationMethodText(); }
        if (adorationMethodText == null) {
            throw new DatabaseHandlingException("Incorrect usage of data -> AdorationMethodType number:" + adorationMethodValue.toString() + " was requested.");
        }
        return adorationMethodText;
    }

    public static AdorationMethodTypes getTypeFromId(Integer id) {
        if (PHYSICAL.getAdorationMethodValue().equals(id)) return PHYSICAL;
        if (ONLINE.getAdorationMethodValue().equals(id)) return ONLINE;
        if (HOURLY_COORDINATOR.getAdorationMethodValue().equals(id)) return HOURLY_COORDINATOR;
        if (DAILY_COORDINATOR.getAdorationMethodValue().equals(id)) return DAILY_COORDINATOR;
        throw new DatabaseHandlingException("Invalid AdorationMethodType requested: " + id);
    }

    public String getAdorationMethodText() {
        return adorationMethodText;
    }

    public Integer getAdorationMethodValue() {
        return adorationMethodValue;
    }
}
