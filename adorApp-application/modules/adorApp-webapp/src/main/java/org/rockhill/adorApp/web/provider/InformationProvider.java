package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithLink;
import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.business.BusinessWithSocial;
import org.rockhill.adorApp.database.business.BusinessWithTranslator;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;
import org.rockhill.adorApp.web.json.CoordinatorJson;
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
    BusinessWithSocial businessWithSocial;
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
            int hourId = (cal.get(Calendar.DAY_OF_WEEK)-1) * 24 + cal.get(Calendar.HOUR_OF_DAY);  // use sun as 0 day
            informationJson.hourInDayNow = hourId % 24;
            informationJson.hourInDayNext = (hourId + 1) % 24;
            informationJson.currentHourList = businessWithLink.getLinksOfHour(hourId);
            informationJson.futureHourList = businessWithLink.getLinksOfHour((hourId + 1) % 168);
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

    public Object getGuestInformation(CurrentUserInformationJson currentUserInformationJson, HttpSession httpSession) {
        GuestInformationJson guestInformationJson = new GuestInformationJson();
        //get name and status
        Long socialId = currentUserInformationJson.socialId;
        Social social = businessWithSocial.getSocialById(socialId);
        if (social == null) {
            //wow, we should not be here
            logger.warn("Guest User got access to prohibited area: " + currentUserInformationJson.loggedInUserName);
            guestInformationJson.error = "access denied";
        } else {
            //we have social info
            if (social.getFacebookUserId().length() > 0) {
                guestInformationJson.isFacebook = true;
                guestInformationJson.emailFacebook = social.getFacebookEmail();
                guestInformationJson.nameFacebook = social.getFacebookUserName();
            } else {
                guestInformationJson.isFacebook = false;
            }
            if (social.getGoogleUserId().length() > 0) {
                guestInformationJson.isGoogle = true;
                guestInformationJson.emailGoogle = social.getGoogleEmail();
                guestInformationJson.nameGoogle = social.getGoogleUserName();
            } else {
                guestInformationJson.isGoogle = false;
            }
            switch (social.getSocialStatus()) {
                default:
                case 1: //waiting for identification
                    guestInformationJson.status = "Nem azonosított felhasználó, kérjük írja meg elérhetőségét a koordinátoroknak, a lentebb található Üzenetküldés segítségével, hogy az azonosítás megtörténhessen.";
                    break;
                case 2: //adoráló - we should be be here
                    guestInformationJson.status = "Regisztrált adoráló.";
                    break;
                case 3: //guest
                    guestInformationJson.status = "Vendég felhasználó.";
                    break;
            }
            guestInformationJson.leadership = coordinatorProvider.getLeadership(currentUserInformationJson);
            guestInformationJson.socialServiceUsed = currentUserInformationJson.socialServiceUsed;
        }
        return guestInformationJson;
    }
}

class GuestInformationJson {
    public String error; // filled only in case of error
    public String socialServiceUsed;
    public Boolean isGoogle;
    public String nameGoogle;
    public String emailGoogle;
    public Boolean isFacebook;
    public String nameFacebook;
    public String emailFacebook;
    public String status;   //status of the adorator
    public String id;   //id of the social record
    public List<CoordinatorJson> leadership; //main coordinators
}
