package org.rockhill.adorApp.web.service;

import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;

public class GoogleUser extends AuthenticatedUser {

    public GoogleUser(Social social, Person person, Integer sessionTimeoutInSec) {
        super(social, person, sessionTimeoutInSec);
    }

}
