package org.rockhill.adorApp.database.business;

import com.sun.istack.NotNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Coordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class BusinessWithCoordinator {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithCoordinator.class);

    public Long newCoordinator(@NotNull Coordinator newC, @NotNull AuditTrail auditTrail) {
        Long id = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.save(newC); //insert into table !
                session.save(auditTrail);
                session.getTransaction().commit();
                id = newC.getId();
                logger.info("Coordinator record created successfully: " + id.toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                logger.warn("Coordinator record creation failure", e);
            }
            session.close();
        }
        return id;
    }

    public List<Coordinator> getList() {
        List<Coordinator> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            result = (List<Coordinator>) session.createQuery("from Coordinator").list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public Coordinator getById(@NotNull Long id) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Coordinator as C where C.id = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            List<Coordinator> result = (List<Coordinator>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    public Long deleteCoordinator(@NotNull Coordinator coordinator, List<AuditTrail> auditTrailList) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                if (auditTrailList != null) {
                    for (AuditTrail auditTrail : auditTrailList) {
                        session.delete(auditTrail);
                    }
                }
                session.delete(coordinator);
                session.getTransaction().commit();
                session.close();
                logger.info("Coordinator deleted successfully: " + coordinator.getId().toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                logger.info("Coordinator delete failed: " + coordinator.getId().toString());
                throw e;
            }
        }
        return coordinator.getId();
    }

    public Long updateCoordinator(@NotNull Coordinator coordinator, @NotNull Collection<AuditTrail> auditTrailCollection) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.update(coordinator);
                for (AuditTrail auditTrail : auditTrailCollection) {
                    session.save(auditTrail);
                }
                session.getTransaction().commit();
                session.close();
                logger.info("Coordinator updated successfully: " + coordinator.getId().toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
        }
        return coordinator.getId();
    }

    /**
     * Returns with list of Coordinators, but only the main coordinators, so
     * - Spiritual Coo
     * - General coo
     * - Daily Coo
     * (so where the coordinatorType is > 23)
     *
     * @return with the list of such coordinators.
     */
    public List<Coordinator> getLeadership() {
        List<Coordinator> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            result = (List<Coordinator>) session.createQuery("from Coordinator as C where C.coordinatorType > 23 order by C.coordinatorType asc").list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public Coordinator getCoordinatorFromPersonId(@NotNull Long id) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Coordinator as C where C.personId = :expectedId order by C.coordinatorType desc";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            List<Coordinator> result = (List<Coordinator>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    private Coordinator getByCoordinatorType(@NotNull Integer i) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Coordinator as C where C.coordinatorType = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", i);
            List<Coordinator> result = (List<Coordinator>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    public Coordinator getDailyCooOfHour(@NotNull Integer coordinatorType) {
        if (coordinatorType > 23) return null;
        int dayPart = coordinatorType / 6;
        return getByCoordinatorType(24 + dayPart * 6);
    }

    public Coordinator getHourlyCooOfHour(@NotNull Integer hour) {
        return getByCoordinatorType(hour);
    }
}
