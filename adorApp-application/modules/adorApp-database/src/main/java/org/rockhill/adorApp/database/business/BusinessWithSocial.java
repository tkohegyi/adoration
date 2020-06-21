package org.rockhill.adorApp.database.business;

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

import java.util.List;

@Component
public class BusinessWithSocial {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithSocial.class);

    public Long newSocial(Social newS, AuditTrail auditTrail) {
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
    public Social getSocialByGUserId(final String googleUserId) {
        List<Social> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.googleUserId like '" + googleUserId + "'";
            Query query = session.createQuery(hql);
            result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public Social getSocialByGEmail(final String gEmail) {
        List<Social> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.gEmail like :expectedGEmail";
            Query query = session.createQuery(hql);
            query.setParameter("expectedGEmail", gEmail);
            result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    //FACEBOOK METHODS =================================================================================================
    public Social getSocialByFUserId(final String facebookUserId) {
        List<Social> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.facebookUserId like '" + facebookUserId + "'";
            Query query = session.createQuery(hql);
            result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public List<Social> getSocialsOfPerson(Person person) {
        List<Social> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.personId = " + person.getId().toString();
            Query query = session.createQuery(hql);
            result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result;
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

    public Social getSocialById(Long id) {
        List<Social> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Social as S where S.id = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            result = (List<Social>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public Long deleteSocial(Social social, List<AuditTrail> auditTrailList) {
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
}
