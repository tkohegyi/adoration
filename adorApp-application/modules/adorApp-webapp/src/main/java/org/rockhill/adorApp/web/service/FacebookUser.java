package org.rockhill.adorApp.web.service;

import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;

public class FacebookUser extends AuthenticatedUser {

    public FacebookUser(Social social, Person person, Integer sessionTimeoutInSec) {
        super(social, person, sessionTimeoutInSec);
    }

}
