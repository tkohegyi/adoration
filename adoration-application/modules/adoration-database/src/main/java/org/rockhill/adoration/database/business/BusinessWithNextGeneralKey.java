package org.rockhill.adoration.database.business;

import org.hibernate.Session;
import org.rockhill.adoration.database.SessionFactoryHelper;
import org.rockhill.adoration.database.business.helper.NextGeneralKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessWithNextGeneralKey {
    private final Logger logger = LoggerFactory.getLogger(BusinessWithNextGeneralKey.class);

    @Autowired
    NextGeneralKey nextGeneralKey;

    public Long getNextGeneralId() {
        Long id;
        Session session = SessionFactoryHelper.getOpenedSession();
        session.beginTransaction();
        id = nextGeneralKey.getNextGeneralKay(session);
        logger.debug("New sequence arrived:" + id.toString());
        session.getTransaction().commit();
        session.close();
        return id;
    }

}
