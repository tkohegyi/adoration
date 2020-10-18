package org.rockhill.adoration.database.business.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Converter {
    private final static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private final static String DATE_PATTERN = "yyyy-MM-dd";

    public <T> Set<T> convertArrayToSet(T array[])
    {

        // Create an empty Set
        Set<T> set = new HashSet<>();

        // Iterate through the array
        for (T t : array) {
            // Add each element into the set
            set.add(t);
        }

        // Return the converted Set
        return set;
    }

    public String getCurrentDateTimeAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    public String getCurrentDateAsString() {
        return getDateAsString(new Date());
    }

    public String getDateAsString(final Date date) {
        String dateStr = "";
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
            dateStr = simpleDateFormat.format(date);
        }
        return dateStr;
    }

}
