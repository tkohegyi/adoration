package org.rockhill.adoration.web.service;

import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.database.tables.Social;

/**
 * Facebook type of AuthenticatedUser.
 */
public class FacebookUser extends AuthenticatedUser {

    /**
     * Creates a Facebook User login class.
     *
     * @param social              is the associated Social login record.
     * @param person              is the associated Person record (real life person) if exists
     * @param sessionTimeoutInSec determines the validity of he session
     */
    public FacebookUser(Social social, Person person, Integer sessionTimeoutInSec) {
        super("Facebook", social, person, sessionTimeoutInSec);
    }

}
