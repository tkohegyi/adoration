package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithLink;
import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.business.BusinessWithTranslator;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.InformationJson;
import org.rockhill.adorApp.web.json.PersonJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    BusinessWithTranslator businessWithTranslator;
    @Autowired
    CoordinatorProvider coordinatorProvider;

    public Object getInformation(CurrentUserInformationJson currentUserInformationJson, HttpSession httpSession) {
        InformationJson informationJson = new InformationJson();
        //get name and status
        Long personId = currentUserInformationJson.personId;
        Person person = businessWithPerson.getPersonById(personId);
        if (person == null) {
            //wow, we should not be here
            logger.warn("User got access to prohibited area: " + currentUserInformationJson.loggedInUserName);
            informationJson.error = "access denied";
        } else {
            //we have person info
            informationJson.name = person.getName();
            informationJson.status = AdoratorStatusTypes.getTranslatedString(person.getAdorationStatus());
            informationJson.id = person.getId().toString();
            informationJson.linkList = businessWithLink.getLinksOfPerson(person);
            informationJson.leadership = coordinatorProvider.getLeadership(currentUserInformationJson);
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(0); //ensure that Sunday is the first day of the week
            int hourId = cal.get(Calendar.DAY_OF_WEEK) * 24 + cal.get(Calendar.HOUR_OF_DAY);
            informationJson.hourInDayNow = hourId % 24;
            informationJson.hourInDayNext = (hourId + 1) % 24;
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
                    relatedPersonList.add(new PersonJson(p,currentUserInformationJson.isPrivilegedUser()));
                } else {
                    logger.warn("Person ID usage found without real Person, id: " + id.toString());
                }
            }
            informationJson.relatedPersonList = relatedPersonList;
            //fill the day names first
            informationJson.dayNames = new HashMap<>();
            for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
                String textId = dayName.toString();
                String value = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode, textId, textId);
                informationJson.dayNames.put(dayName.getDayValue(), value);
            }

        }
        return informationJson;
    }
}
