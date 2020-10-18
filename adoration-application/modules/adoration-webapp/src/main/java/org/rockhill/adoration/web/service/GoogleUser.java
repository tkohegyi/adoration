package org.rockhill.adoration.web.service;

import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.database.tables.Social;

public class GoogleUser extends AuthenticatedUser {

    public GoogleUser(Social social, Person person, Integer sessionTimeoutInSec) {
        super(social, person, sessionTimeoutInSec);
    }

}
