package org.rockhill.adorApp.database.business;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adorApp.database.SessionFactoryHelper;
import org.rockhill.adorApp.database.business.helper.Converter;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessWithLink {

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

}
