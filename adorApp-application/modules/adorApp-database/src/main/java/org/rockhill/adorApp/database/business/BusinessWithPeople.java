package org.rockhill.adorApp.database.business;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.business.helper.NextGeneralKey;
import org.rockhill.adorApp.database.tables.People;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BusinessWithPeople {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithPeople.class);

    @Autowired
    NextGeneralKey nextGeneralKey;

    public Map<Long, People> getPersonMap() {
        Map<Long, People> personMap = new HashMap<>();
        for (People people : getPersonList()) {
            personMap.put(people.getId(), people);
        }
        return personMap;
    }


    public List<People> getPersonList() {
        List<People> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            result = (List<People>) session.createQuery("from People").list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public People getPersonByName(final String name) {
        List<People> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from People as P where P.name = :expectedName";
            Query query = session.createQuery(hql);
            query.setParameter("expectedName", name);
            result = (List<People>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public Long newPerson(People newP) {
        Long id = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            id = nextGeneralKey.getNextGeneralKay(session);
            logger.info("New sequence arrived:" + id.toString());
            newP.setId(id);
            session.save(newP); //insert into People table !
            session.getTransaction().commit();
            session.close();
            logger.info("People created successfully: " + id.toString() + " with name:" + newP.getName());
        }
        return id;
    }

    public People getPersonById(final Long id) {
        List<People> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from People as P where P.id = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            result = (List<People>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public People getPersonByEmail(final String email) {
        List<People> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from People as P where P.email like :email";
            Query query = session.createQuery(hql);
            query.setParameter("email", email);
            result = (List<People>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }
}
