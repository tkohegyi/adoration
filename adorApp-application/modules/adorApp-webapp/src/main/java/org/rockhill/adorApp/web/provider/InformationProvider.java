package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithAuditTrail;
import org.rockhill.adorApp.database.business.BusinessWithLink;
import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.business.BusinessWithSocial;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.business.helper.enums.SocialStatusTypes;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.DeleteEntityJson;
import org.rockhill.adorApp.web.json.InformationJson;
import org.rockhill.adorApp.web.json.PersonJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.*;

@Component
public class InformationProvider {

    private final Logger logger = LoggerFactory.getLogger(InformationProvider.class);

    @Autowired
    BusinessWithPerson businessWithPerson;
    @Autowired
    BusinessWithLink businessWithLink;

    public Object getInformation(CurrentUserInformationJson currentUserProvider, HttpSession httpSession) {
        InformationJson informationJson = new InformationJson();
        //get name and status
        Long personId = currentUserProvider.personId;
        Person person = businessWithPerson.getPersonById(personId);
        if (person == null) {
            //wow, we should not be here
            logger.warn("User got access to prohibited area: " + currentUserProvider.loggedInUserName);
            informationJson.error = "access denied";
        } else {
            //we have person info
            informationJson.name = person.getName();
            informationJson.status = AdoratorStatusTypes.getTranslatedString(person.getAdorationStatus());
            informationJson.linkList = businessWithLink.getLinksOfPerson(person);
            List<Person> leadership = businessWithPerson.getLeadership();
            List<PersonJson> leadershipPerson = new LinkedList<>();
            for (Person p: leadership) {
                leadershipPerson.add(new PersonJson(p, currentUserProvider.isPrivilegedUser()));
            }
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(0); //ensure that Sunday is the first day of the week
            int hourId = cal.get(Calendar.DAY_OF_WEEK) * 24 + cal.get(Calendar.HOUR_OF_DAY);
            informationJson.currentHourList = businessWithLink.getLinksOfHour(hourId);
            informationJson.futureHourList = businessWithLink.getLinksOfHour(hourId + 1);
            Set<Long> personIds = new HashSet<>();
            if (informationJson.linkList != null) {
                for (Link l: informationJson.linkList) {
                    personIds.add(l.getPersonId());
                }
            }
            if (informationJson.currentHourList != null) {
                for (Link l: informationJson.currentHourList) {
                    personIds.add(l.getPersonId());
                }
            }
            if (informationJson.futureHourList != null) {
                for (Link l: informationJson.futureHourList) {
                    personIds.add(l.getPersonId());
                }
            }
            Iterator<Long> ppl = personIds.iterator();
            List<PersonJson> relatedPersonList = new LinkedList<>();
            while (ppl.hasNext()) {
                Long id = ppl.next();
                Person p = businessWithPerson.getPersonById(id);
                if (p != null) {
                    relatedPersonList.add(new PersonJson(p,currentUserProvider.isPrivilegedUser()));
                } else {
                    logger.warn("Person ID usage found without real Person, id: " + id.toString());
                }
            }
            informationJson.relatedPersonList = relatedPersonList;
        }
        return informationJson;
    }
}
