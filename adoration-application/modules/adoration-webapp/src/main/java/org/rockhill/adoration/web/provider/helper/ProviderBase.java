package org.rockhill.adoration.web.provider.helper;

import org.rockhill.adoration.database.business.BusinessWithAuditTrail;
import org.rockhill.adoration.database.tables.AuditTrail;

import java.util.List;

public class ProviderBase {

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

    protected String prepareAuditValueString(Long value) {
        String stringValue;
        if (value != null) {
            stringValue = value.toString();
        } else {
            stringValue = "N/A";
        }
        return stringValue;
    }

    /**
     * Gets the list of audit events associated to a specific object Id.
     *
     * @param businessWithAuditTrail is the business class to work with audit record
     * @param id identifies the record we are interested about its history
     * @return with the list of audit trail that belong to the specified item, as object
     */
    protected Object getEntityHistoryAsObject(BusinessWithAuditTrail businessWithAuditTrail, Long id) {
        List<AuditTrail> auditTrailOfObject;
        auditTrailOfObject = businessWithAuditTrail.getAuditTrailOfObject(id);
        return auditTrailOfObject;
    }

}
