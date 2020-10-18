package org.rockhill.adoration.database.business.helper;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;

@Component
public class NextGeneralKey {

    /**
     * Generates the next general key within Adoration database. Must be called from within a transaction.
     *
     * @return with the next key
     */
    public Long getNextGeneralKay(Session session) {
        {
            //MS SQL solution is:
            // Iterator<BigInteger> iter;
            // Query query = session.createSQLQuery( "SELECT NEXT VALUE FOR dbo.AdorationUniqueNumber");
            // iter = (Iterator<BigInteger>) query.getResultList();
            // iter.next().longValue();
            ArrayList<BigInteger> values;
            Query query = session.createSQLQuery( "select nextval('\"dbo\".\"AdorationUniqueNumber\"')");
            values = (ArrayList<BigInteger>) query.getResultList();
            return values.get(0).longValue();
        }
    }

}
