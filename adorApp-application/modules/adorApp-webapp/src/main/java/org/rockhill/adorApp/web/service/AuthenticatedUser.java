package org.rockhill.adorApp.web.service;

import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;

public class AuthenticatedUser {

    private Social social;
    private Person person;
    private long sessionTimeoutExtender;
    private long sessionTimeout;

    public AuthenticatedUser(Social social, Person person, Integer sessionTimeoutInSec) {
        this.social = social;
        this.person = person;
        this.sessionTimeoutExtender = sessionTimeoutInSec *1000;
        extendSessionTimeout();
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

    public void extendSessionTimeout() {
        this.sessionTimeout = System.currentTimeMillis() + sessionTimeoutExtender;
    }

    public Boolean isSessionValid() {
        return this.sessionTimeout > System.currentTimeMillis();
    }
}
