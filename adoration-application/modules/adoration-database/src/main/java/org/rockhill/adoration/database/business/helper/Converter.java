package org.rockhill.adoration.database.business.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Supporter class to convert special values to other special type of values.
 */
public class Converter {
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    public String getCurrentDateTimeAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        String format;
        format = simpleDateFormat.format(new Date());
        return format;
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
