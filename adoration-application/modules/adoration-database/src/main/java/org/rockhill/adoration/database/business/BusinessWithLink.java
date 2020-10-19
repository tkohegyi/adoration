package org.rockhill.adoration.database.business;

import com.sun.istack.NotNull;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.rockhill.adoration.database.SessionFactoryHelper;
import org.rockhill.adoration.database.business.helper.BusinessBase;
import org.rockhill.adoration.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adoration.database.exception.DatabaseHandlingException;
import org.rockhill.adoration.database.tables.AuditTrail;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.database.tables.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class BusinessWithLink extends BusinessBase {
    private static final Integer MIN_HOUR = 0;
    private static final Integer MAX_HOUR = 167;
    private final Logger logger = LoggerFactory.getLogger(BusinessWithLink.class);

    public boolean isValidHour(Integer hour) {
        return hour >= MIN_HOUR && hour <= MAX_HOUR;
    }

    public List<Link> getLinkList() {
        List<Link> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        result = (List<Link>) session.createQuery("from Link").list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public Link getLink(@NotNull Long id) {
        if (id == null) {
            throw new DatabaseHandlingException("Search for Link called with null id - contact to maintainers");
        }
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Link as L where L.id = :" + EXPECTED_ID;
        Query query = session.createQuery(hql);
        query.setParameter(EXPECTED_ID, id);
        List<Link> result = (List<Link>) query.list();
        session.getTransaction().commit();
        session.close();
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new DatabaseHandlingException("Search for Link failed with id:" + id.toString());
    }

    public String getDayNameFromHourId(@NotNull Integer hourId) {
        Integer day = Math.floorDiv(hourId.intValue(), 24);
        String dayString = TranslatorDayNames.getTranslatedString(day);
        return dayString;
    }

    public String getHourFromHourId(@NotNull Integer hourId) {
        Integer hour = Math.floorMod(hourId.intValue(), 24);
        return hour.toString();
    }

    public Long newLink(@NotNull Link l, @NotNull Collection<AuditTrail> auditTrailCollection) {
        Long id;
        Session session = SessionFactoryHelper.getOpenedSession();
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
        return id;
    }

    public Long updateLink(@NotNull Link l, Collection<AuditTrail> auditTrailCollection) {
        Long id;
        Session session = SessionFactoryHelper.getOpenedSession();
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
        return id;
    }

    public Long deleteLink(@NotNull Link l, @NotNull AuditTrail auditTrail) {
        Session session = SessionFactoryHelper.getOpenedSession();
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
        return l.getId();
    }

    public List<Link> getLinksOfPerson(@NotNull Person person) {
        if (person == null) {
            throw new DatabaseHandlingException("getLinksOfPerson called with null parameter - contact to maintainers");
        }
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Link as L where L.personId = :" + EXPECTED_ID;
        Query query = session.createQuery(hql);
        query.setParameter(EXPECTED_ID, person.getId());
        List<Link> result = (List<Link>) query.list();
        session.getTransaction().commit();
        session.close();
        if (result != null && result.size() > 0) {
            return result;
        }
        return null;
    }

    public List<Link> getLinksOfHour(int hourId) {
        List<Link> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        //get physical adorators ahead
        String hql = "from Link as L where L.hourId = :" + EXPECTED_ID + " order by L.type asc";
        Query query = session.createQuery(hql);
        query.setParameter(EXPECTED_ID, hourId);
        result = (List<Link>) query.list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public List<Link> getPhysicalLinksOfHour(@NotNull Integer hourId) {
        List<Link> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Link as L where L.hourId = :" + EXPECTED_ID + " and L.type = 0";
        Query query = session.createQuery(hql);
        query.setParameter(EXPECTED_ID, hourId);
        result = (List<Link>) query.list();
        session.getTransaction().commit();
        session.close();
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
        List<Link> result;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        String hql = "from Link as L where L.hourId in :expectedHours and L.type = 0 order by L.hourId asc";
        Query query = session.createQuery(hql);
        query.setParameter("expectedHours", "( " + hours + " )");
        result = (List<Link>) query.list();
        session.getTransaction().commit();
        session.close();
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
