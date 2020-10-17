package org.rockhill.adorApp.database.business;

import com.sun.istack.NotNull;
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
    private static final Integer MIN_HOUR = 0;
    private static final Integer MAX_HOUR = 167;

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

    public Link getLink(@NotNull Long id) {
        if (id == null) {
            throw new DatabaseHandlingException("Search for Link called with null id - contact to maintainers");
        }
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Link as L where L.id = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            List<Link> result = (List<Link>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        throw new DatabaseHandlingException("Search for Link failed with id:" + id.toString());
    }

    public String getDayNameFromHourId(@NotNull Integer hourId) {
        Integer day = Math.floorDiv(hourId.intValue(),24);
        String dayString = TranslatorDayNames.getTranslatedString(day);
        return dayString;
    }

    public String getHourFromHourId(@NotNull Integer hourId) {
        Integer hour = Math.floorMod(hourId.intValue(),24);
        return hour.toString();
    }

    public Long newLink(@NotNull Link l, @NotNull Collection<AuditTrail> auditTrailCollection) {
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

    public Long updateLink(@NotNull Link l, Collection<AuditTrail> auditTrailCollection) {
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

    public Long deleteLink(@NotNull Link l, @NotNull AuditTrail auditTrail) {
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

    public List<Link> getLinksOfPerson(@NotNull Person person) {
        if (person == null) {
            throw new DatabaseHandlingException("getLinksOfPerson called with null parameter - contact to maintainers");
        }
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Link as L where L.personId = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", person.getId());
            List<Link> result = (List<Link>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result;
            }
        }
        return null;
    }

    public List<Link> getLinksOfHour(int hourId) {
        List<Link> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            //get physical adorators ahead
            String hql = "from Link as L where L.hourId = :expectedId order by L.type asc";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", hourId);
            result = (List<Link>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public List<Link> getPhysicalLinksOfHour(@NotNull Integer hourId) {
        List<Link> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Link as L where L.hourId = :expectedId and L.type = 0";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", hourId);
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
    public List<Link> getLinksOfWeek(@NotNull Integer coordinatorType) {
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
            String hql = "from Link as L where L.hourId in :expectedHours and L.type = 0 order by L.hourId asc";
            Query query = session.createQuery(hql);
            query.setParameter("expectedHours", "( " + hours + " )");
            result = (List<Link>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public Integer getPreviousHour(@NotNull Integer hourId) {
        Integer previousHour;
        if (hourId > MIN_HOUR) {
            previousHour = hourId - 1;
        } else {
            previousHour = MAX_HOUR;
        }
        return previousHour;
    }

    public Integer getNextHour(@NotNull Integer hourId) {
        Integer nextHour;
        if (hourId < MAX_HOUR) {
            nextHour = hourId + 1;
        } else {
            nextHour = MIN_HOUR;
        }
        return nextHour;
    }

}
