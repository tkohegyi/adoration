package org.rockhill.adorApp.database.business;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adorApp.database.exception.DatabaseHandlingException;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
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
                id = l.getId();
                logger.info("Link created successfully: " + id.toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
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
                id = l.getId();
                logger.info("Link updated successfully: " + id.toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
        }
        return id;
    }

    public Long deleteLink(Link l, AuditTrail auditTrail) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.delete(l);
                session.save(auditTrail);
                session.getTransaction().commit();
                session.close();
                logger.info("Link deleted successfully: " + l.getId().toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
        }
        return l.getId();
    }

    public List<Link> getLinksOfPerson(Person person) {
        List<Link> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Link as L where L.personId = " + person.getId().toString();
            Query query = session.createQuery(hql);
            result = (List<Link>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result;
        }
        return null;
    }

    public List<Link> getLinksOfHour(int hourId) {
        List<Link> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Link as L where L.hourId = " + String.valueOf(hourId);
            Query query = session.createQuery(hql);
            result = (List<Link>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public List<Link> getPhysicalLinksOfHour(Integer hourId) {
        List<Link> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Link as L where L.hourId = " + String.valueOf(hourId) + " and L.type = 0";
            Query query = session.createQuery(hql);
            result = (List<Link>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    /**
     * Returns will the list of Links belong to the same hour of a week (so all links for same our of all days).
     * Only physical hours listed.
     *
     * @param coordinatorType
     * @return
     */
    public List<Link> getLinksOfWeek(Integer coordinatorType) {
        String hours = coordinatorType.toString() + ","
                + (24 + coordinatorType) + ","
                + (48 + coordinatorType) + ","
                + (72 + coordinatorType) + ","
                + (96 + coordinatorType) + ","
                + (120 + coordinatorType) + ","
                + (144 + coordinatorType);
        List<Link> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Link as L where L.hourId in (" + hours + " ) and L.type = 0 order by L.hourId asc";
            Query query = session.createQuery(hql);
            result = (List<Link>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }
}
