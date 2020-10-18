package org.rockhill.adoration.web.service;

import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.database.tables.Social;

public class FacebookUser extends AuthenticatedUser {

    public FacebookUser(Social social, Person person, Integer sessionTimeoutInSec) {
        super(social, person, sessionTimeoutInSec);
    }

}
