package org.rockhill.adoration.database.business.helper.enums;

import org.rockhill.adoration.database.exception.DatabaseHandlingException;

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
        for (AdoratorStatusTypes adoratorStatusTypes : AdoratorStatusTypes.values()) {
            if (adoratorStatusTypes.getAdoratorStatusValue().equals(adoratorStatusValue)) {
                return adoratorStatusTypes.getAdoratorStatusText();
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> AdoratorStatusTypes number:" + adoratorStatusValue.toString() + " was requested.");
    }

    public static AdoratorStatusTypes getTypeFromId(Integer id) {
        for (AdoratorStatusTypes adoratorStatusTypes : AdoratorStatusTypes.values()) {
            if (adoratorStatusTypes.adoratorStatusValue.equals(id)) {
                return adoratorStatusTypes;
            }
        }
        throw new DatabaseHandlingException("Invalid AdoratorStatusTypes requested: " + id);
    }

    public static Boolean isInactive(Integer typeId) {
        AdoratorStatusTypes adoratorStatusTypes = getTypeFromId(typeId);
        return inactives.contains(adoratorStatusTypes);
    }
}
