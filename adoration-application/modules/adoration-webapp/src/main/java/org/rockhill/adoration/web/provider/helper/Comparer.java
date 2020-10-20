package org.rockhill.adoration.web.provider.helper;

public class Comparer {

    protected boolean isLongChanged(final Long oldLongValue, final Long newLongValue) {
        boolean changed = false;
        if (!((oldLongValue == null) && (newLongValue == null))) { //if both null, it was not changed
            if (((oldLongValue == null) && (newLongValue != null)) //at least one of them is not null
                    || ((oldLongValue != null) && (newLongValue == null))
                    || (!oldLongValue.equals(newLongValue))) { //here both of them is not null
                changed = true;
            }
        }
        return changed;
    }

}
