package org.rockhill.adoration.database.business.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Supporter class to convert special date values to String values.
 */
public class DateTimeConverter {
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Gets actual date & time as standard String.
     * @return with the string format of current date & time
     */
    public String getCurrentDateTimeAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        String format;
        format = simpleDateFormat.format(new Date());
        return format;
    }

    /**
     * Gets the actual date as standard String.
     * @return with the string format of the current date.
     */
    public String getCurrentDateAsString() {
        return getDateAsString(new Date());
    }

    /**
     * Gets the standard String format of a given date & time.
     * @param date is the give date & time
     * @return with its string format
     */
    public String getDateAsString(final Date date) {
        String dateStr = "";
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
            dateStr = simpleDateFormat.format(date);
        }
        return dateStr;
    }

}
