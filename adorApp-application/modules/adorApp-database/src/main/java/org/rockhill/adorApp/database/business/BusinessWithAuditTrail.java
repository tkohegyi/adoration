package org.rockhill.adorApp.database.business;

import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.business.helper.Converter;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessWithAuditTrail {

    @Autowired
    BusinessWithNextGeneralKey businessWithNextGeneralKey;

    public List<AuditTrail> getAuditTrailOfObject(Long id) {
        List<AuditTrail> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from AuditTrail as AT where AT.refId = :refId order by AT.id ASC";
            Query query = session.createQuery(hql);
            query.setParameter("refId", id);
            result = (List<AuditTrail>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public AuditTrail prepareAuditTrail(Long refId, String userName, String type, String description) {
        Converter converter = new Converter();
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setId(businessWithNextGeneralKey.getNextGeneralId());
        auditTrail.setRefId(refId);
        auditTrail.setByWho(userName);
        auditTrail.setAtWhen(converter.getCurrentDateTimeAsString());
        auditTrail.setActivityType(type);
        auditTrail.setDescription(description);
        return auditTrail;
    }

}
