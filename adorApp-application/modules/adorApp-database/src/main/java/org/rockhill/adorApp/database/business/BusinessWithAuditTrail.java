package org.rockhill.adorApp.database.business;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.business.helper.Converter;
import org.rockhill.adorApp.database.exception.DatabaseHandlingException;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BusinessWithAuditTrail {

    private final Logger logger = LoggerFactory.getLogger(BusinessWithAuditTrail.class);

    @Autowired
    BusinessWithNextGeneralKey businessWithNextGeneralKey;

    public List<AuditTrail> getAuditTrailOfObject(@NotNull Long id) {
        if (id == null) {
            throw new DatabaseHandlingException("Tried to get audit trail for a null id, pls contact to maintainers");
        }
        List<AuditTrail> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from AuditTrail as AT where AT.refId = :expectedId OR AT.activityType like :likeValue order by AT.atWhen ASC";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            query.setParameter("likeValue", "'%:" + id.toString() + "'");
            result = (List<AuditTrail>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public AuditTrail prepareAuditTrail(@NotNull Long refId, @NotNull String userName, @NotNull String type, @NotNull String description, @Nullable String data) {
        Converter converter = new Converter();
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setId(businessWithNextGeneralKey.getNextGeneralId());
        auditTrail.setRefId(refId);
        auditTrail.setByWho(userName);
        auditTrail.setAtWhen(converter.getCurrentDateTimeAsString());
        auditTrail.setActivityType(type);
        auditTrail.setDescription(description);
        auditTrail.setData(data);
        return auditTrail;
    }

    public void checkDangerousValue(@NotNull final String text, @NotNull final String userName) {
        Pattern p = Pattern.compile("[\\\\#&<>]");
        Matcher m = p.matcher(text);
        if (m.find()) {
            logger.warn("User:" + userName + " tried to use dangerous string value:" + text);
            throw new DatabaseHandlingException("Field content is not allowed.");
        }
    }

    /**
     * Just record a prepared audit trail, not critical so don't need to throw exception if it fails.
     *
     * @param auditTrail the record to be stored.
     */
    public void saveAuditTrailSafe(@NotNull AuditTrail auditTrail) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.save(auditTrail);
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                logger.warn("Cannot save Audit record, issue: " + e.getLocalizedMessage());
            }
        }
    }

}
