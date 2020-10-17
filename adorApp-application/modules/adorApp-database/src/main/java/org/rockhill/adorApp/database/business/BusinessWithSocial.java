package org.rockhill.adorApp.database.business;

import com.sun.istack.NotNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class BusinessWithSocial {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithSocial.class);

    public Long newSocial(@NotNull Social newS, @NotNull AuditTrail auditTrail) {
        Long id = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.save(newS); //insert into Social table !
                session.save(auditTrail);
                session.getTransaction().commit();
                id = newS.getId();
                logger.info("Social record created successfully: " + id.toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                logger.warn("Social record creation failure", e);
            }
            session.close();
        }
        return id;
    }

    //GOOGLE METHODS ===================================================================================================
    public Social getSocialByGUserId(@NotNull final String googleUserId) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.googleUserId like :likeValue";
            Query query = session.createQuery(hql);
            query.setParameter("likeValue", googleUserId);
            List<Social> result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    //FACEBOOK METHODS =================================================================================================
    public Social getSocialByFUserId(@NotNull final String facebookUserId) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.facebookUserId like :likeValue";
            Query query = session.createQuery(hql);
            query.setParameter("likeValue", facebookUserId);
            List<Social> result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    public List<Social> getSocialsOfPerson(@NotNull Person person) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.personId = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", person.getId());
            List<Social> result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result;
            }
        }
        return null;
    }

    public List<Social> getSocialList() {
        List<Social> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            result = (List<Social>) session.createQuery("from Social").list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public Social getSocialById(@NotNull Long id) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.id = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            List<Social> result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    public Long deleteSocial(@NotNull Social social, List<AuditTrail> auditTrailList) {
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
                session.delete(social);
                session.getTransaction().commit();
                session.close();
                logger.info("Social deleted successfully: " + social.getId().toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                logger.info("Social delete failed: " + social.getId().toString());
                throw e;
            }
        }
        return social.getId();
    }

    public Long updateSocial(@NotNull Social social, Collection<AuditTrail> auditTrailCollection) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.update(social);
                for (AuditTrail auditTrail : auditTrailCollection) {
                    session.save(auditTrail);
                }
                session.getTransaction().commit();
                session.close();
                logger.info("Social updated successfully: " + social.getId().toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
        }
        return social.getId();
    }

}
