package org.rockhill.adorApp.web.service;

import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;

public class GoogleUser implements AuthenticatedUser {
    private Social social;
    private Person person;

    public GoogleUser(Social social, Person person) {
        this.social = social;
        this.person = person;
    }

    public Social getSocial() {
        return social;
    }

    public void setSocial(Social social) {
        this.social = social;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
