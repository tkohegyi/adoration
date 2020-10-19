package org.rockhill.adoration.database.business.helper.enums;

import org.rockhill.adoration.database.exception.DatabaseHandlingException;

public enum TranslatorDayNames {
    SUNDAY ("Sunday",0),
    MONDAY("Monday", 1),
    TUESDAY("Tuesday", 2),
    WEDNESDAY("Wednesday", 3),
    THURSDAY("Thursday",4),
    FRIDAY("Friday",5),
    SATURDAY("Saturday", 6);

    public String getDayText() {
        return dayText;
    }

    public Integer getDayValue() {
        return dayValue;
    }

    private final String dayText;
    private final Integer dayValue;

    TranslatorDayNames(String dayText, Integer dayValue) {
        this.dayText = dayText;
        this.dayValue = dayValue;
    }

    public static String getTranslatedString(Integer value) {
        for (TranslatorDayNames translatorDayNames : TranslatorDayNames.values()) {
            if (translatorDayNames.dayValue.equals(value)) {
                return translatorDayNames.dayText;
            }
        }
        throw new DatabaseHandlingException("Incorrect usage of data -> TranslatorDayNames number:" + value.toString() + " was requested.");
    }


}
