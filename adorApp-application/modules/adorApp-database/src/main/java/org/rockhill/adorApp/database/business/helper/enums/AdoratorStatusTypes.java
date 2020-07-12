package org.rockhill.adorApp.database.business.helper.enums;

import org.rockhill.adorApp.database.exception.DatabaseHandlingException;

public enum AdoratorStatusTypes {
    USER("External User/Adorator", 0),
    PRE_ADORATOR("Pre-Adorator", 1),
    ADORATOR("Adorator", 2),
    ADORATOR_HOURLY_COORDINATOR("Hourly Coordinator", 3),
    ADORATOR_DAILY_COORDINATOR("Daily Coordinator", 4),
    ADORATOR_MAIN_COORDINATOR("Main Coordinator", 5),
    ADORATOR_SPIRITUAL_COORDINATOR("Spiritual Coordinator", 6),
    ADORATOR_ADMIN("Admin Coordinator", 7),
    POST_ADORATOR("Post-Adorator", 8),
    DIED_ADORATOR("Died", 9),
    REGISTERED_BY_MISTAKE("Registered by mistake", 10),
    ADORATOR_EMPHASIZED("Emphasized Adorator",11); //for Margit

    private String adoratorStatusText;
    private Integer adoratorStatusValue;

    AdoratorStatusTypes(String adoratorStatusText, Integer adoratorStatusValue) {
        this.adoratorStatusText = adoratorStatusText;
        this.adoratorStatusValue = adoratorStatusValue;
    }

    public Integer getAdoratorStatusValue() {
        return adoratorStatusValue;
    }

    public String getAdoratorStatusText() {
        return adoratorStatusText;
    }

    // helper functions
    public static String getTranslatedString(Integer adoratorStatusValue) {
        String adoratorStatusText = null;
        if (USER.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = USER.getAdoratorStatusText(); }
        if (PRE_ADORATOR.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = PRE_ADORATOR.getAdoratorStatusText(); }
        if (ADORATOR.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = ADORATOR.getAdoratorStatusText(); }
        if (ADORATOR_HOURLY_COORDINATOR.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = ADORATOR_HOURLY_COORDINATOR.getAdoratorStatusText(); }
        if (ADORATOR_DAILY_COORDINATOR.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = ADORATOR_DAILY_COORDINATOR.getAdoratorStatusText(); }
        if (ADORATOR_MAIN_COORDINATOR.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = ADORATOR_MAIN_COORDINATOR.getAdoratorStatusText(); }
        if (ADORATOR_SPIRITUAL_COORDINATOR.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = ADORATOR_SPIRITUAL_COORDINATOR.getAdoratorStatusText(); }
        if (ADORATOR_ADMIN.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = ADORATOR_ADMIN.getAdoratorStatusText(); }
        if (POST_ADORATOR.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = POST_ADORATOR.getAdoratorStatusText(); }
        if (DIED_ADORATOR.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = DIED_ADORATOR.getAdoratorStatusText(); }
        if (REGISTERED_BY_MISTAKE.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = REGISTERED_BY_MISTAKE.getAdoratorStatusText(); }
        if (ADORATOR_EMPHASIZED.getAdoratorStatusValue().equals(adoratorStatusValue)) { adoratorStatusText = ADORATOR_EMPHASIZED.getAdoratorStatusText(); }
        if (adoratorStatusText == null) {
            throw new DatabaseHandlingException("Incorrect usage of data -> AdoratorStatusTypes number:" + adoratorStatusValue.toString() + " was requested.");
        }
        return adoratorStatusText;
    }

    public static AdoratorStatusTypes getTypeFromId(Integer id) {
        if (USER.adoratorStatusValue.equals(id)) return USER;
        if (PRE_ADORATOR.adoratorStatusValue.equals(id)) return PRE_ADORATOR;
        if (ADORATOR.adoratorStatusValue.equals(id)) return ADORATOR;
        if (ADORATOR_HOURLY_COORDINATOR.adoratorStatusValue.equals(id)) return ADORATOR_HOURLY_COORDINATOR;
        if (ADORATOR_DAILY_COORDINATOR.adoratorStatusValue.equals(id)) return ADORATOR_DAILY_COORDINATOR;
        if (ADORATOR_MAIN_COORDINATOR.adoratorStatusValue.equals(id)) return ADORATOR_MAIN_COORDINATOR;
        if (ADORATOR_SPIRITUAL_COORDINATOR.adoratorStatusValue.equals(id)) return ADORATOR_SPIRITUAL_COORDINATOR;
        if (ADORATOR_ADMIN.adoratorStatusValue.equals(id)) return ADORATOR_ADMIN;
        if (POST_ADORATOR.adoratorStatusValue.equals(id)) return POST_ADORATOR;
        if (DIED_ADORATOR.adoratorStatusValue.equals(id)) return DIED_ADORATOR;
        if (REGISTERED_BY_MISTAKE.adoratorStatusValue.equals(id)) return REGISTERED_BY_MISTAKE;
        if (ADORATOR_EMPHASIZED.adoratorStatusValue.equals(id)) return ADORATOR_EMPHASIZED;
        throw new DatabaseHandlingException("Invalid AdoratorStatusTypes requested: " + id);
    }

}
