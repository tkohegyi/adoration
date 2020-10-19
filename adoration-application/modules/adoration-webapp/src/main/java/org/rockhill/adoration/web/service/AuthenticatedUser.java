package org.rockhill.adoration.web.service;

import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.database.tables.Social;

public class AuthenticatedUser {

    private Social social;
    private Person person;
    private final long sessionTimeoutExtender;
    private long sessionTimeout;
    private String serviceName;

    public AuthenticatedUser(String serviceName, Social social, Person person, Integer sessionTimeoutInSec) {
        this.serviceName = serviceName;
        this.social = social;
        this.person = person;
        this.sessionTimeoutExtender = (long)sessionTimeoutInSec * 1000;
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

    public String getServiceName() {
        return serviceName;
    }
}
