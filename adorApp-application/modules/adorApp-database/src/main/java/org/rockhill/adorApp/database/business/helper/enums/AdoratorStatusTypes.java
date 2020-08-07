package org.rockhill.adorApp.database.business.helper.enums;

import org.rockhill.adorApp.database.exception.DatabaseHandlingException;

import java.util.HashSet;
import java.util.Set;

public enum AdoratorStatusTypes {
    USER("External User/Adorator", 0),
    PRE_ADORATOR("Pre-Adorator", 1),
    ADORATOR("Adorator", 2),
    POST_ADORATOR("Post-Adorator", 3),
    DIED_ADORATOR("Died", 4),
    REGISTERED_BY_MISTAKE("Registered by mistake", 5),
    ADORATOR_EMPHASIZED("Emphasized Adorator",6), //for Margit and all coordinators
    ADORATOR_ADMIN("Administrator", 7);

    private final String adoratorStatusText;
    private final Integer adoratorStatusValue;
    private static final Set<AdoratorStatusTypes> inactives;

    AdoratorStatusTypes(String adoratorStatusText, Integer adoratorStatusValue) {
        this.adoratorStatusText = adoratorStatusText;
        this.adoratorStatusValue = adoratorStatusValue;
    }

    static {
        inactives = new HashSet<>();
        inactives.add(AdoratorStatusTypes.REGISTERED_BY_MISTAKE);
        inactives.add(AdoratorStatusTypes.PRE_ADORATOR);
        inactives.add(AdoratorStatusTypes.POST_ADORATOR);
        inactives.add(AdoratorStatusTypes.DIED_ADORATOR);
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
        if (ADORATOR_ADMIN.adoratorStatusValue.equals(id)) return ADORATOR_ADMIN;
        if (POST_ADORATOR.adoratorStatusValue.equals(id)) return POST_ADORATOR;
        if (DIED_ADORATOR.adoratorStatusValue.equals(id)) return DIED_ADORATOR;
        if (REGISTERED_BY_MISTAKE.adoratorStatusValue.equals(id)) return REGISTERED_BY_MISTAKE;
        if (ADORATOR_EMPHASIZED.adoratorStatusValue.equals(id)) return ADORATOR_EMPHASIZED;
        throw new DatabaseHandlingException("Invalid AdoratorStatusTypes requested: " + id);
    }

    public static Boolean isInactive(Integer typeId) {
        AdoratorStatusTypes adoratorStatusTypes = getTypeFromId(typeId);
        return inactives.contains(adoratorStatusTypes);
    }
}
