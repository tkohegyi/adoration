package org.rockhill.adorApp.database.business;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.business.helper.NextGeneralKey;
import org.rockhill.adorApp.database.tables.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BusinessWithPerson {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithPerson.class);

    @Autowired
    NextGeneralKey nextGeneralKey;

    public Map<Long, Person> getPersonMap() {
        Map<Long, Person> personMap = new HashMap<>();
        for (Person person : getPersonList()) {
            personMap.put(person.getId(), person);
        }
        return personMap;
    }


    public List<Person> getPersonList() {
        List<Person> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            result = (List<Person>) session.createQuery("from Person").list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public Person getPersonByName(final String name) {
        List<Person> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Person as P where P.name = :expectedName";
            Query query = session.createQuery(hql);
            query.setParameter("expectedName", name);
            result = (List<Person>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public Long newPerson(Person newP) {
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
            logger.info("Person created successfully: " + id.toString() + " with name:" + newP.getName());
        }
        return id;
    }

    public Person getPersonById(final Long id) {
        List<Person> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Person as P where P.id = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            result = (List<Person>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public Person getPersonByEmail(final String email) {
        List<Person> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Person as P where P.email like :email";
            Query query = session.createQuery(hql);
            query.setParameter("email", email);
            result = (List<Person>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }
}
