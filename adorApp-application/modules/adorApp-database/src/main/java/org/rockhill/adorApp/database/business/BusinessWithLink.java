package org.rockhill.adorApp.database.business;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.business.helper.Converter;
import org.rockhill.adorApp.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adorApp.database.exception.DatabaseHandlingException;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class BusinessWithLink {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithLink.class);

    public List<Link> getLinkList() {
        List<Link> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            result = (List<Link>) session.createQuery("from Link").list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public Link getLink(Long id) {
        List<Link> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Link as L where L.id = " + id.toString();
            result = (List<Link>) session.createQuery(hql).list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        throw new DatabaseHandlingException("Search for Link failed with id:" + id.toString());
    }

    public String getDayNameFromHourId(Integer hourId) {
        Integer day = Math.floorDiv(hourId.intValue(),24);
        String dayString = TranslatorDayNames.getTranslatedString(day);
        return dayString;
    }

    public String getHourFromHourId(Integer hourId) {
        Integer hour = Math.floorMod(hourId.intValue(),24);
        return hour.toString();
    }

    public Long newLink(Link l, Collection<AuditTrail> auditTrailCollection) {
        Long id = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.save(l);
                for (AuditTrail auditTrail : auditTrailCollection) {
                    session.save(auditTrail);
                }
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
            id = l.getId();
            logger.info("Account Activity created successfully: " + id.toString());
        }
        return id;
    }

    public Long updateLink(Link l, Collection<AuditTrail> auditTrailCollection) {
        Long id = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.update(l);
                for (AuditTrail auditTrail : auditTrailCollection) {
                    session.save(auditTrail);
                }
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
            id = l.getId();
            logger.info("Account Activity created successfully: " + id.toString());
        }
        return id;
    }
}
