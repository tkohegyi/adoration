package org.rockhill.adorApp.database.business;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.business.helper.NextGeneralKey;
import org.rockhill.adorApp.database.tables.Social;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessWithSocial {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithSocial.class);

    @Autowired
    NextGeneralKey nextGeneralKey;

    public Long newSocial(Social newS) {
        Long id = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            id = nextGeneralKey.getNextGeneralKay(session);
            logger.info("New sequence arrived:" + id.toString());
            newS.setId(id);
            session.save(newS); //insert into Social table !
            session.getTransaction().commit();
            session.close();
            logger.info("Social link created successfully: " + id.toString());
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

}
