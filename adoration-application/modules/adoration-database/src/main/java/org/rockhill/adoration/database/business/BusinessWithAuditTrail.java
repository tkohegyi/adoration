package org.rockhill.adoration.database.business;

import com.sun.istack.NotNull; //NOSONAR
import com.sun.istack.Nullable; //NOSONAR
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.rockhill.adoration.database.SessionFactoryHelper;
import org.rockhill.adoration.database.business.helper.BusinessBase;
import org.rockhill.adoration.database.business.helper.DateTimeConverter;
import org.rockhill.adoration.database.exception.DatabaseHandlingException;
import org.rockhill.adoration.database.tables.AuditTrail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Business class to handle AuditTrail Database table.
 */
@Component
public class BusinessWithAuditTrail extends BusinessBase {

    private final Logger logger = LoggerFactory.getLogger(BusinessWithAuditTrail.class);

    @Autowired
    private BusinessWithNextGeneralKey businessWithNextGeneralKey;

    /**
     * Get the list of audit records that has connection to the given reference id.
     *
     * @param id is the given reference id
     * @return with the list of related audit records
     */
    public List<AuditTrail> getAuditTrailOfObject(@NotNull Long id) {
        if (id == null) {
            throw new DatabaseHandlingException("Tried to get audit trail for a null id, pls contact to maintainers");
        }
        List<AuditTrail> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from AuditTrail as AT where AT.refId = :" + EXPECTED_PARAMETER + " OR AT.activityType like :likeValue order by AT.atWhen ASC";
        Query<AuditTrail> query = session.createQuery(hql, AuditTrail.class);
        query.setParameter(EXPECTED_PARAMETER, id);
        query.setParameter("likeValue", "'%:" + id.toString() + "'");
        result = query.list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    /**
     * Prepares a new audit record by using the specified fields.
     *
     * @param refId       record info
     * @param userName    record info
     * @param type        record info
     * @param description record info
     * @param data        record info
     * @return with the audit record prepared for save
     */
    public AuditTrail prepareAuditTrail(@NotNull Long refId, @NotNull String userName, @NotNull String type, @NotNull String description, @Nullable String data) {
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setId(businessWithNextGeneralKey.getNextGeneralId());
        auditTrail.setRefId(refId);
        auditTrail.setByWho(userName);
        auditTrail.setAtWhen(dateTimeConverter.getCurrentDateTimeAsString());
        auditTrail.setActivityType(type);
        auditTrail.setDescription(description);
        auditTrail.setData(data);
        return auditTrail;
    }

    /**
     * Checks if the user provided a dangerous text (from HTML perspective) into the text field.
     * If yes, logs the event and prohibits to move forward with the update/save action.
     *
     * @param text     is the text to be checked
     * @param userName is the person name who initiated the change
     */
    public void checkDangerousValue(@NotNull final String text, @NotNull final String userName) {
        Pattern p = Pattern.compile("[\\\\#&<>]");
        Matcher m = p.matcher(text);
        if (m.find()) {
            logger.warn("User: {} tried to use dangerous string value: {}", userName, text);
            throw new DatabaseHandlingException("Field content is not allowed.");
        }
    }

    /**
     * Just record a prepared audit trail, not critical so don't need to throw exception if it fails.
     *
     * @param auditTrail the record to be stored.
     */
    public void saveAuditTrailSafe(@NotNull AuditTrail auditTrail) {
        Session session = SessionFactoryHelper.getOpenedSession();
        try {
            session.beginTransaction();
            session.save(auditTrail);
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            session.getTransaction().rollback();
            session.close();
            logger.warn("Cannot save Audit record, issue: {}", e.getLocalizedMessage());
        }
    }

}
