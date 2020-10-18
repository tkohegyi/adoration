package org.rockhill.adoration.database.business;

import com.sun.istack.NotNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adoration.database.SessionFactoryHelper;
import org.rockhill.adoration.database.tables.AuditTrail;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.database.tables.Social;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class BusinessWithPerson {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithPerson.class);

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

    public Person getPersonByName(@NotNull final String name) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Person as P where P.name = :expectedName";
            Query query = session.createQuery(hql);
            query.setParameter("expectedName", name);
            List<Person> result = (List<Person>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    public Long newPerson(@NotNull Person newP, @NotNull AuditTrail auditTrail) {
        Long id = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.save(newP);
                session.save(auditTrail);
                session.getTransaction().commit();
                session.close();
                id = newP.getId();
                logger.info("Person created successfully: " + id.toString() + " with name:" + newP.getName());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
        }
        return id;
    }

    public Person getPersonById(@NotNull final Long id) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Person as P where P.id = :expectedId";
            Query query = session.createQuery(hql);
            query.setParameter("expectedId", id);
            List<Person> result = (List<Person>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    public Person getPersonByEmail(@NotNull final String email) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Person as P where P.email like :email";
            Query query = session.createQuery(hql);
            query.setParameter("email", email);
            List<Person> result = (List<Person>) query.list();
            session.getTransaction().commit();
            session.close();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
    }

    public Long updatePerson(@NotNull Person person, @NotNull Collection<AuditTrail> auditTrailCollection) {
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                session.update(person);
                for (AuditTrail auditTrail : auditTrailCollection) {
                    session.save(auditTrail);
                }
                session.getTransaction().commit();
                session.close();
                logger.info("Person updated successfully: " + person.getId().toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                throw e;
            }
        }
        return person.getId();
    }

    public Long deletePerson(@NotNull Person person, List<Social> socialList, List<Link> linkList, List<AuditTrail> auditTrailList) {
        //the huge method of deleting an activity from DB.
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            try {
                session.beginTransaction();
                if (socialList != null) {
                    for (Social social : socialList) {
                        session.update(social); //this is just update, the rest is delete
                    }
                }
                if (linkList != null) {
                    for (Link link : linkList) {
                        session.delete(link);
                    }
                }
                if (auditTrailList != null) {
                    for (AuditTrail auditTrail : auditTrailList) {
                        session.delete(auditTrail);
                    }
                }
                session.delete(person);
                session.getTransaction().commit();
                session.close();
                logger.info("Person deleted successfully: " + person.getId().toString());
            } catch (Exception e) {
                session.getTransaction().rollback();
                session.close();
                logger.info("Person delete failed: " + person.getId().toString());
                throw e;
            }
        }
        return person.getId();
    }

}
