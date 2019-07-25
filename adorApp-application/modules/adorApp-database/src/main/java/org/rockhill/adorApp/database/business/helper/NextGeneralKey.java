package org.rockhill.adorApp.database.business.helper;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Iterator;

@Component
public class NextGeneralKey {

    /**
     * Generates the next general key within Adoration database. Must be called from within a transaction.
     *
     * @return with the next key
     */
    public Long getNextGeneralKay(Session session) {
        {
            Iterator<BigInteger> iter;
            Query query = session.createSQLQuery( "SELECT NEXT VALUE FOR dbo.AdorationUniqueNumber");
            iter = (Iterator<BigInteger>) query.list().iterator();
            return iter.next().longValue();
        }
    }

}
