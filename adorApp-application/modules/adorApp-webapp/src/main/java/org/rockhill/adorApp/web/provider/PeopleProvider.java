package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.PersonInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PeopleProvider {

    private final Logger logger = LoggerFactory.getLogger(PeopleProvider.class);


    @Autowired
    BusinessWithPerson businessWithPerson;

    public Object getPersonListAsObject() {
        List<Person> people = businessWithPerson.getPersonList();
        return people;
    }

    public Object getPersonAsObject(final Long id) {
        Person p = businessWithPerson.getPersonById(id);
        return p;
    }

    public Long updatePerson(PersonInformationJson p, CurrentUserInformationJson currentUserInformationJson) {
        //TODO
        return null;
    }
}
